package dev.dereli.algo.kotlin

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Файл: 12_coroutines_basics.kt
 * Тема: Основы корутин, Структурированная конкурентность, Диспетчеры, Отмена.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: COROUTINES
 * =========================================================================================
 *
 * 1. Что такое Корутина?
 *    - Это "легковесный поток", управляемый рантаймом Kotlin, а не операционной системой.
 *    - Тысячи корутин могут работать на одном реальном потоке.
 *    - Главное отличие от потока: корутина не "блокирует" поток, а "приостанавливается" (suspend).
 *      В момент приостановки поток ОС освобождается и может выполнять другую работу.
 *
 * 2. `suspend` — Ключевое слово Магии:
 *    - `suspend`-функция — это функция, которая может быть приостановлена и возобновлена позже.
 *    - Вызывать `suspend`-функцию можно только из другой `suspend`-функции или из корутин-билдера (`launch`, `async`).
 *
 * 3. Structured Concurrency (Структурированная конкурентность) — Главный принцип:
 *    - Корутины существуют в иерархии (родитель-ребенок), привязанной к `CoroutineScope`.
 *    - Правила:
 *      a) Если Scope отменяется, все его дочерние корутины тоже отменяются.
 *      b) Если дочерняя корутина падает с исключением, она "убивает" родителя (и всех братьев).
 *      c) Родительская корутина не завершится, пока не завершатся все её дети.
 *    - Это решает 99% проблем с утечками памяти и "зомби"-задачами.
 *
 * 4. `Job` и `CoroutineScope`:
 *    - `Job`: "Ручка" управления корутиной (`.start()`, `.cancel()`, `.join()`).
 *    - `CoroutineScope`: Контекст, в котором запускаются корутины. Он "знает" о всех своих детях.
 *      В Android у `ViewModel` есть `viewModelScope`, у `Activity` — `lifecycleScope`.
 *
 * 5. `Dispatchers` — Где выполнять код:
 *    - `Dispatchers.Main`: UI-поток (только в Android/JavaFX).
 *    - `Dispatchers.IO`: Для блокирующих операций (сеть, диск, БД). Использует пул потоков.
 *    - `Dispatchers.Default`: Для тяжелых вычислений (CPU-bound), (сортировка, JSON-парсинг). Пул равен числу ядер CPU.
 *    - `withContext(Dispatcher)` — идиоматичный способ переключить поток внутри корутины.
 *
 * 6. Cancellation (Отмена) — Кооперативность:
 *    - Вызов `job.cancel()` не убивает корутину. Он просто выставляет флаг `isActive = false`.
 *    - Корутина ОБЯЗАНА сама проверять этот флаг (например, `while(isActive)`).
 *    - Все `suspend`-функции из `kotlinx.coroutines` (например, `delay`) делают это автоматически.
 * =========================================================================================
 */
fun main() = runBlocking {
    // runBlocking блокирует поток main, пока не выполнятся все корутины внутри.
    // Используется ТОЛЬКО в main() и тестах. В Android коде его быть не должно.

    println("--- Launch & Join ---")
    demoLaunchJoin()

    println("\n--- Async & Await ---")
    demoAsyncAwait()

    println("\n--- Dispatchers & withContext ---")
    demoDispatchersAndContext()

    println("\n--- Cancellation (Cooperative) ---")
    demoCancellation()

    println("\n--- Exception Handling (Supervisor) ---")
    demoExceptionHandling()
}

/*
 * Launch: помнить
 * - Builder: launch { ... }
 * - Возвращает: Job (управление корутиной: join, cancel).
 * - Результат: Не возвращает значения (Fire and forget).
 * - Исключения: Если падает, то крашит весь Scope (если не Supervisor).
 */
private suspend fun demoLaunchJoin() = coroutineScope {
    val job = launch {
        delay(500)
        println("  Launch: World!")
    }
    print("  Launch: Hello, ")
    job.join() // Suspend point: ждем завершения job
    println("Done.")
}

/*
 * Async: помнить
 * - Builder: async { ... }
 * - Возвращает: Deferred<T> (наследник Job, но с результатом).
 * - Результат: .await() возвращает T.
 * - Используется для параллельных вычислений.
 */
private suspend fun demoAsyncAwait() = coroutineScope {
    val time = measureTimeMillis {
        // Запускаем две задачи ОДНОВРЕМЕННО
        val deferred1 = async { heavyCalculation(1) }
        val deferred2 = async { heavyCalculation(2) }

        println("  Async: Waiting for results...")
        // await() приостанавливает нас, пока результат не будет готов
        val sum = deferred1.await() + deferred2.await()
        println("  Async: Sum = $sum")
    }
    println("  Async: Completed in $time ms (expected ~500ms, not 1000ms)")
}

private suspend fun heavyCalculation(num: Int): Int {
    delay(500) // имитация работы
    return num * 10
}

/*
 * Dispatchers & Context: помнить
 * - Context = набор элементов (Job + Dispatcher + Name + Handler).
 * - Можно комбинировать через `+`.
 * - withContext(Dispatchers.IO) — стандартный способ переключить поток внутри корутины (например, для запроса в БД).
 */
private suspend fun demoDispatchersAndContext() = coroutineScope {
    // Комбинируем контекст: Имя + Диспетчер
    launch(Dispatchers.Default + CoroutineName("MyWorker")) {
        val name = coroutineContext[CoroutineName]?.name
        println("  Start in thread: ${Thread.currentThread().name}, name: $name")

        // Переключаемся на IO для блокирующей операции (БД, Сеть)
        val result = withContext(Dispatchers.IO) {
            println("  Working in IO: ${Thread.currentThread().name}")
            "Data from DB"
        }

        println("  Back to Default: ${Thread.currentThread().name}, got: $result")
    }
}

/*
 * Cancellation: помнить !!!
 * Отмена КООПЕРАТИВНА.
 * Если корутина выполняет CPU-работу (цикл) и не проверяет isActive / ensureActive(),
 * вызов job.cancel() просто выставит флаг, но корутина продолжит работать.
 *
 * delay() и другие suspend-функции проверяют отмену автоматически.
 */
private suspend fun demoCancellation() = coroutineScope {
    val job = launch(Dispatchers.Default) {
        var i = 0
        val startTime = System.currentTimeMillis()
        // ОШИБКА: while (i < 5) { ... } — этот цикл не остановить!
        // ПРАВИЛЬНО: while (isActive) или добавление ensureActive()
        while (isActive) {
            if (System.currentTimeMillis() >= startTime + 10) { // Каждые 10 мс
                println("  I'm sleeping $i ...")
                i++
                // ensureActive() // тоже вариант проверки
            }
        }
        println("  Job: I am cancelled!") // Этот код может выполниться в блоке finally
    }

    delay(30)
    println("  Main: Tired of waiting. Cancel!")
    job.cancelAndJoin() // Отменяем и ждем завершения
    println("  Main: Now I can quit.")
}

/*
 * Exception Handling & Supervisor: помнить
 * - Обычный Job/coroutineScope: Если падает ребенок -> падает родитель -> падают остальные дети. (Принцип атомарности).
 * - SupervisorJob / supervisorScope: Если падает ребенок -> родитель НЕ падает, другие дети работают.
 *
 * Используем SupervisorScope, когда задачи независимы (например, загрузка 3-х разных картинок).
 */
private suspend fun demoExceptionHandling() {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("  Handler caught: ${exception.message}")
    }

    println("  Start SupervisorScope...")
    supervisorScope {
        // Ребенок 1: Падает
        launch(handler) { // handler нужен, чтобы логгировать ошибку, иначе краш в консоль
            println("  Child 1 fails")
            throw RuntimeException("Boom!")
        }

        // Ребенок 2: Работает дальше, несмотря на смерть брата
        launch {
            delay(100)
            println("  Child 2 finished (Survived!)")
        }
    }
    println("  SupervisorScope finished")
}
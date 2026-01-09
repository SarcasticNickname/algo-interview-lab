package dev.dereli.algo.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Файл: 13_flow_basics.kt
 * Тема: Asynchronous Flow, Реактивные потоки, Cold vs Hot Streams.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: REACTIVE STREAMS (FLOW)
 * =========================================================================================
 *
 * 1. Что такое Flow и зачем он нужен?
 *    - Flow — это реализация паттерна "Reactive Streams" (Реактивные потоки) на корутинах.
 *    - Он позволяет работать с ПОСЛЕДОВАТЕЛЬНОСТЬЮ асинхронных данных.
 *    - `suspend fun` возвращает ОДНО значение. `Flow` возвращает МНОГО значений (0..N).
 *    - Это замена `RxJava` в современном Kotlin/Android.
 *
 * 2. Cold vs Hot Streams (Холодные vs Горячие потоки) — Ключевая концепция:
 *
 *    - a) COLD Flow (Обычный `flow { ... }`):
 *      - "Ленивый" (Lazy). Код внутри `flow` НЕ выполняется, пока нет подписчика (`.collect()`).
 *      - Unicast (одноадресный): Для КАЖДОГО нового подписчика поток данных создается ЗАНОВО.
 *      - Аналог: Видео на YouTube. Каждый зритель смотрит его с начала, независимо от других.
 *      - Use Case: Одноразовые операции (запрос в БД, скачивание файла).
 *
 *    - b) HOT Flow (`StateFlow`, `SharedFlow`):
 *      - "Активный" (Eager). Поток существует и может генерировать данные НЕЗАВИСIMO от подписчиков.
 *      - Multicast (многоадресный): Один поток данных раздается ВСЕМ текущим подписчикам.
 *      - Аналог: Прямая трансляция (Live Stream). Все зрители видят одно и то же в один момент.
 *        Если подключился позже — пропустил начало.
 *      - Use Case: UI State, События (клики), Broadcasts.
 *
 * 3. Backpressure (Обратное давление) — Встроенная фича:
 *    - Flow построен на `suspend`-функциях. Если `collect` (потребитель) работает медленно,
 *      `emit` (производитель) автоматически ПРИОСТАНОВИТСЯ, чтобы не переполнять память.
 *      Это главное преимущество перед RxJava (где backpressure нужно настраивать вручную).
 *
 * 4. StateFlow vs SharedFlow (Два типа Hot-потоков):
 *    - `StateFlow`:
 *      - Создан для хранения СОСТОЯНИЯ (UI State).
 *      - Всегда имеет начальное значение (`.value`).
 *      - Conflation: отдает подписчикам только самое ПОСЛЕДНЕЕ значение.
 *      - Не эмитит дубликаты (`old == new`).
 *
 *    - `SharedFlow`:
 *      - Создан для передачи СОБЫТИЙ (Events).
 *      - Не имеет начального значения.
 *      - `replay`: может "запоминать" и отдавать новым подписчикам N последних событий.
 *      - По умолчанию события "теряются", если на них никто не подписан.
 * =========================================================================================
 */

fun main() = runBlocking {
    println("--- Cold Flow Demonstration ---")
    demoColdFlowNature()

    println("\n--- Operators & Error Handling ---")
    demoFlowOperatorsAndErrors()

    println("\n--- Flow Context (flowOn) ---")
    demoFlowContext()

    println("\n--- StateFlow (UI State) ---")
    demoStateFlow()

    println("\n--- SharedFlow (One-time Events) ---")
    demoSharedFlow()
}

/*
 * Cold Flow: помнить
 * Демонстрация того, что flow перезапускается для каждого collect().
 * Это критично: если внутри flow тяжелый запрос к API, он выполнится 2 раза для 2 подписчиков.
 */
private suspend fun demoColdFlowNature() {
    val coldFlow = flow {
        println("  -> Starting flow builder (heavy work)...")
        emit(1)
        emit(2)
    }

    println("1. First collector:")
    coldFlow.collect { println("    Received: $it") }

    println("2. Second collector:")
    coldFlow.collect { println("    Received: $it") }
    // Видим в логах, что "Starting flow builder" напечаталось дважды!
}

/*
 * Operators & Error Handling: помнить
 * - map, filter: трансформация (как в коллекциях).
 * - transform: мощный оператор, позволяет эмитить произвольное кол-во значений на 1 входящее.
 * - catch: перехватывает исключения UPSTREAM (те, что произошли ВЫШЕ по цепочке).
 * - onCompletion: выполняется в конце (успешно или с ошибкой), аналог finally.
 */
private suspend fun demoFlowOperatorsAndErrors() {
    (1..5).asFlow()
        .filter { it % 2 == 0 } // Оставляем четные: 2, 4
        .map { it * 10 }        // Умножаем: 20, 40
        .transform { value ->
            // Пример transform: превращаем одно число в два
            emit("Value: $value")
            emit("Value repeated: $value")
        }
        .onEach {
            // Сайд-эффект (например, лог). Не меняет поток.
            if (it.contains("40")) throw RuntimeException("Boom on 40!")
        }
        .catch { e ->
            // Поймает ошибку из onEach (так как onEach выше)
            println("  CAUGHT EXCEPTION: ${e.message}")
            emit("ErrorState") // Можно эмитить значение при ошибке (recovery)
        }
        .onCompletion { cause ->
            // Выполнится всегда. cause != null, если поток упал/был отменен
            println("  Flow completed. Cause: $cause")
        }
        .collect {
            println("  Result: $it")
        }
}

/*
 * Flow Context (flowOn): помнить
 * - Context Preservation: Flow по умолчанию работает в контексте того, кто вызвал collect().
 * - Запрещено: менять контекст внутри flow { ... } через withContext (будет крэш).
 * - Решение: оператор .flowOn(Dispatcher). Он меняет контекст ТОЛЬКО для операторов ВЫШЕ него.
 */
private suspend fun demoFlowContext() {
    val flow = flow {
        // Этот блок выполнится в IO (благодаря flowOn ниже)
        println("  Emitting in thread: ${Thread.currentThread().name}")
        emit("Data")
    }.flowOn(Dispatchers.IO) // Меняет контекст для flow builder
        .map {
            // Этот блок тоже выполнится в IO (т.к. flowOn влияет на всё выше)
            println("  Mapping in thread: ${Thread.currentThread().name}")
            it.uppercase()
        }

    // collect выполняется в том контексте, где запущен (в данном случае main/blocking)
    flow.collect {
        println("  Collecting in thread: ${Thread.currentThread().name}")
    }
}

/*
 * StateFlow: помнить
 * - Аналог LiveData, но на корутинах.
 * - Хранит ОДНО значение (value). Всегда имеет initial state.
 * - Hot (горячий): данные не пересоздаются для новых подписчиков.
 * - Conflation (склеивание): медленный подписчик получит только последнее актуальное значение, пропустив промежуточные.
 * - Equality check: если записать то же самое значение (old == new), эмита НЕ будет.
 */
private suspend fun demoStateFlow() = coroutineScope {
    // MutableStateFlow требует начальное значение
    val state = MutableStateFlow("Initial")

    val collectorJob = launch {
        // collect для StateFlow никогда не завершается сам (это бесконечный поток),
        // пока не отменена корутина.
        state.collect { value ->
            println("  State updated: $value")
        }
    }

    delay(50)
    state.value = "Loading" // Обновление состояния

    delay(50)
    state.value = "Success"
    state.value = "Success" // Дубликат! collect НЕ сработает второй раз.

    delay(50)
    collectorJob.cancel() // Обязательно отменяем, иначе main не завершится
}

/*
 * SharedFlow: помнить
 * - Используется для "One-time events" (События): навигация, тосты, снекбары.
 * - Не хранит значение (по умолчанию replay=0).
 * - Если подписчика нет в момент emit, событие улетает в пустоту (dropped).
 * - Подписчики получают ВСЕ события (нет conflation по умолчанию), если успевают обрабатывать.
 */
private suspend fun demoSharedFlow() = coroutineScope {
    // extraBufferCapacity - буфер для событий, если коллектор не успевает
    val events = MutableSharedFlow<String>(replay = 0)

    val job = launch {
        events.collect { println("  Event handled: $it") }
    }

    delay(50)
    events.emit("Click Button 1")
    events.emit("Show Toast")

    delay(50)
    job.cancel()
}
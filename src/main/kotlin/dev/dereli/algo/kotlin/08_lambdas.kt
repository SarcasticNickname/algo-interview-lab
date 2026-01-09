package dev.dereli.algo.kotlin

/**
 * Файл: 08_lambdas.kt
 * Тема: Лямбда-выражения, Функции высшего порядка, Inline-функции.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: LAMBDAS & HIGHER-ORDER FUNCTIONS
 * =========================================================================================
 *
 * 1. Lambda (Лямбда) — это анонимная функция.
 *    - Это блок кода, который можно передавать как значение (в переменные, в аргументы функций).
 *    - Синтаксис: `{ параметры -> тело }`.
 *
 * 2. Higher-Order Function (Функция высшего порядка):
 *    - Это любая функция, которая либо ПРИНИМАЕТ другую функцию как аргумент,
 *      либо ВОЗВРАЩАЕТ функцию как результат.
 *    - `map`, `filter`, `forEach` — это классические HOF.
 *
 * 3. Производительность и `inline`:
 *    - Проблема: Каждая лямбда в Kotlin компилируется в отдельный объект анонимного класса
 *      (реализующего интерфейс `FunctionN`).
 *    - Это создает "мусор" для Garbage Collector и накладные расходы на вызов.
 *    - Решение: `inline`-функции. Компилятор не создает объект, а КОПИРУЕТ тело
 *      inline-функции и тело лямбды прямо в место вызова.
 *    - Следствие: `inline`-лямбды позволяют делать `return` из внешней функции (non-local return),
 *      так как они становятся частью её кода.
 *
 * 4. Closure (Замыкание):
 *    - Лямбда может "захватывать" (capture) и изменять переменные из своего внешнего окружения.
 *    - В отличие от Java (до 8), переменные не обязаны быть `final`.
 *
 * 5. Receiver Lambdas (Лямбды с получателем):
 *    - Тип: `A.() -> Unit`. Это основа для DSL (Domain Specific Languages).
 *    - Отличие от обычной лямбды `(A) -> Unit`:
 *      - `(A) -> Unit`: объект `A` доступен как параметр (`it`).
 *      - `A.() -> Unit`: объект `A` доступен как `this` (методы можно вызывать напрямую).
 *
 * 6. SAM Conversion (Single Abstract Method):
 *    - Если в Java есть интерфейс с ОДНИМ абстрактным методом (например, `Runnable`),
 *      Kotlin позволяет передавать лямбду вместо создания анонимного объекта.
 * =========================================================================================
 */

fun main() {
    println("--- Basics & Trailing Lambda ---")
    demoBasicLambdasAndTrailing()

    println("\n--- Function Types & High Order ---")
    demoFunctionTypes()

    println("\n--- Closures (Zahvat) ---")
    demoClosures()

    println("\n--- References (::) ---")
    demoMemberReferences()

    println("\n--- Receiver Lambdas (DSL basics) ---")
    demoReceiverLambdas()

    println("\n--- Inline Functions & Returns ---")
    demoInlineFunctions()

    println("\n--- SAM Conversions ---")
    demoSAM()
}

/*
 * Basics: помнить
 * - Синтаксис: { x: Int, y: Int -> body }
 * - it: неявное имя единственного параметра.
 * - Trailing Lambda: если последний аргумент функции - лямбда, выносим её за скобки.
 * - Destructuring: { (key, value) -> ... }
 */
private fun demoBasicLambdasAndTrailing() {
    val nums = listOf(1, 2, 3)

    // 1. Стандартный вид
    val doublePlain = nums.map({ x -> x * 2 })

    // 2. Trailing Lambda (Рекомендуемый стиль)
    // Скобки () можно опустить, если лямбда - единственный аргумент.
    val doubleTrailing = nums.map { it * 2 }
    println("Trailing it: $doubleTrailing")

    // 3. Destructuring arguments (очень удобно для Map)
    val map = mapOf(1 to "A", 2 to "B")

    // Вместо entry -> entry.key, entry.value пишем (k, v)
    val formatted = map.map { (id, name) -> "$id=$name" }
    println("Destructuring lambda: $formatted")

    // 4. Unused parameter (_)
    // Если параметр не нужен, используем подчеркивание
    map.forEach { (_, name) -> print("$name ") }
    println()
}

/*
 * Function Types: помнить
 * - Тип функции: (InputType) -> ReturnType
 * - Unit: если ничего не возвращает.
 * - Функцию можно хранить в переменной.
 */
private fun demoFunctionTypes() {
    // Явное указание типа переменной
    val sum: (Int, Int) -> Int = { a, b -> a + b }

    // Вызов через переменную
    println("sum(2, 3) = ${sum(2, 3)}")

    // Передача в функцию высшего порядка
    // mathOp принимает два числа и ФУНКЦИЮ операции
    val res = mathOp(10, 5, sum)
    println("mathOp sum = $res")

    // Можно передать лямбду напрямую
    val resMinus = mathOp(10, 5) { a, b -> a - b }
    println("mathOp minus = $resMinus")
}

// Функция высшего порядка (принимает функцию 'operation')
private fun mathOp(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

/*
 * Closures (Замыкания): помнить
 * - Лямбда "видит" и может изменять переменные, объявленные снаружи.
 * - В Java (до 8) требовалось final, в Kotlin - нет.
 * - Осторожно: это side-effect.
 */
private fun demoClosures() {
    var sum = 0
    val list = listOf(10, 20, 30)

    // Лямбда меняет внешнюю переменную sum
    list.forEach { sum += it }

    println("Closure sum = $sum")
}

/*
 * Member References (::): помнить
 * - Если лямбда просто вызывает метод у объекта: { it.method() } -> Class::method
 * - Если лямбда вызывает топ-левел функцию: ::functionName
 */
private fun demoMemberReferences() {
    val strings = listOf("abc", "de")

    // Вместо { it.length }
    val lens = strings.map(String::length)
    println("Refs length: $lens")

    // Ссылка на конструктор
    // val users = strings.map { User(it) } -> strings.map(::User)
}

/*
 * Receiver Lambdas (A.() -> Unit): помнить
 * - "Лямбда с получателем". Внутри лямбды `this` указывает на объект типа A.
 * - Это основа DSL (Gradle KTS, HTML builders, Compose).
 * - Отличие: (A) -> Unit (тут объект это `it`), A.() -> Unit (тут объект это `this`).
 */
private fun demoReceiverLambdas() {
    // Обычная: приходится писать it.append
    val buildNormal: (StringBuilder) -> Unit = { sb ->
        sb.append("Normal")
    }

    // С ресивером: пишем методы StringBuilder напрямую
    val buildDsl: StringBuilder.() -> Unit = {
        append("DSL") // this.append
        append(" Style")
    }

    val sb = StringBuilder()
    buildDsl(sb) // Можно вызвать как функцию
    sb.buildDsl() // Можно вызвать как extension-метод!
    println("Receiver result: $sb")
}

/*
 * Inline Functions: помнить !!! (Собеседования)
 * Проблема: Каждая лямбда компилируется в анонимный класс (new Function...).
 * Это нагрузка на память (GC) и процессор (виртуальные вызовы).
 *
 * Решение: inline fun. Компилятор КОПИРУЕТ тело функции и тело лямбды прямо в место вызова.
 * Плюсы: Нет создания объектов, работает быстрее.
 * Минусы: Увеличивает размер байткода (если функция большая).
 *
 * Non-local return:
 * Из обычной лямбды нельзя сделать `return` (выйти из внешней функции).
 * Из inline лямбды — МОЖНО.
 */
private fun demoInlineFunctions() {
    println("Start inline demo")

    // forEach - это inline функция стандартной библиотеки
    (1..5).forEach {
        if (it == 3) {
            println("Found 3, returning from demoInlineFunctions!")
            return // ЭТОТ return завершает demoInlineFunctions(), а не только лямбду!
        }
        print("$it ")
    }

    // Этот код не выполнится
    println("End inline demo")
}

/*
 * SAM Conversions (Single Abstract Method): помнить
 * Если в Java есть интерфейс с ОДНИМ методом (Runnable, OnClickListener),
 * Kotlin позволяет передавать туда лямбду.
 */
private fun demoSAM() {
    // Java-style: executor.execute(new Runnable { void run() { ... } })

    // Kotlin-style:
    runNow {
        println("I am a Runnable created from Lambda!")
    }
}

// Пример Java-интерфейса (fun interface в Kotlin то же самое)
fun interface RunnableAction {
    fun run()
}

private fun runNow(action: RunnableAction) {
    action.run()
}
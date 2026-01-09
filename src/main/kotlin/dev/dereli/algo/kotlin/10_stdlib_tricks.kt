package dev.dereli.algo.kotlin

import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * Файл: 10_stdlib_tricks.kt
 * Тема: Идиоматичный Kotlin, Утилиты стандартной библиотеки, Destructuring.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: IDIOMATIC KOTLIN
 * =========================================================================================
 *
 * 1. Идиоматичность — это "Думать на языке Kotlin".
 *    Вместо того чтобы писать циклы и `if-else` как в Java, идиоматичный код использует
 *    выразительные функции стандартной библиотеки (`stdlib`).
 *    Это делает код короче, безопаснее и более читаемым.
 *
 * 2. Smart Casts & Null Safety:
 *    - Это краеугольный камень языка. Компилятор сам "умнеет" после проверок,
 *      избавляя от ручных приведений типа (`(String) obj`).
 *    - Ограничение: Smart Cast не работает для `var`-свойств класса, так как их
 *      значение может быть изменено другим потоком между проверкой и использованием.
 *
 * 3. Chaining & Single-expression style (Цепочки и однострочники):
 *    - `takeIf`, `coerceIn`, `?.let` — все эти функции спроектированы так, чтобы
 *      их можно было встраивать в цепочки вызовов.
 *    - Это позволяет писать сложные трансформации данных в виде потока (flow),
 *      который читается слева направо.
 *
 * 4. Destructuring Declarations (Деструктуризация):
 *    - Это синтаксический сахар для вызова функций `componentN()`.
 *    - `data class` генерирует их автоматически.
 *    - `Pair`, `Triple`, `Map.Entry` тоже их поддерживают.
 *    - Позволяет извлекать данные из объектов, не обращаясь к ним по имени,
 *      что делает код для работы с парами/тройками очень лаконичным.
 *
 * 5. Scope-безопасные билдеры (`buildList`, `buildString`):
 *    - Это пример хорошего API-дизайна. Они скрывают изменяемое состояние (MutableList)
 *      внутри лямбды и наружу отдают уже неизменяемый результат (List).
 *      Это предотвращает случайные мутации объекта после его создания.
 * =========================================================================================
 */

fun main() {
    println("--- Elvis & Smart Cast ---")
    demoElvisAndSmartCast()

    println("\n--- TakeIf / TakeUnless ---")
    demoTakeIfUnless()

    println("\n--- Coerce (Clamping limits) ---")
    demoCoerce()

    println("\n--- Builders (String/List) ---")
    demoBuildStringBuildList()

    println("\n--- Destructuring Declarations ---")
    demoDestructuring()

    println("\n--- Benchmarking (measureTime) ---")
    demoMeasureTime()

    println("\n--- TODO function ---")
    // demoTodo() // упадет с ошибкой
}

/*
 * Elvis + smart cast: помнить
 * - ?: (Elvis) возвращает дефолт, если слева null.
 * - Smart Cast: авто-кастинг после проверки типа (is String) или null (if x != null).
 * - ВАЖНО: Smart Cast НЕ работает для `var` свойств класса (так как другой поток может их изменить).
 *   Работает для `val` свойств и локальных переменных.
 */
private fun demoElvisAndSmartCast() {
    val s: String? = null
    val len = s?.length ?: 0 // Если s null, вернем 0
    println("elvis len = $len")

    val x: Any = "kotlin"
    if (x is String) {
        // x здесь автоматически String
        println("smart cast uppercase = ${x.uppercase()}")
    }
}

/*
 * takeIf / takeUnless: помнить
 * - takeIf { cond } -> возвращает объект, если cond == true.
 * - takeUnless { cond } -> возвращает объект, если cond == false.
 * Удобно для валидации в цепочках.
 */
private fun demoTakeIfUnless() {
    val raw = "   hello   "
    // Пример: нормализовать строку, но если она пустая - вернуть null
    val normalized = raw.trim().takeIf { it.isNotEmpty() }
    println("normalized: '$normalized'")

    val n = 7
    val result = n.takeUnless { it % 2 != 0 } // Если НЕ нечетное (т.е. четное)
    println("takeUnless even: $result") // null
}

/*
 * Coerce (Ограничение значений): помнить
 * Вместо if (x > max) max else if (x < min) min
 * Используем coerceIn, coerceAtLeast, coerceAtMost.
 */
private fun demoCoerce() {
    val value = 150

    // Ограничить диапазоном 0..100
    val clamped = value.coerceIn(0, 100)
    println("coerceIn(0, 100) for 150 -> $clamped")

    val userInput = -5
    val nonNegative = userInput.coerceAtLeast(0)
    println("coerceAtLeast(0) for -5 -> $nonNegative")
}

/*
 * buildString / buildList / buildMap: помнить
 * Создают StringBuilder/MutableList внутри, дают наполнить его, и возвращают String/List (read-only).
 * Плюсы: Чистый код, нет лишних переменных scope.
 */
private fun demoBuildStringBuildList() {
    val s = buildString {
        append("Hello")
        append(", ")
        append("Kotlin")
    }
    println("buildString = $s")

    // Создаем List динамически
    val list = buildList {
        add(1)
        addAll(listOf(2, 3))
        if (true) add(4)
    }
    println("buildList = $list") // вернет Read-only List
}

/*
 * Destructuring: помнить
 * - Разложение объекта на переменные: val (name, age) = user.
 * - Работает для Data Class, Pair, Triple, Map.Entry, List (до 5 элементов).
 * - Механизм: методы component1(), component2()...
 * - `_` используется для пропуска ненужных переменных.
 */
private fun demoDestructuring() {
    val pair = "id" to 42
    val (key, value) = pair
    println("Pair: $key=$value")

    val user = UserDTO(1, "Serkan", "Admin")
    // Нам нужно только имя, id пропускаем через _
    val (_, name, role) = user
    println("Data: name=$name role=$role")
}

private data class UserDTO(val id: Int, val name: String, val role: String)

/*
 * Benchmarking: помнить
 * measureTimeMillis { ... } - простой способ замерить время выполнения блока.
 * Полезно в алгоритмах для сравнения решений.
 */
private fun demoMeasureTime() {
    val time = measureTimeMillis {
        // Имитация работы
        val list = List(1_000_000) { Random.nextInt() }
        list.sorted()
    }
    println("Sorting 1M items took: $time ms")
}

/*
 * TODO(): помнить
 * Функция, которая всегда кидает NotImplementedError.
 * Используется как заглушка при написании кода.
 * Тип возврата - Nothing.
 */
private fun demoTodo() {
    val x = 10
    if (x > 5) {
        TODO("Реализовать обработку x > 5 позже")
    }
}
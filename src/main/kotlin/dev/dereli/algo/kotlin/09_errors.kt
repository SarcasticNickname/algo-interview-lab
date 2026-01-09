package dev.dereli.algo.kotlin

import java.io.Closeable

/**
 * Файл: 09_errors.kt
 * Тема: Обработка ошибок, Исключения, Result, Ресурсы.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: ERROR HANDLING
 * =========================================================================================
 *
 * 1. Checked vs Unchecked Exceptions:
 *    - В Java есть проверяемые (`Checked`, например IOException) и непроверяемые (`Unchecked`, например NullPointerException) исключения.
 *    - В Kotlin ВСЕ исключения — непроверяемые. Компилятор НЕ заставляет вас оборачивать код в try-catch.
 *    - Философия: Исключения — для исключительных ситуаций. Для ожидаемых ошибок лучше использовать `Result` или `null`.
 *
 * 2. try-catch as Expression:
 *    - Как и `if`/`when`, `try-catch` является выражением.
 *    - Его результат — это значение последнего выражения в `try` или `catch`.
 *    - Блок `finally` выполняется всегда, но на возвращаемое значение не влияет.
 *
 * 3. Result<T>:
 *    - Современный, функциональный способ обработки ошибок.
 *    - Вместо выброса исключения, которое прерывает поток, мы "заворачиваем" результат (успех или ошибку) в объект.
 *    - Позволяет строить чистые цепочки (`map`, `recover`, `onFailure`).
 *    - Идеально для сетевых запросов или парсинга.
 *
 * 4. Ресурсы (`.use`):
 *    - Аналог `try-with-resources` в Java.
 *    - Применяется к любому `Closeable` (InputStream, Cursor, ...).
 *    - Гарантирует, что `.close()` будет вызван, даже если внутри блока произойдет исключение.
 * =========================================================================================
 */

fun main() {
    println("--- Try/Catch Expression ---")
    demoTryCatchExpression()

    println("\n--- Preconditions (require/check/error) ---")
    demoPreconditions()

    println("\n--- Nothing Type (Unreachable Code) ---")
    demoNothingType()

    println("\n--- Result & runCatching (Functional Errors) ---")
    demoRunCatchingAndResult()

    println("\n--- Resources (.use for Auto-Close) ---")
    demoResourcesUse()
}

/*
 * Try-Catch Expression: помнить
 * - Можно присвоить результат выполнения блока переменной.
 * - Последнее выражение в блоке try или catch становится возвращаемым значением.
 */
private fun demoTryCatchExpression() {
    val str = "abc"

    val number: Int = try {
        str.toInt()
    } catch (e: NumberFormatException) {
        println("  Log: failed to parse '$str'")
        -1 // дефолтное значение
    }

    println("  Parsed number: $number")
}

/*
 * Preconditions: помнить
 * Стандартные функции для проверки условий. Кидают RuntimeException.
 * 1. require(Boolean) { "message" } -> IllegalArgumentException. Для проверки аргументов функции.
 * 2. check(Boolean)   { "message" } -> IllegalStateException. Для проверки состояния объекта.
 * 3. error(message)                 -> IllegalStateException. Для невозможных состояний.
 */
private fun demoPreconditions() {
    try {
        User(id = -1, name = "") // Сначала сработает require на id
    } catch (e: IllegalArgumentException) {
        println("  Require failed: ${e.message}")
    }

    val session = Session(active = false)
    try {
        session.performAction()
    } catch (e: IllegalStateException) {
        println("  Check failed: ${e.message}")
    }
}

private data class User(val id: Int, val name: String) {
    init {
        // require - проверяем ВХОДЯЩИЕ параметры
        require(id > 0) { "User ID must be positive, but got $id" }
        require(name.isNotBlank()) { "User name must not be blank" }
    }
}

private class Session(val active: Boolean) {
    fun performAction() {
        // check - проверяем ВНУТРЕННЕЕ состояние
        check(active) { "Session is not active, cannot perform action" }
        println("  Action performed successfully")
    }
}

/*
 * Nothing Type: помнить
 * - Тип "функция никогда не вернет управление успешно".
 * - Позволяет компилятору знать, что код дальше недостижим (Unreachable code).
 * - `null ?: fail()` работает, потому что Nothing является подтипом любого типа.
 */
private fun demoNothingType() {
    val data: String? = null

    try {
        // Если data null, fail вызовет throw, и присваивания не произойдет.
        // Если не null, data скастится к String (smart cast).
        val result: String = data ?: fail("Data is missing!")
        println(result)
    } catch (e: RuntimeException) {
        println("  Caught exception from fail(): ${e.message}")
    }
}

private fun fail(reason: String): Nothing {
    throw RuntimeException(reason)
}

/*
 * Result & runCatching: помнить (Modern Style)
 * - runCatching { ... } выполняет блок и возвращает Result<T>.
 * - Исключение не "взрывает" поток, а сохраняется внутри объекта Result.
 * - Удобно для цепочек: map, onFailure, onSuccess, fold.
 */
private fun demoRunCatchingAndResult() {
    val input = "invalid_int"

    val result: Result<Int> = runCatching {
        input.toInt() // упадет
    }

    // 1. Простая обработка
    result
        .onSuccess { println("  Success: $it") }
        .onFailure { println("  Failure: ${it.javaClass.simpleName}") }

    // 2. Трансформация (Map & Recover)
    val mappedResult = result
        .map { it * 2 } // выполнится только если успех
        .recover { e ->   // выполнится если была ошибка, позволяет "восстановиться"
            println("  Recovering from error, returning default value")
            0
        }

    println("  Mapped/Recovered result: ${mappedResult.getOrNull()}")

    // 3. Fold - сведение к одному типу (самый безопасный способ распаковки)
    val message = result.fold(
        onSuccess = { "The parsed number is $it" },
        onFailure = { "An error happened: ${it.message}" }
    )
    println("  Folded message: $message")
}

/*
 * Resources (.use): помнить !!!
 * - Аналог try-with-resources в Java.
 * - Применяется к объектам, реализующим AutoCloseable (или Closeable).
 * - Гарантирует вызов .close() даже если внутри блока произошло исключение.
 */
private fun demoResourcesUse() {
    // Имитация ресурса
    class MyResource : Closeable {
        fun read() = println("  Reading data from resource...")
        override fun close() = println("  Resource closed automatically!")
    }

    MyResource().use { resource ->
        resource.read()
        // res.close() вызовется автоматически здесь, после выхода из блока
    }
}
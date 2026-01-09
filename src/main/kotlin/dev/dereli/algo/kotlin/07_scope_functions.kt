package dev.dereli.algo.kotlin

/**
 * Файл: 07_scope_functions.kt
 * Тема: Функции области видимости (let, run, with, apply, also).
 *
 * ШПАРГАЛКА (Заучить таблицу!):
 * -----------------------------------------------------------------------
 * | Функция | Контекст (как обращаемся) | Возвращает (Результат)      | Главный Use Case                     |
 * |---------|---------------------------|-----------------------------|--------------------------------------|
 * | let     | it (можно переименовать)  | Результат лямбды (R)        | Null-check (?.let), маппинг, скоуп   |
 * | run     | this                      | Результат лямбды (R)        | Настройка + вычисление, блок кода    |
 * | with    | this                      | Результат лямбды (R)        | Группировка вызовов (не extension)   |
 * | apply   | this                      | Сам объект (Context object) | Инициализация / Настройка (Builder)  |
 * | also    | it (можно переименовать)  | Сам объект (Context object) | Сайд-эффекты (логи), валидация       |
 * -----------------------------------------------------------------------
 *
 * Как выбрать?
 * 1. Нужно вернуть САМ объект? -> apply (настройка) или also (лог/проверка).
 * 2. Нужно вернуть РЕЗУЛЬТАТ? -> let (преобразование), run (вычисление), with.
 * 3. it или this?
 *    - `it` лучше, если объект используется как аргумент. `it` можно переименовать -> `name -> ...`.
 *    - `this` лучше, если вызываем методы объекта. Но `this` затеняет внешний класс!
 */

fun main() {
    println("--- let ---")
    demoLet()

    println("\n--- run ---")
    demoRun()

    println("\n--- apply ---")
    demoApply()

    println("\n--- also ---")
    demoAlso()

    println("\n--- with ---")
    demoWith()

    println("\n--- takeIf / takeUnless ---")
    demoTakeIfTakeUnless()

    println("\n--- The 'this' Trap (Shadowing) ---")
    demoScopeShadowing()
}

/*
 * let: помнить
 * Контекст: it
 * Возврат: Result
 *
 * Топ кейсы:
 * 1. Выполнение кода только если не null: `str?.let { ... }`
 * 2. Введение локальной области видимости для переменной.
 */
private fun demoLet() {
    val text: String? = "Kotlin"

    // 1. Null-safety
    // Если text != null, выполняется блок. Результат блока записывается в len.
    val len = text?.let {
        println("Working with: $it")
        it.length // результат
    } ?: 0
    println("Length: $len")

    // 2. Переименование it (читабельность)
    text?.let { str ->
        println("My string is $str")
    }
}

/*
 * run: помнить
 * Контекст: this
 * Возврат: Result
 *
 * Топ кейсы:
 * 1. obj.run { ... } -> что-то посчитать внутри объекта и вернуть результат.
 * 2. run { ... } -> (без объекта) просто блок кода, чтобы ограничить область видимости переменных.
 */
private fun demoRun() {
    val service = Service("http://api.com", 8080)

    // Кейс 1: Настройка + Результат
    val status = service.run {
        // тут this = service
        port += 1 // меняем состояние
        "Service running on $url:$port" // возвращаем строку
    }
    println(status)

    // Кейс 2: Блок кода (Non-extension run)
    val hex = run {
        val x = 255 // x виден только внутри этого блока
        Integer.toHexString(x)
    }
    println("Hex: $hex")
    // println(x) // Ошибка, x не виден
}

private class Service(val url: String, var port: Int)

/*
 * apply: помнить
 * Контекст: this
 * Возврат: Сам Объект
 *
 * Топ кейс:
 * Инициализация объекта (Builder pattern).
 * "Примени к этому объекту настройки и верни его же".
 */
private fun demoApply() {
    // Часто используется в Android для Intent, Bundle, Fragment
    val settings = AppSettings().apply {
        theme = "Dark"
        notificationsEnabled = true
        // return не нужен, вернется settings
    }
    println("Applied settings: $settings")
}

private data class AppSettings(var theme: String = "Light", var notificationsEnabled: Boolean = false)

/*
 * also: помнить
 * Контекст: it
 * Возврат: Сам Объект
 *
 * Топ кейс:
 * "А также сделай вот это..."
 * Логирование, валидация, печать, не разрывая цепочку вызовов.
 * Часто путают с apply. Если нужно настраивать поля (setX) -> apply. Если нужно использовать объект как аргумент -> also.
 */
private fun demoAlso() {
    val nums = mutableListOf(1, 2, 3)

    val result = nums
        .also { println("Original: $it") } // it = список
        .map { it * 2 }
        .also { println("Mapped: $it") }   // it = новый список
        .sum()

    println("Sum: $result")
}

/*
 * with: помнить
 * Контекст: this
 * Возврат: Result
 *
 * Отличие: Это НЕ extension-функция. Вызывается как with(obj) { ... }.
 * Используется, чтобы не писать имя объекта много раз.
 * Сейчас часто заменяется на .run {}, так как run поддерживает null-safety (obj?.run), а with(obj?) - нет.
 */
private fun demoWith() {
    val sb = StringBuilder()

    val resultString = with(sb) {
        append("Line 1\n")
        append("Line 2")
        toString().uppercase() // возвращаем результат
    }
    println("With result: $resultString")
}

/*
 * takeIf / takeUnless: помнить
 * Фильтрация одиночного объекта.
 * takeIf { cond } -> возвращает объект, если true, иначе null.
 * takeUnless { cond } -> возвращает объект, если false, иначе null.
 */
private fun demoTakeIfTakeUnless() {
    val number = 42

    // Раньше: if (number % 2 == 0) number else null
    val evenOrNull = number.takeIf { it % 2 == 0 }

    val str = "   "
    // Очень полезно для строк
    val validString = str.takeIf { it.isNotBlank() } // null

    println("Even: $evenOrNull, ValidStr: $validString")
}

/*
 * Проблема вложенности (Shadowing): Важно!
 * Когда мы вкладываем run/apply/with друг в друга, `this` перекрывается.
 * В таких случаях лучше использовать let/also (через it или именованный аргумент).
 */
private fun demoScopeShadowing() {
    val outer = Outer2("OUTER")

    outer.run {
        // this = Outer
        println("1. this is $this")

        val inner = Inner("INNER")
        inner.run {
            // this = Inner. Как обратиться к Outer?
            println("2. this is $this")

            // Чтобы обратиться к outer, нужны метки (labels), что неудобно
            // println(this@run.name) // так не сработает, если имена функций одинаковы
        }
    }

    println("--- Better solution ---")

    // Решение: использовать let/also, чтобы дать имя переменной
    outer.let { out ->
        val inner = Inner("INNER")
        inner.run {
            println("Inner is $this, Outer is $out") // Всё четко и понятно
        }
    }
}

private data class Outer2(val name: String)
private data class Inner(val name: String)
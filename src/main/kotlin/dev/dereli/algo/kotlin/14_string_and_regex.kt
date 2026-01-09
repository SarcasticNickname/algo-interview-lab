package dev.dereli.algo.kotlin

/**
 * Файл: 14_strings_and_regex.kt
 * Тема: Строки, StringBuilder, Регулярные выражения (Regex), ASCII/Unicode.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК
 * =========================================================================================
 *
 * 1. String Immutability (Неизменяемость):
 *    - В Kotlin (как и в Java) строки НЕИЗМЕНЯЕМЫ.
 *    - Любая операция (uppercase, trim, replace, +) создает НОВЫЙ объект строки в куче.
 *    - Старая строка остается в памяти (пока ее не соберет GC).
 *    - В алгоритмах: Никогда не делайте `str += char` внутри цикла! Это сложность O(n^2),
 *      так как на каждом шаге копируется весь массив символов.
 *
 * 2. StringBuilder:
 *    - Это изменяемый (mutable) список символов.
 *    - Используется для сборки строк в циклах. Сложность добавления: амортизированное O(1).
 *    - Потоконебезопасен (в отличие от старого StringBuffer). Быстрее.
 *
 * 3. Char & ASCII/Unicode:
 *    - Char в Kotlin — это 16-битный Unicode символ (UTF-16).
 *    - Арифметика: символы можно вычитать ('c' - 'a' = 2). Это база для алгоритмов анаграмм/частот.
 *    - Методы: isDigit(), isLetter(), isUpperCase().
 *
 * 4. Regex (Регулярные выражения):
 *    - Мощный инструмент поиска шаблонов (email, телефон, парсинг логов).
 *    - Дорогой в создании! Если регулярка используется в цикле, компилируйте её один раз (`val regex = "...".toRegex()`).
 *
 * 5. String Pool (Пул строк):
 *    - Литералы ("hello") хранятся в специальной области памяти. Если создать две одинаковые строки литералами,
 *      это будет одна ссылка.
 * =========================================================================================
 */

fun main() {
    println("--- 1. Immutability & StringBuilder ---")
    demoStringBuilder()

    println("\n--- 2. Slicing & Manipulation (Algo basics) ---")
    demoStringManipulation()

    println("\n--- 3. Char Arithmetic (Frequency Arrays) ---")
    demoCharArithmetic()

    println("\n--- 4. Regex (Patterns) ---")
    demoRegex()

    println("\n--- 5. Formatting & Templates ---")
    demoFormatting()
}

/*
 * StringBuilder: помнить
 * Если нужно собрать строчку из кусочков (в цикле) -> ВСЕГДА StringBuilder
 * String concatenation (+) в цикле - зло и O(n^2)
 */
private fun demoStringBuilder() {
    // Плохо (O(N^2)):
    // var s = ""
    // for(i in 0..10) s+=i // создаёт 10 лишних объектов

    // Хорошо (O(n))
    val sb = StringBuilder()
    for (i in 0..3) {
        sb.append("Step $i; ")
    }

    // В Kotlin есть удобная функция-обёртка buildString:
    val result = buildString {
        append("Start")
        append(sb)
        append("End")
    }

    println("Builder Result: $result")
}

/*
 * String Manipulation: Помнить
 * Эти методы надо знать наизусть для решения задач (LeetCode Easy/Medium)
 * - substring, chunked, windowed, split, joinToString
 */
private fun demoStringManipulation() {
    val raw = "Kotlin-Is-Fun"

    // 1. Substring (подстрока)
    // substringAfter / substringBefore - супер удобны, чтобы не искать индексы вручную
    println("After '-': ${raw.substringAfter("-")}") // Is-Fun
    println("Before last '-': ${raw.substringBeforeLast("-")}") // Kotlin-Is

    // 2. Split (разделение)
    val parts = raw.split("-") // List<String>
    println("Split: $parts")

    // 3. Join(сборка обратно)
    val joined = parts.joinToString(separator = "->", prefix = "[", postfix = "")
    println("Joined: $joined")

    // 4. Chunked (разбить на пачки фиксированной длины)
    val text = "1234567890"
    println("Chunked(3): ${text.chunked(3)}") // [123,456,789,0]

    // 5. Windowed (Скользящее окно - Sliding Window)
    // Важно для алгоритмов (например, найти подстроку с определённой суммой символов)
    val sliding = text.windowed(size = 3, step = 1)
    println("Windowed: $sliding") // [123, 234, 345, ...]

    // 6. Access (доступ к символу)
    // str[i] работает за O(1)
    println("Char at 1: ${raw[1]}")
}

/*
 * Char Arithmetic: помнить
 * - Char можно конвертировать в Int(код) и обратно
 * - ('a' .. 'z') - это range
 * - 'c' - 'a' возвращает разницу в позиции (сдвиг)
 * - Это используется для создания "частотных массивов" (Frequency map) без HashMap
 */
private fun demoCharArithmetic() {
    val ch = 'c'
    val base = 'a'

    // Получаем индекс буквы в алфавите (0-25)
    val index = ch - base
    println("Index of $ch is $index") // c(2) - a(0) = 2

    // Частотный массив (для анаграмм)
    // Если строки состоят только из английских букв, массив IntArray(26) быстрее и легче, чем HashMap
    val word = "banana"
    val freq = IntArray(26)
    for (char in word) {
        freq[char - 'a']++
    }

    // Печатаем ненулевые
    print("Frequency: ")
    freq.forEachIndexed { i, count ->
        if (count > 0) print("${(base + i)}=$count")
    }
    println()

    // Проверки
    println("Is digit '1'? ${'1'.isDigit()}")
    println("Is letter '?'? ${'?'.isLetter()}")
}

/*
 * Regex: помнить
 * - Regex("pattern") создает объект.
 * - raw strings ("""...""") удобны для Regex, чтобы не писать двойные слеши (\\d).
 * - find() - найти первое вхождение.
 * - findAll() - найти все.
 * - matchEntire() - подходит ли ВСЯ строка под шаблон.
 * - Группы (Groups) - вытаскивание частей (через скобки).
 */
private fun demoRegex() {
    // """\d{4}-\d{2}-\d{2}""" -> ищем дату YYYY-MM-DD
    // Без тройных кавычек пришлось бы писать "\\d{4}-\\d{2}..."
    val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
    val text = "Report generated on 2023-10-15 and updated 2023-10-16."

    // 1. Поиск
    val firstMatch = dateRegex.find(text)
    println("First date found: ${firstMatch?.value}")

    val allMatches = dateRegex.findAll(text).map { it.value }.toList()
    println("All dates: $allMatches")

    // 2. Группы захвата (Capture Groups)
    // Парсим "Item: 123, Price: 99"
    val itemRegex = Regex("""Item: (\w+), Price: (\d+)""")
    val input = "Item: Apple, Price: 100"

    val match = itemRegex.matchEntire(input)
    if (match != null) {
        // destructured доступен для групп
        val (name, price) = match.destructured
        println("Parsed: Item='$name', Price=${price.toInt()}")
    }

    // 3. Замена (Replace)
    // Заменить все цифры на *
    val secret = "My phone is 123-45-67"
    val masked = secret.replace(Regex("""\d"""), "*")
    println("Masked: $masked")
}

/*
 * Formatting: помнить
 * - trimIndent() / trimMargin() - для красивых многострочных текстов.
 * - padStart / padEnd - дополнение пробелами (или символами) до нужной длины.
 * - String.format() - классический Java стиль (%d, %.2f).
 */
private fun demoFormatting() {
    // 1. Multiline clean
    val json = """
        {
            "id": 1,
            "val": "test"
        }
    """.trimIndent() // убирает общий отступ слева
    println("JSON:\n$json")

    // 2. Padding (часто нужно для вывода таблиц или бинарных чисел)
    val bin = "101"
    val padded = bin.padStart(8, '0') // Дополнить нулями слева до длины 8
    println("Padded binary: $padded")

    // 3. Format (округление)
    val pi = 3.1415926
    // %.2f - 2 знака после запятой
    println("Formatted PI: ${"%.2f".format(pi)}")
}
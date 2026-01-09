package dev.dereli.algo.kotlin

/**
 * Файл: 16_functional_idioms.kt
 * Тема: Функциональные идиомы, Хвостовая рекурсия, Ленивые вычисления (Sequences), Свертки.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК (FUNCTIONAL POWER)
 * =========================================================================================
 *
 * 1. Recursion & Stack Overflow:
 *    - Обычная рекурсия тратит память стека на каждый вызов. Глубина ~10-20 тысяч вызовов — и приложение падает.
 *    - Решение: Хвостовая рекурсия (Tail Call Optimization).
 *    - В Kotlin: модификатор `tailrec`. Если рекурсивный вызов — это ПОСЛЕДНЯЯ операция функции,
 *      компилятор перепишет её в обычный цикл `while`. Память O(1).
 *
 * 2. Eager vs Lazy (List vs Sequence):
 *    - List (Eager/Жадный): `list.map{}.filter{}`.
 *      Создает НОВЫЙ список на каждом шаге. 1 млн элементов -> map -> еще 1 млн в памяти -> filter -> результат.
 *      Плохо для длинных цепочек и больших данных.
 *    - Sequence (Lazy/Ленивый): `seq.map{}.filter{}`.
 *      Ничего не делает, пока не вызовешь терминальную операцию (toList, count, sum).
 *      Обрабатывает элемент за элементом сквозь всю цепочку (Vertical processing).
 *      Аналог Stream API в Java.
 *
 * 3. Fold / Reduce / Scan:
 *    - Это универсальные функции агрегации. `sum`, `min`, `joinToString` — это всё частные случаи `fold`.
 *    - `fold`: требует начальное значение (initial). Безопасен для пустых списков.
 *    - `reduce`: берет первый элемент как initial. Падает на пустом списке.
 *    - `scan` (Prefix Sum): как fold, но возвращает список ВСЕХ промежуточных результатов.
 *      Супер полезно для алгоритмов "Running Total" (бегущая сумма).
 * =========================================================================================
 */

fun main() {
    println("--- 1. Tail Recursion (No StackOverflow) ---")
    demoTailRecursion()

    println("\n--- 2. Sequences vs Lists (Lazy vs Eager) ---")
    demoSequences()

    println("\n--- 3. Fold & Reduce (Aggregation) ---")
    demoFoldReduce()

    println("\n--- 4. Scan (Running Totals) ---")
    demoScan()
}

/*
 * Tail Recursion: помнить
 * - Работает только если рекурсивный вызов — последнее действие.
 * - Если написать `1 + factorial(n-1)`, это НЕ хвостовая рекурсия (т.к. последнее действие — сложение).
 * - Нужно передавать аккумулятор в параметры.
 */
private fun demoTailRecursion() {
    // Обычная рекурсия (упадет на 100_000)
    // fun recursiveSum(n: Long): Long = if (n <= 0) 0 else n + recursiveSum(n - 1)

    // Хвостовая рекурсия (работает как цикл)
    val result = tailRecursiveSum(100_000, 0)
    println("Sum of 1..100_000 = $result")
}

// Модификатор tailrec обязателен, чтобы компилятор сделал магию
private tailrec fun tailRecursiveSum(n: Long, accumulator: Long): Long {
    return if (n <= 0) {
        accumulator
    } else {
        // Последнее действие - чистый вызов функции (без сложений снаружи)
        tailRecursiveSum(n - 1, accumulator + n)
    }
}

/*
 * Sequences: помнить
 * - Используй .asSequence() для длинных цепочек операций (> 2-3 шагов) или больших коллекций.
 * - Позволяет избежать создания промежуточных коллекций.
 * - Поддерживает short-circuit (ранний выход): если нужен .first(), он не будет обрабатывать весь список.
 */
private fun demoSequences() {
    val list = listOf(1, 2, 3, 4, 5)

    println(">> List processing (Eager):")
    // Сначала map выполнится для ВСЕХ, потом filter для ВСЕХ
    val listRes = list
        .map { print("Map($it) "); it * 2 }
        .filter { print("Filter($it) "); it > 5 }
        .first() // Взяли первый попавшийся
    println("\nResult: $listRes")
    // Вывод будет: Map(1) Map(2) ... Map(5) Filter(2) ... (много лишней работы)

    println("\n>> Sequence processing (Lazy):")
    // Берет 1 -> map -> filter. Не подошел?
    // Берет 2 -> map -> filter. Не подошел?
    // Берет 3 -> map -> filter. Подошел! Стоп.
    val seqRes = list.asSequence()
        .map { print("Map($it) "); it * 2 }
        .filter { print("Filter($it) "); it > 5 }
        .first()
    println("\nResult: $seqRes")
    // Вывод: Map(1) Filter(2) Map(2) Filter(4) Map(3) Filter(6). Экономия!
}

/*
 * Fold & Reduce: помнить
 * - fold(initial) { acc, item -> ... }
 * - reduce { acc, item -> ... } (initial = item[0])
 * - Используем fold, если тип аккумулятора отличается от типа элементов (List<Int> -> String).
 */
private fun demoFoldReduce() {
    val nums = listOf(1, 2, 3, 4)

    // Пример: Сумма (классика)
    val sum = nums.fold(0) { acc, i -> acc + i }
    println("Fold Sum: $sum")

    // Пример: Построение строки (вручную, как joinToString)
    // acc - StringBuilder, i - Int
    val str = nums.fold(StringBuilder("Start:")) { sb, i ->
        sb.append(" $i")
    }.toString()
    println("Fold Builder: $str")

    // Reduce - опасно на пустых списках!
    // val empty = emptyList<Int>()
    // empty.reduce { a, b -> a + b } // Exception: Empty collection can't be reduced.

    // Fold безопасен
    // empty.fold(0) ... // вернет 0
}

/*
 * Scan (Running Fold): помнить
 * - Генерирует последовательность всех промежуточных аккумуляторов.
 * - scan(0) { acc, i -> acc + i } превращает [1, 2, 3] в [0, 1, 3, 6].
 * - Очень часто встречается в задачах на "Prefix Sums" (сумма на отрезке за O(1)).
 */
private fun demoScan() {
    val nums = listOf(10, 20, 30, 40)

    // Running Sum (накопительная сумма)
    val runningTotal = nums.scan(0) { acc, i -> acc + i }

    println("Original: $nums")
    println("Prefix Sums (scan): $runningTotal")
    // Результат: [0, 10, 30, 60, 100]
    // Теперь сумма элементов с index 1 по 2 (20+30) = runningTotal[3] - runningTotal[1] = 60 - 10 = 50.
}
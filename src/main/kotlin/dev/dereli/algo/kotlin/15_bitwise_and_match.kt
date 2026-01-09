package dev.dereli.algo.kotlin

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Файл: 15_bitwise_and_math.kt
 * Тема: Битовые операции, Unsigned типы, BigInteger, Нюансы математики (Modulo).
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК (BITWISE MAGIC)
 * =========================================================================================
 *
 * 1. Битовое представление (Binary):
 *    - Компьютер видит Int как 32 бита (0 или 1).
 *    - Положительные числа начинаются с 0, отрицательные с 1 (Two's complement - дополнительный код).
 *
 * 2. Основные операторы (В Kotlin это инфиксные функции, а не символы как в Java):
 *    - shl (<<) : Сдвиг влево. Равносильно умножению на 2^N. (x shl 1 == x * 2).
 *    - shr (>>) : Арифметический сдвиг вправо. Сохраняет знак числа. (x shr 1 == x / 2).
 *    - ushr (>>>): Логический сдвиг вправо. Заполняет нулями слева (игнорирует знак). Важно для Unsigned!
 *    - and (&)  : "И". 1 если оба 1. Используется для МАСОК (обнулить лишние биты).
 *    - or  (|)  : "Или". 1 если хотя бы один 1. Используется для УСТАНОВКИ битов.
 *    - xor (^)  : "Исключающее ИЛИ". 1 если биты РАЗНЫЕ. (1 xor 1 = 0).
 *                 Свойство: (A xor A = 0). Супер-полезно для поиска дубликатов.
 *    - inv (~)  : Инверсия. Меняет 0 на 1 и наоборот.
 *
 * 3. Математика:
 *    - Деление по модулю (%): В Kotlin/Java результат имеет знак делимого! -5 % 2 = -1 (а не 1).
 *    - BigInteger: Для чисел больше Long.MAX_VALUE (факториалы, огромные суммы).
 *    - BigDecimal: Для денег (float/double теряют точность, BigDecimal - нет).
 * =========================================================================================
 */

fun main() {
    println("--- 1. Basic Bitwise Ops ---")
    demoBitwiseBasics()

    println("\n--- 2. Practical Bit Tricks (Algo) ---")
    demoBitwiseTricks()

    println("\n--- 3. Shifts (Optimization) ---")
    demoShifts()

    println("\n--- 4. Modulo (Negative numbers trap) ---")
    demoModuloBehavior()

    println("\n--- 5. Big Numbers (BigInteger / BigDecimal) ---")
    demoBigNumbers()
}

/*
 * Basic Bitwise Ops: помнить
 * Визуализация битов помогает понять, что происходит.
 * Используем .toString(2) для вывода в двоичном виде.
 */
private fun demoBitwiseBasics() {
    val a = 0b1100 // 12
    val b = 0b1010 // 10

    // AND (Оба должны быть 1) -> 1000 (8)
    println("AND: ${(a and b).toBinString()} (8)")

    // OR (Хотя бы один 1) -> 1110 (14)
    println("OR : ${(a or b).toBinString()} (14)")

    // XOR (Разные биты) -> 0110 (6)
    println("XOR: ${(a xor b).toBinString()} (6)")

    // INV (Инверсия) -> переворачивает все 32 бита
    val c = 0
    println("INV: ${c.inv().toBinString()} (-1)")
}

/*
 * Practical Bit Tricks: помнить (ОЧЕНЬ ВАЖНО ДЛЯ АЛГОРИТМОВ)
 * Эти паттерны встречаются в Easy/Medium задачах постоянно.
 */
private fun demoBitwiseTricks() {
    val n = 42

    // 1. Проверка на четность
    // n % 2 == 0 медленнее, чем n & 1 == 0
    // Если последний бит 0 -> число четное. Если 1 -> нечетное.
    val isOdd = (n and 1) != 0
    println("Is 42 odd? $isOdd")

    // 2. Сброс последнего установленного бита (Brian Kernighan’s Algorithm)
    // n and (n - 1). Используется для подсчета количества единиц в числе.
    var x = 0b1010 // 10
    println("Before turn off rightmost bit: ${x.toBinString()}")
    x = x and (x - 1) // 1010 & 1001 = 1000
    println("After:  ${x.toBinString()}")

    // 3. Проверка на степень двойки
    // Число является степенью двойки (1, 2, 4, 8...), если у него только один бит = 1.
    // Тогда (n & (n-1)) должно стать 0.
    val p = 16
    val isPowerOfTwo = (p > 0) && (p and (p - 1) == 0)
    println("Is 16 power of two? $isPowerOfTwo")

    // 4. Поиск уникального числа (Single Number problem)
    // Массив, где все числа встречаются 2 раза, а одно - 1 раз.
    // A xor A = 0. 0 xor B = B.
    val nums = listOf(5, 1, 5, 2, 1) // Ответ 2
    val unique = nums.reduce { acc, i -> acc xor i }
    println("Single number in $nums is $unique")
}

/*
 * Shifts: помнить
 * shl (<<) - умножение на 2
 * shr (>>) - деление на 2
 * ushr (>>>) - беззнаковый сдвиг (важно при работе с цветами, байтами)
 */
private fun demoShifts() {
    val x = 10 // 0...1010

    println("10 shl 1 (10 * 2) = ${x shl 1}")
    println("10 shr 1 (10 / 2) = ${x shr 1}")

    val negative = -10
    // shr сохраняет знак (заполняет единицами слева)
    println("-10 shr 1 = ${negative shr 1}") // -5
    // ushr не сохраняет знак (заполняет нулями, число становится огромным положительным)
    println("-10 ushr 1 = ${negative ushr 1}") // 2147483643
}

/*
 * Modulo Behavior: помнить
 * В математике -5 mod 2 = 1.
 * В программировании (Kotlin/Java) -5 % 2 = -1.
 * Результат берет знак ДЕЛИМОГО (числителя).
 *
 * Если нужен "настоящий" математический модуль (для циклических массивов),
 * используйте формулу: ((a % n) + n) % n
 */
private fun demoModuloBehavior() {
    val n = -5
    val d = 2
    println("-5 % 2 = ${n % d}") // -1

    // Правильный циклический индекс (например, сдвиг влево в массиве)
    // [0, 1, 2], shift left from 0 -> index 2
    val index = 0
    val size = 3
    val prevIndexRaw = index - 1 // -1
    // val safeIndex = prevIndexRaw % size // будет -1, ошибка ArrayOutOfBounds
    val mathMod = ((index - 1) % size + size) % size
    println("Correct cyclic index for -1 in size 3 is: $mathMod") // 2
}

/*
 * Big Numbers: помнить
 * Long вмещает до 9 квинтиллионов (2^63 - 1). Если нужно больше -> BigInteger.
 * Double/Float не подходят для денег (0.1 + 0.2 != 0.3). -> BigDecimal.
 */
private fun demoBigNumbers() {
    // BigInteger
    var bigInt = BigInteger.valueOf(Long.MAX_VALUE)
    bigInt = bigInt.add(BigInteger.TEN) // Long бы переполнился и ушел в минус
    println("BigInteger: $bigInt")

    // BigDecimal
    val price = BigDecimal("0.1")
    val tax = BigDecimal("0.2")
    // В double было бы 0.30000000000000004
    println("BigDecimal 0.1 + 0.2 = ${price.add(tax)}")
}

// Extension для красивого вывода битов
private fun Int.toBinString(): String = Integer.toBinaryString(this).padStart(4, '0')
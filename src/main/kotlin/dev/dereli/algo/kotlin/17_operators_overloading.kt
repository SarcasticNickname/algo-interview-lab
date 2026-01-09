package dev.dereli.algo.kotlin

/**
 * Файл: 17_operators_overloading.kt
 * Тема: Перегрузка операторов, Invoke, Get/Set, CompareTo.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК (OPERATOR MAGIC)
 * =========================================================================================
 *
 * 1. Концепция:
 *    - В Kotlin нет произвольных операторов (как в Scala/Haskell). Есть фиксированный набор символов (+, -, *, /, [], (), >, <),
 *      которые транслируются компилятором в вызовы конкретных функций.
 *    - Чтобы перегрузить оператор, функция должна иметь модификатор `operator` и специальное имя.
 *
 * 2. Таблица трансляции (Mapping):
 *    a + b  -> a.plus(b)
 *    a - b  -> a.minus(b)
 *    a * b  -> a.times(b)
 *    a / b  -> a.div(b)
 *    a % b  -> a.rem(b)
 *    !a     -> a.not()
 *    a[i]   -> a.get(i)
 *    a[i]=b -> a.set(i, b)
 *    a()    -> a.invoke()
 *    a > b  -> a.compareTo(b) > 0
 *
 * 3. Важные правила:
 *    - inc (++) и dec (--) не меняют объект "на месте" (для val), а возвращают новый.
 *      Kotlin сам делает переприсваивание: x++ -> x = x.inc().
 *    - compareTo должен возвращать Int (0, <0, >0), чтобы заработали операторы >, <, >=, <=.
 *    - equals (==) переопределяется БЕЗ `operator` (так как он уже есть в Any), но концептуально это тоже оператор.
 * =========================================================================================
 */

fun main() {
    println("--- 1. Arithmetic (+, -, *) ---")
    demoArithmetic()

    println("\n--- 2. Index Access ( [] ) ---")
    demoIndexAccess()

    println("\n--- 3. Invoke Operator ( () ) ---")
    demoInvoke()

    println("\n--- 4. Comparison (CompareTo) ---")
    demoComparison()
}

/*
 * Arithmetic Operators: помнить
 * Пример: Класс Vector (Point).
 * Это делает код алгоритмов (геометрия) очень чистым.
 */
private fun demoArithmetic() {
    val v1 = Vector(1, 2)
    val v2 = Vector(3, 4)

    // Вызывает v1.plus(v2)
    val sum = v1 + v2
    println("Vector Sum: $sum")

    // Вызывает v2.times(2)
    val scaled = v2 * 2
    println("Vector Scaled: $scaled")

    // Унарный минус (-v1)
    println("Negated: ${-v1}")
}

private data class Vector(val x: Int, val y: Int) {
    // a + b
    operator fun plus(other: Vector): Vector {
        return Vector(this.x + other.x, this.y + other.y)
    }

    // a * scalar (умножение на число)
    operator fun times(scalar: Int): Vector {
        return Vector(this.x * scalar, this.y * scalar)
    }

    // -a (унарный минус)
    operator fun unaryMinus(): Vector {
        return Vector(-x, -y)
    }
}

/*
 * Index Access ([]): помнить
 * operator get / set.
 * Позволяет обращаться к объекту как к массиву или map.
 * Идеально для Matrix, Grid, CustomList.
 */
private fun demoIndexAccess() {
    val matrix = Matrix2x2()

    // set(row, col, value)
    matrix[0, 0] = 1
    matrix[0, 1] = 2
    matrix[1, 0] = 3
    matrix[1, 1] = 4

    // get(row, col)
    println("Matrix[1, 0] = ${matrix[1, 0]}")
}

// Простая матрица 2x2 на одномерном массиве
private class Matrix2x2 {
    private val data = IntArray(4)

    // Чтение: m[row, col]
    operator fun get(row: Int, col: Int): Int {
        checkBounds(row, col)
        return data[row * 2 + col]
    }

    // Запись: m[row, col] = value
    operator fun set(row: Int, col: Int, value: Int) {
        checkBounds(row, col)
        data[row * 2 + col] = value
    }

    private fun checkBounds(r: Int, c: Int) {
        if (r !in 0..1 || c !in 0..1) throw IndexOutOfBoundsException("$r,$c")
    }
}

/*
 * Invoke Operator (()): помнить
 * Позволяет вызывать объект как функцию.
 * Часто используется в DSL, или когда объект представляет собой "Действие" (UseCase, Command).
 */
private fun demoInvoke() {
    val greeter = Greeter("Welcome")

    // Вызов объекта как функции!
    greeter("Serkan") // эквивалентно greeter.invoke("Serkan")

    // Полезно в Gradle скриптах и конфигураторах
    val config = Config()
    config {
        println("Configuring inside invoke lambda...")
    }
}

private class Greeter(val greeting: String) {
    operator fun invoke(name: String) {
        println("$greeting, $name!")
    }
}

private class Config {
    operator fun invoke(block: () -> Unit) {
        block()
    }
}

/*
 * Comparison (CompareTo): помнить
 * Чтобы работали операторы >, <, >=, <=, класс должен реализовать Comparable<T>.
 * Либо просто иметь метод compareTo, возвращающий Int.
 */
private fun demoComparison() {
    val m1 = Money(100)
    val m2 = Money(200)

    println("m1 < m2: ${m1 < m2}")  // m1.compareTo(m2) < 0
    println("m1 >= m2: ${m1 >= m2}") // m1.compareTo(m2) >= 0

    // Сортировка работает автоматически, т.к. Comparable
    val list = listOf(Money(500), Money(100), Money(300))
    println("Sorted money: ${list.sorted()}")
}

private data class Money(val amount: Int) : Comparable<Money> {
    override fun compareTo(other: Money): Int {
        return this.amount - other.amount
    }

    override fun toString(): String = "$$amount"
}
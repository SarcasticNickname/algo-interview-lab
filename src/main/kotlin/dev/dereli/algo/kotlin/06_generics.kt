package dev.dereli.algo.kotlin

/**
 * Файл: 06_generics.kt
 * Тема: Обобщения (Generics), Вариантность (in/out), Стирание типов (Type Erasure).
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: GENERICS & VARIANCE
 * =========================================================================================
 *
 * 1. Зачем нужны Дженерики?
 *    - Для создания кода, который работает с разными типами (параметризованный код).
 *    - Главная цель: Обеспечить безопасность типов (Type Safety) на этапе компиляции,
 *      избегая ручных приведений типов (cast) и ошибок ClassCastException в рантайме.
 *
 * 2. Type Erasure (Стирание типов) — Главная проблема:
 *    - Из-за совместимости с Java, в JVM информация о дженериках (List<String>) стирается
 *      во время компиляции и превращается в `List<Object>`.
 *    - Следствие: В рантайме НЕЛЬЗЯ проверить `if (obj is List<String>)`.
 *    - Решение в Kotlin: `reified` типы в `inline` функциях (см. ниже).
 *
 * 3. Variance (Вариантность) — Как типы соотносятся друг с другом:
 *    Представим, что `Cat` — это подтип `Animal`.
 *
 *    - a) Invariance (Инвариантность) — по умолчанию.
 *      `MutableList<Cat>` НЕ ЯВЛЯЕТСЯ подтипом `MutableList<Animal>`.
 *      Причина: Безопасность. Если бы это было разрешено, можно было бы сделать
 *      `val animals: MutableList<Animal> = cats` и потом `animals.add(Dog())`.
 *      Мы бы положили собаку в список кошек.
 *
 *    - b) Covariance (`out`) — "Продюсер".
 *      `List<out T>` означает, что T можно только ЧИТАТЬ (produce).
 *      `List<Cat>` ЯВЛЯЕТСЯ подтипом `List<Animal>`.
 *      Логика: Если я могу прочитать из списка кошку, я точно могу прочитать её как животное.
 *      Это безопасно, так как писать в такой список нельзя.
 *
 *    - c) Contravariance (`in`) — "Консьюмер".
 *      `Comparator<in T>` означает, что T можно только ПИСАТЬ (consume).
 *      `Comparator<Animal>` ЯВЛЯЕТСЯ подтипом `Comparator<Cat>`.
 *      Логика: Если у меня есть компаратор, который умеет сравнивать ЛЮБЫХ животных,
 *      он точно справится со сравнением двух кошек.
 *
 * 4. Star Projection (`*`):
 *    - `List<*>` означает "список чего-то неизвестного".
 *    - Используется, когда тип не важен, но нужно безопасно работать с коллекцией.
 *    - Читать можно только как `Any?`. Писать ничего нельзя.
 * =========================================================================================
 */

fun main() {
    println("--- Basic Generics ---")
    demoGenericClassAndFunc()

    println("\n--- Constraints (Upper Bound) ---")
    demoConstraints()

    println("\n--- Why Invariance? (Safety) ---")
    demoInvarianceProblem()

    println("\n--- Variance: OUT (Producer) ---")
    demoVarianceOut()

    println("\n--- Variance: IN (Consumer) ---")
    demoVarianceIn()

    println("\n--- Star Projection (*) ---")
    demoStarProjection()

    println("\n--- Reified (Inline magic) ---")
    demoReified()
}

// --- 1. Basic Generics ---

/*
 * <T> - это "placeholder". Имя T (Type) - конвенция, можно назвать <E> (Element) или <Key>.
 */
private fun demoGenericClassAndFunc() {
    val intBox = Box(1)
    val strBox = Box("Kotlin")
    println("Boxes: ${intBox.value}, ${strBox.value}")

    val item = lastItem(listOf("A", "B", "C"))
    println("Generic func result: $item")
}

// Класс-контейнер
private class Box<T>(val value: T)

// Обобщенная функция
private fun <T> lastItem(list: List<T>): T? {
    if (list.isEmpty()) return null
    return list[list.size - 1]
}

// --- 2. Constraints (Ограничения) ---

/*
 * Constraints: помнить
 * : Type -> Верхняя граница (Upper Bound). T должен быть наследником Type.
 * where ... -> Множественные ограничения.
 */
private fun demoConstraints() {
    println("max(5, 10) = ${findMax(5, 10)}")
    // findMax("a", 1) // Ошибка компиляции: Int не наследует String (и наоборот)

    printSortedAndBig(listOf("delta", "alpha", "omega"))
}

// Одиночное ограничение: T должен реализовывать Comparable<T>
private fun <T : Comparable<T>> findMax(a: T, b: T): T {
    return if (a >= b) a else b
}

// Множественные ограничения через 'where'
// T должен быть и CharSequence (строкой/буфером), и Comparable
private fun <T> printSortedAndBig(list: List<T>)
        where T : CharSequence,
              T : Comparable<T> {
    val sorted = list.sorted()
    println("Sorted items len > 3: ${sorted.filter { it.length > 3 }}")
}

// --- 3. Invariance Problem (Почему это важно?) ---

/*
 * Почему MutableList<String> != MutableList<Any>?
 * Если бы это было разрешено, мы могли бы положить Int в список строк.
 */
private fun demoInvarianceProblem() {
    val strings: MutableList<String> = mutableListOf("A", "B")

    // Внимание: следующая строка НЕ скомпилируется в Kotlin (Инвариантность защищает нас)
    // val anyList: MutableList<Any> = strings

    // Если бы скомпилировалась:
    // anyList.add(100) // Мы положили Int
    // val s: String = strings[2] // Runtime Crash! Мы ожидаем String, а там Int.

    println("Invariance protects memory: MutableList<Subtype> is NOT MutableList<Supertype>")
}

// --- 4. Variance: OUT (Covariance) ---

/*
 * OUT (Producer): помнить
 * Ключевое слово `out T`.
 * - Мы обещаем только ЧИТАТЬ T (возвращать из функций).
 * - Мы НЕ будем принимать T в аргументы функций.
 * Результат: Producer<Cat> ЯВЛЯЕТСЯ подтипом Producer<Animal>.
 * Пример: Kotlin List (он immutable, поэтому он out).
 */
private fun demoVarianceOut() {
    val stringSource: Source<String> = Source("Data")

    // Безопасно: Source<String> -> Source<Any>
    // Мы можем прочитать String как Any.
    val anySource: Source<Any> = stringSource

    println("Source out: ${anySource.produce()}")
}

// Класс только "производит" T
private class Source<out T>(val value: T) {
    fun produce(): T = value
    // fun consume(item: T) {} // ОШИБКА! Нельзя принимать T, так как класс out.
}

// --- 5. Variance: IN (Contravariance) ---

/*
 * IN (Consumer): помнить
 * Ключевое слово `in T`.
 * - Мы обещаем только ПОТРЕБЛЯТЬ T (принимать в аргументы).
 * - Мы НЕ будем возвращать T (точнее, можем только как Any?).
 * Результат: Consumer<Animal> ЯВЛЯЕТСЯ подтипом Consumer<Cat>.
 * Логика: Если я умею обрабатывать Любое Животное, я точно сумею обработать Кошку.
 */
private fun demoVarianceIn() {
    val numberPrinter: Printer<Number> = Printer()

    // Безопасно: Printer<Number> -> Printer<Double>
    // Если принтер печатает любые числа, он напечатает и Double.
    val doublePrinter: Printer<Double> = numberPrinter

    doublePrinter.print(3.14) // Ожидает Double, мы даем Double. Реальный объект (numberPrinter) умеет Number.
}

private class Printer<in T> {
    fun print(item: T) {
        println("Printing: $item")
    }
}

// --- 6. Star Projection (*) ---

/*
 * Star (*): помнить
 * Используется, когда нам НЕ ВАЖЕН конкретный тип generic'а.
 * List<*> это "список чего-то".
 * - Читать можно только как Any?
 * - Писать нельзя ничего (кроме null).
 */
private fun demoStarProjection() {
    val list: List<Int> = listOf(1, 2, 3)
    printListSize(list)
}

fun printListSize(list: List<*>) {
    // val item: Int = list[0] // Ошибка! Мы не знаем, что там Int.
    val item: Any? = list.firstOrNull() // Безопасно только Any?
    println("List<*> size: ${list.size}, first=$item")
}

// --- 7. Reified (Inline Magic) ---

/*
 * Reified: помнить
 * В Java/Kotlin дженерики стираются (List<String> -> List).
 * Из-за этого нельзя написать: "if (obj is T)" или "T::class.java".
 *
 * Решение: inline fun + reified T.
 * Компилятор вставляет код функции в место вызова, подставляя РЕАЛЬНЫЙ тип вместо T.
 * Работает ТОЛЬКО с inline функциями.
 */
private fun demoReified() {
    printType<String>()
    printType<Int>()

    val list = listOf("A", 1, "B", 2.0)
    val strings = filterByType<String>(list)
    println("Filtered strings: $strings")
}

private inline fun <reified T> printType() {
    // T::class доступно только благодаря reified
    println("Type is: ${T::class.simpleName}")
}

private inline fun <reified T> filterByType(list: List<Any>): List<T> {
    val result = mutableListOf<T>()
    for (item in list) {
        if (item is T) { // Обычный дженерик тут бы упал с ошибкой "Cannot check for instance of erased type"
            result.add(item)
        }
    }
    return result
}
package dev.dereli.algo.kotlin

/**
 * Файл: 01_types.kt
 * Тема: Система типов Kotlin, Null Safety, Примитивы vs Объекты.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: СИСТЕМА ТИПОВ
 * =========================================================================================
 *
 * 1. "Всё есть объект" (Conceptually):
 *    В Kotlin нет ключевых слов для примитивов (как `int`, `boolean` в Java). Мы всегда пишем
 *    Int, Boolean, Double.
 *    ОДНАКО: Компилятор Kotlin умный. Где возможно, он превращает Int в примитивный `int` JVM
 *    для производительности. В объект (`java.lang.Integer`) он превращается только если:
 *    - Тип Nullable (Int?) — примитив не может быть null.
 *    - Используется в Дженериках (List<Int>) — коллекции Java работают только с объектами.
 *
 * 2. Строгая типизация и Отсутствие неявного приведения:
 *    Kotlin НЕ разрешает присвоить Int в переменную Long автоматически.
 *    В Java: `long l = 10;` (ОК).
 *    В Kotlin: `val l: Long = 10` (Ошибка). Нужно: `val l: Long = 10.toLong()`.
 *    Это сделано, чтобы избежать случайных ошибок потери точности и неожиданных сравнений.
 *
 * 3. Null Safety (Безопасность от Null):
 *    Система типов делит мир на две вселенные:
 *    - T (NotNull): никогда не хранит null.
 *    - T? (Nullable): может хранить null.
 *    Это проверяется на этапе КОМПИЛЯЦИИ. Если вы попытаетесь вызвать метод у T? без проверки,
 *    код просто не скомпилируется. Никаких NPE в рантайме (почти).
 *
 * 4. Иерархия типов:
 *    - Any: Супер-класс для всего (даже для Int и String). Аналог Object, но легче.
 *    - Unit: Аналог void. Возвращается функциями, которые ничего не возвращают.
 *    - Nothing: Тип, у которого нет экземпляров. Означает "функция никогда не вернет управление"
 *      (вечный цикл или выброс исключения).
 * =========================================================================================
 */

fun main() {
    println("--- Numbers & Casting ---")
    demoNumbers()

    println("\n--- Unsigned Types ---")
    demoUnsignedTypes()

    println("\n--- Char & String ---")
    demoCharString()

    println("\n--- Boolean Logic ---")
    demoBoolean()

    println("\n--- Type Inference ---")
    demoTypeInference()

    println("\n--- Null Safety (The Billion Dollar Fix) ---")
    demoNullability()

    println("\n--- Type Hierarchy (Any, Unit, Nothing) ---")
    demoAnyUnitNothing()

    println("\n--- Equality (Structural vs Referential) ---")
    demoEquality()

    println("\n--- Ranges & Boxing ---")
    demoRangesAndBoxing()
}

/*
 * Numbers (Числа):
 * Основные моменты:
 * 1. Типы: Byte(8), Short(16), Int(32), Long(64) | Float(32), Double(64).
 * 2. Литералы помогают компилятору понять тип: 100L (Long), 3.14F (Float), 0xFF (Hex).
 * 3. Деление целых чисел всегда возвращает целое (отбрасывает остаток).
 */
private fun demoNumbers() {
    val i: Int = 42
    val l: Long = 42L
    val d: Double = 3.14
    val f: Float = 3.14F
    val hex: Int = 0xFF   // 255 в десятичной
    val bin: Int = 0b1010 // 10 в десятичной

    // Пример строгой типизации:
    // val brokenLong: Long = i // ОШИБКА КОМПИЛЯЦИИ: Type mismatch.

    // Правильный способ: Явное преобразование
    val iToLong: Long = i.toLong()
    println("Numbers: i=$i, l=$l, hex=$hex, bin=$bin")

    // Деление целых чисел
    // Если нужно дробное, хотя бы один операнд должен быть Double/Float
    val divInt = 5 / 2      // = 2
    val divDouble = 5.0 / 2 // = 2.5
    println("Division: 5/2=$divInt, 5.0/2=$divDouble")

    // Сравнение разных типов невозможно без приведения
    // println(42 == 42L) // Ошибка: Operator '==' cannot be applied to 'Int' and 'Long'
    println("Compare Int vs Long: ${42.toLong() == 42L}")
}

/*
 * Unsigned Types (Беззнаковые типы):
 * - Появились стабильно в Kotlin 1.5.
 * - UByte, UShort, UInt, ULong.
 * - Позволяют использовать весь диапазон битов для положительных чисел.
 *   (Int max ~2 млрд, UInt max ~4 млрд).
 * - Под капотом это Inline Classes, хранящие обычные примитивы, но меняющие логику операций.
 */
private fun demoUnsignedTypes() {
    val uInt: UInt = 42u
    val uLong: ULong = 1000uL

    // Пример переполнения, которое работает корректно для беззнаковой логики
    // 0 - 1 для UInt будет UInt.MAX_VALUE
    val zero: UInt = 0u
    // println(zero - 1u) // вернет 4294967295

    println("UInt max: ${UInt.MAX_VALUE}")
    println("Int max:  ${Int.MAX_VALUE}")
}

/*
 * Char & String:
 * - Char: это НЕ число в смысле арифметики (как в C++). 'A' + 1 работает, но 'A' * 2 - нет.
 *   Чтобы получить код символа, используем .code.
 * - String: Неизменяемая последовательность Char.
 * - Raw String ("""..."""): Игнорирует спецсимволы (\n, \t). Удобно для JSON, SQL, RegEx.
 */
private fun demoCharString() {
    val ch: Char = 'K'
    // val i: Int = ch // Ошибка!
    val code: Int = ch.code

    val s: String = "Kotlin"
    println("Char code of '$ch' is $code")

    // String Templates (Интерполяция строк)
    // $var - для простой переменной
    // ${expr} - для выражения
    println("String info: s=$s, length=${s.length}, upper=${s.uppercase()}")

    // Raw String + trimIndent() убирает отступ слева, выравнивая текст
    val json = """
        {
            "id": 1,
            "name": "Serkan"
        }
    """.trimIndent()
    println("Raw JSON:\n$json")
}

/*
 * Boolean:
 * - Стандартные true/false.
 * - Ленивость (Short-circuit):
 *   (funcA() || funcB()) -> если funcA() вернула true, funcB() НЕ ВЫЗЫВАЕТСЯ.
 *   Это критично, если funcB() тяжелая или имеет побочные эффекты.
 */
private fun demoBoolean() {
    val a = true
    val b = false
    println("Logic: AND=${a && b}, OR=${a || b}, NOT=${!a}")
}

/*
 * Type Inference (Вывод типов):
 * - Компилятор анализирует правую часть и приписывает тип переменной.
 * - Это работает статически (в момент компиляции), тип фиксируется навсегда.
 * - Если вы не инициализируете переменную сразу, тип нужно указать явно.
 */
private fun demoTypeInference() {
    val x = 10      // Inferred as Int
    val y = 10.0    // Inferred as Double
    val z = "Text"  // Inferred as String

    // var w // Ошибка: This variable must either have a type annotation or be initialized

    println("Inferred types: x is Int=${x is Int}, y is Double=${y is Double}")
}

/*
 * Nullability (Null-Safety):
 * Самая важная часть.
 * 1. Safe Call (?.): "Сделай, если не null, иначе верни null".
 * 2. Elvis Operator (?:): "Если слева null, возьми то, что справа".
 * 3. Not-null Assertion (!!): "Я гарантирую, что тут не null". Если ошибешься -> NPE.
 */
private fun demoNullability() {
    val notNull: String = "Always data"
    // notNull = null // Ошибка компиляции

    // Имитация данных с сервера (может прийти null)
    val response: String? = if (System.currentTimeMillis() % 2 == 0L) "ServerData" else null

    // 1. Безопасное получение длины
    val len: Int? = response?.length

    // 2. Elvis: предоставление запасного варианта
    // Если response?.length вернет null, возьмем 0.
    val lenOrZero: Int = response?.length ?: 0

    println("Nullability: value='$response'")
    println("Safe length: $len")
    println("Length or Default: $lenOrZero")

    // 3. !! (Опасная зона)
    // Используем только если на 100% уверены (например, после проверки в коде, которую компилятор не увидел)
    try {
        val l = response!!.length
        println("Force unwrapped length: $l")
    } catch (e: NullPointerException) {
        println("Caught NPE from !! operator")
    }
}

/*
 * Any, Unit, Nothing:
 * - Any: Родитель всех классов.
 * - Unit: "Ничего полезного". Возвращается методами типа print(). Это объект (синглтон).
 * - Nothing: "Ничего вообще". Используется для функций, которые прерывают исполнение (throw).
 *   Позволяет компилятору понять: "после вызова этой функции код дальше не пойдет".
 */
private fun demoAnyUnitNothing() {
    val obj: Any = "Hello Any"
    // Smart Cast: мы проверили `is String`, и внутри if `obj` уже считается String
    if (obj is String) {
        println("Any is actually a String of length: ${obj.length}")
    }

    val u: Unit = printAndReturnUnit("Demonstrating Unit")
    println("Unit value is: $u") // kotlin.Unit

    // fail("Stop here") // раскомментируй, чтобы проверить краш
}

private fun printAndReturnUnit(text: String): Unit {
    println(text)
    // return Unit // Неявно добавляется компилятором
}

private fun fail(message: String): Nothing {
    throw IllegalStateException(message)
    // return здесь невозможен
}

/*
 * Equality (Равенство):
 * - == (Structural Equality): Транслируется в вызов .equals(). Безопасно для null.
 * - === (Referential Equality): Сравнивает адреса памяти.
 *
 * Внимание: JVM Integer Cache.
 * Java (и Kotlin) кеширует объекты Integer в диапазоне -128..127.
 * Поэтому `===` может вернуть true для маленьких чисел, но false для больших.
 */
private fun demoEquality() {
    val s1 = "Kotlin"
    val s2 = "Kot" + "lin" // Компилятор/Runtime может оптимизировать это в одну ссылку (String Pool)

    println("Strings '==' : ${s1 == s2}")  // true (содержимое)
    println("Strings '===': ${s1 === s2}") // скорее всего true (String Pool)

    // Тест Integer Cache
    val a: Int? = 100
    val b: Int? = 100
    println("Boxed 100 === 100: ${a === b}") // TRUE (взяты из кеша)

    val x: Int? = 1000
    val y: Int? = 1000
    println("Boxed 1000 === 1000: ${x === y}") // FALSE (новые объекты в куче)
    println("Boxed 1000 == 1000: ${x == y}")   // TRUE (значения равны)
}

/*
 * Ranges (Диапазоны) & Boxing:
 * - 1..5 — Range (Interval).
 * - Boxing: Generic коллекции (List<Int>) хранят ОБЪЕКТЫ (Integer).
 *   Это создает нагрузку на память.
 *   IntArray хранит примитивы (int[]), это намного эффективнее.
 */
private fun demoRangesAndBoxing() {
    val n = 5
    // When expression
    val res = when (n) {
        in 1..10 -> "In range 1..10"
        !in 20..30 -> "Not in 20..30"
        else -> "Unknown"
    }
    println("Range check: $res")

    // Boxing example
    val a: Int = 10     // primitive int
    val b: Int? = null  // null reference (boxed)

    // b ?: 0 распаковывает b, если там есть значение, или берет 0
    val sum = a + (b ?: 0)
    println("Sum with nullable: $sum")

    // Эффективность массивов
    val list: List<Int> = listOf(1, 2, 3) // List<Integer> (Boxed overhead)
    val array: IntArray = intArrayOf(1, 2, 3) // int[] (Efficient)
    println("IntArray is efficient: ${array.joinToString()}")
}
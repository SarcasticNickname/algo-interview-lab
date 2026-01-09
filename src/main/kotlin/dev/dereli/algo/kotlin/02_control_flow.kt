package dev.dereli.algo.kotlin

/**
 * Файл: 02_control_flow.kt
 * Тема: Управляющие конструкции (If, When, Loops).
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: CONTROL FLOW
 * =========================================================================================
 *
 * 1. Expression vs Statement (Выражение vs Инструкция):
 *    - В Java `if` — это инструкция (statement), она просто управляет потоком, но не имеет значения.
 *    - В Kotlin `if` и `when` — это выражения (expressions). Они ВОЗВРАЩАЮТ результат вычисления.
 *    - Именно поэтому в Kotlin нет тернарного оператора `condition ? true : false`. Его заменяет `if (a) b else c`.
 *
 * 2. When (Supercharged Switch):
 *    - Заменяет `switch-case` и цепочки `if-else-if`.
 *    - Автоматический Smart Cast внутри ветки (если проверили `is String`, внутри это уже String).
 *    - Если `when` используется как выражение (присваивается переменной), ветка `else` обязательна
 *      (если компилятор не уверен, что покрыты все варианты, например, как в Enum/Sealed).
 *    - `break` не нужен. Выполняется только первая подошедшая ветка.
 *
 * 3. Loops (Циклы):
 *    - Нет C-style цикла `for (int i=0; i<n; i++)`.
 *    - `for` работает через итераторы (как `foreach` в Java).
 *    - Диапазоны (`Ranges`) оптимизируются компилятором в обычные циклы с индексом, где это возможно,
 *      поэтому производительность не страдает.
 * =========================================================================================
 */

fun main() {
    println("--- If Expression ---")
    demoIfExpression()

    println("\n--- When (Value matching) ---")
    demoWhenValue()

    println("\n--- When (Condition matching & Smart Casts) ---")
    demoWhenType()

    println("\n--- Basic Loops (Ranges) ---")
    demoBasicLoops()

    println("\n--- Advanced Loops (Destructuring) ---")
    demoAdvancedLoops()

    println("\n--- Labels (Nested loops control) ---")
    demoBreakContinueLabels()
}

/*
 * if-expression: помнить
 * 1. if возвращает значение ПОСЛЕДНЕГО выражения в выбранной ветке блока.
 * 2. Если if используется как выражение (присваивается в val), ветка `else` ОБЯЗАТЕЛЬНА.
 *    (Иначе что вернется, если условие false? Unit? Это сломает типизацию).
 */
private fun demoIfExpression() {
    val n = 7

    // Как выражение (замена тернарного оператора)
    // Тип переменной sign выводится как String
    val sign = if (n > 0) "non-negative" else "negative"
    println("if-expression: n=$n, sign: $sign")

    val max = if (n > 10) n else 10
    println("max via if = $max")

    // Блоки кода в ветках
    // Результатом будет последняя строка блока
    val result = if (n > 10) {
        println("Calculating large number logic...")
        n * 2 // Возвращаемое значение
    } else {
        println("Calculating small number logic...")
        0     // Возвращаемое значение
    }
    println("Result block: $result")
}

/*
 * when(value): помнить
 * - Аналог switch(value).
 * - Аргумент может быть любым (Int, String, Object).
 * - Можно группировать значения через запятую: `1, 2 ->`.
 * - Можно проверять вхождение в диапазон: `in 1..10`.
 * - `else` обязателен, если `when` используется как выражение.
 */
private fun demoWhenValue() {
    val day = 6

    val name = when (day) {
        1 -> "Monday"
        2 -> "Tuesday"
        3, 4, 5 -> "Mid-week" // Группировка нескольких значений
        in 6..7 -> "Weekend"  // Проверка диапазона
        else -> "Unknown"     // Дефолтная ветка (default)
    }
    println("when(value): day=$day => $name")

    val n = 42
    val bucket = when (n) {
        in 0..9 -> "Single digit"
        in 10..99 -> "Double digit"
        else -> "Large or negative"
    }
    println("when(range): n=$n => $bucket")
}

/*
 * when(без аргумента): помнить
 * - Аналог цепочки if (cond1) { ... } else if (cond2) { ... }.
 * - Удобнее и читабельнее, чем if-else-if.
 * - Здесь же работает Smart Cast: после проверки `is String` переменная внутри ветки
 *   считается строкой, и можно вызывать `.length` без каста.
 */
private fun demoWhenType() {
    val x: Any = "kotlin" // Тип Any, но внутри лежит String

    val result = when {
        // Проверка условия
        x is String && x.length >= 5 -> "String (len >= 5)"

        // Smart Cast: x автоматически скастился к String
        x is String -> "String (len < 5), actual len=${x.length}"

        x is Int -> "It's an Integer"

        else -> "Other type"
    }
    println("when(no value): x=$x => $result")
}

/*
 * Basic Loops: помнить
 * - `..` создает диапазон (Range) ВКЛЮЧИТЕЛЬНО.
 * - `until` создает диапазон ИСКЛЮЧАЯ верхнюю границу (удобно для индексов массивов 0..size-1).
 * - `downTo` для обратного цикла.
 * - `step` меняет шаг (по умолчанию 1).
 */
private fun demoBasicLoops() {
    print("Range (1..5): ")
    for (i in 1..5) print("$i ") // 1 2 3 4 5

    print("\nUntil (0 until 3): ")
    for (i in 0 until 3) print("$i ") // 0 1 2 (3 не включено)

    print("\nDownTo + Step (6 downTo 0 step 2): ")
    for (i in 6 downTo 0 step 2) print("$i ") // 6 4 2 0
    println()

    // Классические while / do-while
    var x = 2
    while (x > 0) {
        print("w$x ")
        x--
    }

    var y = 1
    do {
        print("d$y ")
        y--
    } while (y > 0)
}

/*
 * Advanced Loops: помнить (ОЧЕНЬ ВАЖНО ДЛЯ АЛГОРИТМОВ)
 * 1. withIndex(): Позволяет получить индекс элемента без ручного счетчика.
 * 2. Destructuring Declaration: `for ((k, v) in map)` — это распаковка Map.Entry на лету.
 *    Это намного чище, чем `entry.key` и `entry.value`.
 */
private fun demoAdvancedLoops() {
    val list = listOf("a", "b", "c")

    // Деструктуризация IndexedValue
    println("\nWithIndex:")
    for ((index, value) in list.withIndex()) {
        println("  list[$index] = $value")
    }

    val map = mapOf(1 to "One", 2 to "Two")
    println("Map iteration:")
    for ((key, value) in map) {
        println("  key=$key, val=$value")
    }
}

/*
 * Labels (Метки): помнить
 * - Позволяют управлять потоком во вложенных циклах.
 * - break@label: выпрыгивает сразу из внешнего цикла (помеченного меткой).
 * - continue@label: переходит к следующей итерации внешнего цикла.
 * - Без меток break прерывает только самый внутренний цикл.
 * Use Case: Поиск элемента в двумерной матрице. Нашли -> break@outer.
 */
private fun demoBreakContinueLabels() {
    println("\nLabels demo:")
    outer@ for (i in 1..3) {
        for (j in 1..3) {
            // Пример continue
            if (i == 2 && j == 2) {
                println("  [continue outer] at i=$i, j=$j")
                continue@outer // Пропускаем остаток inner цикла и переходим к i=3
            }
            // Пример break
            if (i == 3 && j == 1) {
                println("  [break outer] at i=$i, j=$j")
                break@outer // Полный выход из обоих циклов
            }
            println("  i=$i, j=$j")
        }
    }
}
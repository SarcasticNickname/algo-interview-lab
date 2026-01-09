package dev.dereli.algo.kotlin

/**
 * Файл: 03_functions.kt
 * Тема: Функции, Аргументы, Extensions, Infix, Varargs, Tailrec.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: ФУНКЦИИ
 * =========================================================================================
 *
 * 1. "Граждане первого класса":
 *    Функции в Kotlin могут существовать вне классов (Top-level functions).
 *    Им не нужен класс-обертка (как статические методы в Java).
 *
 * 2. Extension Functions (Функции-расширения):
 *    - Позволяют "дописать" метод в класс, к которому у вас нет доступа (String, View, List).
 *    - ВАЖНО: Это "Синтаксический сахар". Под капотом создается статическая функция,
 *      где объект передается первым аргументом (`public static void lastChar(String this$0)`).
 *    - Static Dispatch: Вызываемая функция определяется по ТИПУ ПЕРЕМЕННОЙ, а не по реальному типу объекта в памяти.
 *
 * 3. Default Arguments:
 *    - Позволяют задавать значения по умолчанию.
 *    - Убивают необходимость в паттерне "Telescoping Constructor" (множество конструкторов с разным числом параметров)
 *      и паттерне "Builder".
 *
 * 4. Tail Recursion (tailrec):
 *    - Оптимизация компилятора. Если рекурсивный вызов — это последнее действие,
 *      компилятор разворачивает рекурсию в цикл `while`.
 *    - Это спасает от переполнения стека (StackOverflowError) в алгоритмах.
 * =========================================================================================
 */

fun main() {
    println("--- Basic & Single Expression ---")
    demoBasic()
    demoSingleExpression()

    println("\n--- Default & Named Args ---")
    demoDefaultAndNamedArgs()

    println("\n--- Extension Functions (Static Dispatch) ---")
    demoExtensionFunctions()

    println("\n--- Infix Functions (DSL style) ---")
    demoInfixFunctions()

    println("\n--- Varargs & Spread Operator ---")
    demoVarargs()

    println("\n--- Local Functions & Tailrec (Algo tools) ---")
    demoLocalAndTailrec()
}

/*
 * Basics: помнить
 * - fun name(params): ReturnType
 * - Если функция ничего не возвращает, тип Unit (аналог void). Его можно не писать.
 */
private fun demoBasic() {
    val a = 3
    val b = 5
    val sum = add(a, b)
    println("add($a, $b) = $sum")
}

private fun add(x: Int, y: Int): Int {
    return x + y
}

/*
 * Single-expression functions: помнить
 * - Если тело функции — одно выражение, фигурные скобки можно опустить.
 * - Тип возвращаемого значения выводится автоматически (Type Inference).
 * - Очень часто используется для геттеров или маленьких утилит.
 */
private fun demoSingleExpression() {
    // Полная форма: fun square(x: Int): Int { return x * x }
    println("square(7) = ${square(7)}")
}

private fun square(x: Int) = x * x

/*
 * Default + Named args: помнить
 * - Параметрам можно дать дефолтные значения.
 * - Named Arguments: при вызове можно указывать имена параметров. Это позволяет:
 *   1. Менять порядок аргументов.
 *   2. Пропускать дефолтные аргументы.
 *   3. Делать код читабельным (понятно, что значит true/false).
 */
private fun demoDefaultAndNamedArgs() {
    // 1. Позиционные аргументы (как в Java)
    println(greet("Serkan"))

    // 2. Именованные аргументы (читабельность + пропуск дефолтного name)
    println(greet(punctuation = "!!!", name = "Developer"))

    // 3. Смешанный стиль (лучше не злоупотреблять) и использование всех дефолтов
    println(greet())
}

private fun greet(name: String = "Guest", punctuation: String = "!"): String {
    return "Hello, $name$punctuation"
}

/*
 * Extension Functions (Расширения): помнить !!!
 * - `this` внутри функции — это объект, у которого вызвали функцию (Receiver).
 * - Доступ к private/protected полям класса ЗАПРЕЩЕН (так как это просто внешняя статика).
 * - Если у класса уже есть метод с таким же именем и сигнатурой, Extension НЕ вызовется (метод класса имеет приоритет).
 */
private fun demoExtensionFunctions() {
    val text = "Kotlin"
    println("Last char of '$text' is '${text.lastChar()}'")

    // Пример расширения примитива
    println("Is 5 even? ${5.isEven()}")
}

// Расширяем класс String
private fun String.lastChar(): Char {
    // this - это сама строка
    return this.get(this.length - 1)
}

// Расширяем класс Int
private fun Int.isEven(): Boolean = this % 2 == 0


/*
 * Infix functions: помнить
 * Делает код похожим на естественный язык. Используется для DSL и утилит (to, until, step).
 * Требования:
 * 1. Функция-член (Member) или Расширение (Extension).
 * 2. Ровно ОДИН параметр.
 * 3. Помечена ключевым словом `infix`.
 */
private fun demoInfixFunctions() {
    // Стандартная infix функция 'to' создает Pair<A, B>
    val pair = 1 to "one" // эквивалент 1.to("one")

    // Наша кастомная функция
    val result = 5 vs 3
    println("Battle: 5 vs 3 -> $result")
}

// Пример infix функции
private infix fun Int.vs(other: Int): String {
    return if (this > other) "$this wins" else "$other wins"
}

/*
 * Varargs (Variable arguments): помнить
 * - vararg позволяет передавать N аргументов через запятую.
 * - Внутри функции это массив (Array<T>), а не List.
 * - Spread Operator (*):
 *   Если у вас уже есть массив, и вы хотите передать его содержимое в vararg,
 *   нужно поставить `*` перед переменной. Это распаковывает массив.
 */
private fun demoVarargs() {
    printAll("A", "B", "C")

    val array = arrayOf("x", "y", "z")
    // printAll(array) // ОШИБКА: ожидается String, а пришел Array<String>

    printAll(*array) // Spread operator: содержимое массива передается как отдельные аргументы
}

private fun printAll(vararg messages: String) {
    // messages имеет тип Array<out String>
    for (m in messages) print("$m ")
    println()
}

/*
 * Local functions & Tail Recursion: помнить
 *
 * 1. Local Functions:
 *    - Функция внутри функции. Имеет доступ к локальным переменным внешней функции (Closure).
 *    - Идеально для алгоритмов (DFS, BFS), чтобы не засорять класс вспомогательными методами
 *      и не таскать состояние (state) в аргументах.
 *
 * 2. Tail Recursion (tailrec):
 *    - Если функция вызывает саму себя как ПОСЛЕДНЮЮ операцию, стек не растет.
 *    - Это превращается в цикл.
 *    - Если забыть модификатор tailrec, код будет работать, но может упасть с StackOverflowError
 *      на больших данных.
 */
private fun demoLocalAndTailrec() {
    // Локальная функция
    fun dfs(node: Int) {
        println("  Visiting node $node (inside local fun)")
        // Тут можно было бы использовать переменные из demoLocalAndTailrec
    }
    dfs(1)

    // Tail recursion
    println("  GCD(12, 18) = ${gcd(12, 18)}")
}

// Алгоритм Евклида (НОД)
// tailrec работает, т.к. вызов gcd(...) - это единственное, что возвращается (нет сложения/умножения после вызова)
private tailrec fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}
package dev.dereli.algo.kotlin

/**
 * Файл: 05_oop_basics.kt
 * Тема: Классы, Объекты, Наследование, Интерфейсы, Модификаторы доступа.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: ООП В KOTLIN
 * =========================================================================================
 *
 * 1. Classes & Properties:
 *    - В Kotlin нет "полей" (fields) в публичном API, есть "свойства" (properties).
 *    - `val` = поле + геттер.
 *    - `var` = поле + геттер + сеттер.
 *    - `field` = ключевое слово (backing field), доступное только внутри get/set.
 *
 * 2. Constructors:
 *    - Primary Constructor (в заголовке класса) — основной и рекомендуемый.
 *    - `init { ... }` — блок инициализации, выполняется сразу после primary конструктора.
 *    - Secondary Constructor (`constructor`) — нужен редко (для Java interop или перегрузки).
 *
 * 3. Inheritance (Наследование):
 *    - По умолчанию все классы — `final` (запрещены для наследования).
 *    - Чтобы разрешить наследование, нужен модификатор `open`.
 *    - Методы тоже `final` по умолчанию. Чтобы переопределить (`override`), метод должен быть `open`.
 *
 * 4. Data Classes:
 *    - Классы для хранения данных (DTO).
 *    - Компилятор генерирует: `equals`, `hashCode`, `toString`, `copy`, `componentN`.
 *    - Ограничение: нельзя наследовать (но могут реализовать интерфейсы).
 *
 * 5. Object & Companion Object:
 *    - `object` — это Singleton (ленивая инициализация при первом обращении).
 *    - `companion object` — аналог `static` методов/полей. Привязан к классу, но является объектом.
 *
 * 6. Sealed Classes/Interfaces:
 *    - "Enum на стероидах". Ограниченная иерархия.
 *    - Позволяет компилятору проверять исчерпываемость (`when` без `else`).
 *    - Sealed Interface лучше, если нет общего состояния (позволяет множественное наследование).
 * =========================================================================================
 */

fun main() {
    println("--- Class & Properties ---")
    demoClassAndProperties()

    println("\n--- Secondary Constructor ---")
    demoSecondaryConstructor()

    println("\n--- Data Class (Generated methods) ---")
    demoDataClass()

    println("\n--- Inheritance & Abstract Class ---")
    demoInheritanceAndInterface()

    println("\n--- Object & Companion (Singleton/Static) ---")
    demoObjectAndCompanion()

    println("\n--- Enum (Constants with state) ---")
    demoEnum()

    println("\n--- Sealed Class (Restricted Hierarchy) ---")
    demoSealed()
}

/*
 * Properties & Custom Accessors: помнить
 * - computed property (get() = ...) не занимает памяти, вычисляется каждый раз.
 * - custom setter позволяет добавить логику (валидацию, лог) при присваивании.
 */
private fun demoClassAndProperties() {
    val r = Rectangle(10, 20)
    println("Rect: square?=${r.isSquare}, area=${r.area}")

    r.color = "blue" // Вызывает кастомный setter
    println("Rect color: ${r.color}") // BLUE (setter сделал uppercase)
}

private class Rectangle(val width: Int, val height: Int) {
    // Computed property (нет backing field)
    val isSquare: Boolean
        get() = width == height

    // Computed property (expression body)
    val area get() = width * height

    // Property с кастомным сеттером
    var color: String = "White"
        set(value) {
            if (value.isBlank()) return
            // field - доступ к реальной ячейке памяти (backing field)
            field = value.uppercase()
        }
}

/*
 * Secondary Constructor: помнить
 * - Используется ключевое слово `constructor`.
 * - ОБЯЗАН делегировать в Primary Constructor через `: this(...)`.
 */
private fun demoSecondaryConstructor() {
    val v1 = View("Main")
    val v2 = View("Button", 100) // Вызов вторичного
    println("Views: $v1, $v2")
}

private class View(val title: String) {
    var width: Int = 0

    // Вторичный конструктор
    constructor(title: String, width: Int) : this(title) {
        this.width = width
    }

    override fun toString(): String = "View('$title', w=$width)"
}

/*
 * Data Class: помнить
 * - equals/hashCode генерируются только по полям в конструкторе.
 * - copy() позволяет создать копию, изм енив часть полей (иммутабельность).
 * - componentN() функции позволяют делать destructuring.
 */
private fun demoDataClass() {
    val s1 = UiState(isLoading = true, items = listOf("a", "b"))
    val s2 = s1.copy(isLoading = false) // Копия

    println("s1: $s1")
    println("s2: $s2")

    // Destructuring declaration
    val (loading, items) = s2
    println("Destructured: loading=$loading, items=$items")
}

private data class UiState(
    val isLoading: Boolean,
    val items: List<String>
    // Поля в теле класса НЕ участвуют в equals/toString!
) {
    var tempState: Int = 0
}

/*
 * Inheritance & Interfaces: помнить
 * - class/method по умолчанию final. Используй open.
 * - Interface: описывает поведение, нет state (полей). Может иметь default implementation.
 * - Abstract class: может иметь state и конструктор.
 */
private fun demoInheritanceAndInterface() {
    val repo: Repo = FileRepo()
    repo.log("Operation started") // Default method из интерфейса
    repo.save("UserData")
}

private interface Repo {
    // val id: Int = 1 // ОШИБКА: Interface can't have backing fields
    val name: String // Абстрактное свойство

    fun save(data: String)

    // Default implementation (как default в Java 8)
    fun log(msg: String) {
        println("[REPO LOG]: $msg")
    }
}

private abstract class BaseRepo : Repo {
    // Абстрактный класс может иметь состояние
    protected var lastUpdated: Long = 0
}

private class FileRepo : BaseRepo() {
    override val name: String = "FileRepository"

    override fun save(data: String) {
        lastUpdated = System.currentTimeMillis()
        println("Saved '$data' to file. Last updated: $lastUpdated")
    }
}

/*
 * Object & Companion: помнить
 * - object = Singleton. Инициализируется лениво (при первом доступе).
 * - companion object = Статика. Можно вызывать методы через ИмяКласса.method().
 * - const val = Compile-time constant (подставляется везде, как #define).
 */
private fun demoObjectAndCompanion() {
    println("Base URL: ${AppConfig.BASE_URL}")
    val p = Person.create("John Doe") // Factory method
    println("Person: $p")
}

private object AppConfig {
    const val BASE_URL = "https://api.example.com"
}

private class Person private constructor(val name: String) {
    // Companion object может реализовывать интерфейсы
    companion object Factory {
        fun create(name: String): Person = Person(name.trim())
    }
    override fun toString() = "Person($name)"
}

/*
 * Enum: помнить
 * - entries (Kotlin 1.9+) эффективнее, чем values() (не создает массив каждый раз).
 * - Можно переопределять методы для каждого элемента.
 */
private fun demoEnum() {
    val status = Status.IN_PROGRESS
    println("Status: $status, code=${status.code}, done?=${status.isFinished()}")

    // Поиск по коду
    println("Status 500: ${Status.fromCode(500)}")
}

private enum class Status(val code: Int) {
    NEW(0),
    IN_PROGRESS(1),
    DONE(200) {
        override fun isFinished() = true
    },
    ERROR(500) {
        override fun isFinished() = true
    };

    open fun isFinished() = false

    companion object {
        fun fromCode(code: Int): Status? = entries.find { it.code == code }
    }
}

/*
 * Sealed Class: помнить
 * - Все наследники известны на этапе компиляции.
 * - when(sealed) не требует else, если обработаны все ветки.
 * - Удобно для Result, UI State, Navigation.
 */
private fun demoSealed() {
    val result: OpResult = OpResult.Success(42)
    println("Result: ${handleResult(result)}")
}

private sealed class OpResult {
    data class Success(val data: Int) : OpResult()
    data class Error(val message: String) : OpResult()
    data object Loading : OpResult() // object экономит память (нет смысла создавать новые инстансы)
}

private fun handleResult(r: OpResult): String = when (r) {
    is OpResult.Success -> "OK: ${r.data}"
    is OpResult.Error -> "FAIL: ${r.message}"
    OpResult.Loading -> "WAIT..."
}
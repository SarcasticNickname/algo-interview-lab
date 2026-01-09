package dev.dereli.algo.kotlin

import kotlin.properties.Delegates

/**
 * Файл: 11_oop_advanced.kt
 * Тема: Продвинутые концепции ООП: Делегирование, Отложенная инициализация, Value Classes.
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: ADVANCED OOP
 * =========================================================================================
 *
 * 1. Delegation (Делегирование) — "Композиция вместо наследования" на стероидах.
 *    - Это механизм, при котором один объект (делегат) "передает" часть своих обязанностей
 *      другому объекту (делегату).
 *    - В Kotlin есть две формы:
 *      a) Class Delegation (`by`): `class MyList(inner: List) : List by inner`.
 *         Компилятор автоматически генерирует все методы интерфейса, которые вызывают
 *         соответствующие методы `inner`. Позволяет легко реализовать паттерн "Декоратор".
 *      b) Property Delegation (`by`): `val name: String by lazy { ... }`.
 *         Логика `get()` и `set()` свойства делегируется другому объекту.
 *         `lazy`, `observable` — это стандартные делегаты.
 *
 * 2. Отложенная инициализация (Lazy Initialization):
 *    - Это паттерн, при котором объект создается не в момент создания родителя, а только
 *      при первом обращении к нему.
 *    - a) `by lazy`:
 *         - Только для `val` (неизменяемых свойств).
 *         - Потокобезопасен по умолчанию (режим `SYNCHRONIZED`).
 *         - Идеально для тяжелых, дорогих в создании объектов (БД, DI-граф).
 *    - b) `lateinit var`:
 *         - Только для `var` (изменяемых свойств) и не-nullable типов.
 *         - Нельзя для примитивов (Int, Double).
 *         - Обещание компилятору: "я проинициализирую это свойство позже, до того, как его прочитают".
 *         - Если обещание нарушено -> `UninitializedPropertyAccessException`.
 *         - Идеально для полей, которые инициализируются фреймворком (Android `onCreate`, DI).
 *
 * 3. Value Classes (ex. Inline Classes):
 *    - Идея: Создать типобезопасную обертку над примитивом (`UserId(Int)`) БЕЗ
 *      накладных расходов на создание объекта в куче.
 *    - Компилятор (где возможно) "разворачивает" обертку и использует в байткоде
 *      напрямую примитив.
 *    - Это дает безопасность типов (`fun sendMoney(id: UserId)`) без потери производительности.
 *
 * 4. Nested vs Inner Classes:
 *    - `Nested` (по умолчанию): Статический вложенный класс. Не имеет доступа к `this` внешнего класса.
 *    - `Inner`: Нестатический вложенный класс. Хранит НЕЯВНУЮ ссылку на экземпляр внешнего класса.
 *      - Это позволяет обращаться к `private` полям родителя.
 *      - ВАЖНО: В Android это частая причина утечек памяти (Memory Leaks), если `Inner`-класс
 *        живет дольше, чем его родитель (например, Activity).
 * =========================================================================================
 */

fun main() {
    println("--- Interface Delegation (Decorator) ---")
    demoInterfaceDelegationBy()

    println("\n--- Lazy (by lazy) ---")
    demoPropertyDelegationLazy()

    println("\n--- Lateinit var ---")
    demoLateinit()

    println("\n--- Observable & Vetoable ---")
    demoObservableAndVetoable()

    println("\n--- Sealed Interface ---")
    demoSealedInterface()

    println("\n--- Value Class (Inline) ---")
    demoValueClass()

    println("\n--- Object Expression (Anonymous) ---")
    demoObjectExpression()

    println("\n--- Nested vs Inner ---")
    demoNestedVsInner()
}

/*
 * Delegation (by): помнить
 * Класс может делегировать реализацию интерфейса другому объекту.
 * Это паттерн "Декоратор" за 0 строк кода.
 * Мы можем переопределить только нужные методы, остальные пойдут в `real`.
 */
private fun demoInterfaceDelegationBy() {
    val realRepo = InMemoryRepo()
    val proxyRepo = LoggingRepo(realRepo) // Оборачиваем

    proxyRepo.save("User1") // Пройдет через лог
    println("All: ${proxyRepo.all()}") // Пройдет напрямую к real
}

private interface Repo2 {
    fun save(value: String)
    fun all(): List<String>
}

private class InMemoryRepo : Repo2 {
    private val data = ArrayList<String>()
    override fun save(value: String) {
        data.add(value)
    }

    override fun all(): List<String> = data.toList()
}

// Делегируем всё интерфейсу real, кроме тех методов, что override
private class LoggingRepo(private val real: Repo2) : Repo2 by real {
    override fun save(value: String) {
        println("LOG: Saving $value")
        real.save(value) // вызываем оригинал
    }
}

/*
 * lazy: помнить
 * - Только для val.
 * - Вычисляется один раз при первом обращении.
 * - По умолчанию synchronized (потокобезопасно).
 */
private fun demoPropertyDelegationLazy() {
    val holder = ConfigHolder()
    println("1. Accessing config...")
    // "Heavy computation..." напечатается только тут
    println("Value: ${holder.config}")

    println("2. Accessing again...")
    println("Value: ${holder.config}") // берет готовое
}

private class ConfigHolder {
    val config: String by lazy {
        println(">> Heavy computation of config...")
        "Config_v1"
    }
}

/*
 * Lateinit var: помнить
 * - Только для var.
 * - Нельзя для примитивов (Int, Double...), только ссылочные типы.
 * - Если обратиться до инициализации -> UninitializedPropertyAccessException.
 * - Можно проверить .isInitialized (но только внутри класса, где объявлено, либо через reference).
 */
private fun demoLateinit() {
    val p = Provider()
    // println(p.data) // Ошибка!

    p.init("Payload")
    println("Lateinit data: ${p.data}")
}

private class Provider {
    lateinit var data: String

    fun init(value: String) {
        data = value
    }

    fun check() {
        if (::data.isInitialized) { // проверка
            println(data)
        }
    }
}

/*
 * Observable & Vetoable: помнить
 * - observable: "слушатель" изменения (old -> new).
 * - vetoable: может "запретить" изменение, если лямбда вернет false.
 */
private fun demoObservableAndVetoable() {
    var counter: Int by Delegates.observable(0) { prop, old, new ->
        println("Counter changed: $old -> $new")
    }
    counter = 1
    counter = 2

    var age: Int by Delegates.vetoable(0) { prop, old, new ->
        new >= old // Разрешаем менять только в большую сторону (растем)
    }
    age = 10
    println("Age set to 10: $age")
    age = 5 // вето! 5 < 10
    println("Age set to 5 (ignored): $age") // останется 10
}

/*
 * Sealed Interface: помнить
 * - В отличие от Sealed Class, интерфейс не хранит состояние (fields).
 * - Класс может наследовать sealed interface и другой класс (множественное наследование типов).
 * - when должен быть исчерпывающим.
 */
private fun demoSealedInterface() {
    val event: UIEvent = UIEvent.Click(10)
    handleEvent(event)
}

private sealed interface UIEvent {
    data class Click(val id: Int) : UIEvent
    data object Back : UIEvent // object эффективнее для событий без данных
}

private fun handleEvent(e: UIEvent) {
    when (e) {
        is UIEvent.Click -> println("Clicked ${e.id}")
        UIEvent.Back -> println("Go back")
    }
}

/*
 * Value class (ex. Inline class): помнить
 * - Обертка над ОДНИМ значением.
 * - В Runtime (по возможности) заменяется на примитив, не создавая объект в куче.
 * - Типобезопасность без оверхеда: UserId(1) != OrderId(1).
 */
private fun demoValueClass() {
    val uid = UserId(100)
    val oid = OrderId(100)

    // uid == oid // Ошибка компиляции! Типы разные.
    println("Value class: ${uid.id}")
}

@JvmInline
private value class UserId(val id: Int)

@JvmInline
private value class OrderId(val id: Int)


/*
 * Object Expression (Анонимные объекты): помнить
 * - Аналог new Interface() { ... } в Java.
 * - Создается "на месте".
 * - Может наследовать класс и интерфейсы одновременно.
 */
private fun demoObjectExpression() {
    val clickListener = object : ClickListener {
        override fun onClick() {
            println("Anonymous object clicked!")
        }
    }
    clickListener.onClick()

    // Ad-hoc объект (просто набор полей/методов без типа)
    val point = object {
        val x = 10
        val y = 20
    }
    println("Ad-hoc point: ${point.x}, ${point.y}")
}

private interface ClickListener {
    fun onClick()
}

/*
 * Nested vs Inner: помнить
 * - Nested (по умолчанию): статический вложенный класс. НЕ ИМЕЕТ доступа к this внешнего класса.
 * - Inner: имеет ссылку на внешний класс (outer).
 * - В Android Inner классы опасны утечками памяти (если живут дольше, чем Activity/View).
 */
private fun demoNestedVsInner() {
    val outer = Outer("Main")

    val nested = Outer.Nested()
    println(nested.foo())

    val inner = outer.Inner() // Создается ОТ экземпляра outer
    println(inner.foo())
}

private class Outer(private val name: String) {
    class Nested {
        fun foo() = "Nested sees nothing"
        // fun test() = name // Ошибка!
    }

    inner class Inner {
        // inner видит приватные поля внешнего класса
        fun foo() = "Inner sees $name"
    }
}
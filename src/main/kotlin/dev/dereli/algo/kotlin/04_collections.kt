package dev.dereli.algo.kotlin

import java.util.*

/**
 * Файл: 04_collections.kt
 * Тема: Коллекции, Структуры данных, Сложность операций (Big O).
 *
 * =========================================================================================
 * ТЕОРЕТИЧЕСКИЙ БЛОК: СТРУКТУРЫ ДАННЫХ
 * =========================================================================================
 *
 * 1. List (Списки):
 *    - ArrayList: Динамический массив.
 *      - Доступ по индексу `get(i)`: O(1).
 *      - Вставка в конец `add(e)`: Амортизированное O(1).
 *      - Вставка в середину/начало `add(0, e)`: O(N) (сдвиг массива).
 *    - LinkedList: Двусвязный список.
 *      - Доступ `get(i)`: O(N) (перебор).
 *      - Вставка/удаление, если есть итератор: O(1).
 *      - В Котлин/Android почти всегда выигрывает ArrayList из-за Cache Locality процессора.
 *
 * 2. Set (Множества - уникальность):
 *    - HashSet: На хеш-таблице. Порядок хаотичный. Поиск/Вставка O(1).
 *    - TreeSet: На красно-черном дереве. Сортированный порядок. Поиск/Вставка O(log N).
 *    - LinkedHashSet: HashSet + порядок вставки.
 *
 * 3. Map (Ключ-Значение):
 *    - HashMap: O(1). Без порядка.
 *    - TreeMap: O(log N). Ключи отсортированы. Удобно для поиска диапазонов (floorKey).
 *
 * 4. Queue / Deque (Очереди):
 *    - ArrayDeque: Лучшая реализация очереди и стека в Java/Kotlin.
 *      - Быстрее Stack (он синхронизирован) и LinkedList (меньше мусора).
 *    - PriorityQueue: Куча (Heap). Min-элемент всегда сверху O(1). Вставка/Удаление O(log N).
 * =========================================================================================
 */

fun main() {
    println("--- Arrays vs Lists (Static vs Dynamic) ---")
    demoArraysVsLists()

    println("\n--- ArrayList vs LinkedList (Array vs Nodes) ---")
    demoArrayListVsLinkedList()

    println("\n--- HashSet vs TreeSet (O(1) vs O(log N)) ---")
    demoHashSetVsTreeSet()

    println("\n--- HashMap vs TreeMap (Unordered vs Sorted) ---")
    demoHashMapVsTreeMap()

    println("\n--- Queue / Stack / Deque (ArrayDeque) ---")
    demoQueueStackDeque()

    println("\n--- PriorityQueue (Min-Heap) ---")
    demoPriorityQueue()

    println("\n--- Basic Ops (Map, Filter, Fold) ---")
    demoCollectionOps()

    println("\n--- Advanced Ops (Flatten, Windowed, Zip) ---")
    demoAdvancedCollectionOps()
}

/*
 * Arrays vs Lists: помнить
 * - Array<T>: Фиксированный размер, Mutable. Эквивалент T[] в Java.
 * - List<T>: Интерфейс. В Kotlin по умолчанию Immutable (listOf).
 * - MutableList<T>: Интерфейс с методами add/remove. Обычно это ArrayList.
 */
private fun demoArraysVsLists() {
    val arr = intArrayOf(1, 2, 3) // int[] - примитивы (без боксинга, эффективно)
    val objArr = arrayOf("a", "b") // String[]

    val list = listOf(1, 2, 3) // Read-only List (Arrays.ArrayList или SingletonList)
    val mutable = mutableListOf(1, 2, 3) // java.util.ArrayList

    println("Array access: ${arr[1]}")
    println("List access:  ${list[1]}")

    // arr.add(4) // ОШИБКА: у массива фиксированный размер
    mutable.add(4) // OK: динамический список
    println("Mutable list grew: $mutable")
}

/*
 * ArrayList vs LinkedList: помнить
 * Главное правило собеседования:
 * Если нужен доступ по индексу -> ArrayList.
 * Если нужна очередь/стек -> ArrayDeque.
 * LinkedList почти никогда не нужен (из-за Cache Misses процессора).
 */
private fun demoArrayListVsLinkedList() {
    val arrayList = ArrayList<Int>()
    val linkedList = LinkedList<Int>()

    // Вставка в конец: O(1) для обоих
    arrayList.add(1)
    linkedList.add(1)

    // Доступ по индексу
    // arrayList[500] -> O(1) - моментально (смещение в памяти)
    // linkedList[500] -> O(N) - пойдет по 500 узлам
}

/*
 * HashSet vs TreeSet: помнить
 * HashSet:
 * - O(1) contains/add.
 * - Порядок элементов не гарантирован (зависит от hashCode).
 * TreeSet:
 * - O(log N) contains/add (медленнее).
 * - Элементы ВСЕГДА отсортированы.
 * - Используется, когда нужно `ceiling` (наименьший элемент >= заданному) или `subSet`.
 */
private fun demoHashSetVsTreeSet() {
    val hs = hashSetOf(5, 1, 3)
    println("HashSet: $hs (random order)")

    val ts = sortedSetOf(5, 1, 3) // TreeSet under the hood
    println("TreeSet: $ts (sorted: 1, 3, 5)")

    println("HashSet contains 3: ${hs.contains(3)}") // O(1)
}

/*
 * HashMap vs TreeMap: помнить
 * Аналогично множествам, но для пар Ключ-Значение.
 * TreeMap полезна для задач "Расписание", "Интервалы", "Ближайший ключ".
 */
private fun demoHashMapVsTreeMap() {
    val hm = HashMap<String, Int>()
    hm["Z"] = 1
    hm["A"] = 2
    println("HashMap keys: ${hm.keys}") // Порядок не гарантирован

    val tm = TreeMap<String, Int>()
    tm["Z"] = 1
    tm["A"] = 2
    println("TreeMap keys: ${tm.keys}") // [A, Z] (сортировка по ключу)

    // Киллер-фича TreeMap: поиск ближайших ключей
    val numberMap = TreeMap<Int, String>()
    numberMap[10] = "Ten"
    numberMap[20] = "Twenty"

    println("FloorKey(15): ${numberMap.floorKey(15)}") // 10 (ближайшее снизу)
    println("CeilingKey(15): ${numberMap.ceilingKey(15)}") // 20 (ближайшее сверху)
}

/*
 * Queue / Stack / Deque: помнить
 * - Stack (Java Class) - устарел, наследует Vector, синхронизирован (медленный).
 * - Используйте ArrayDeque. Это и Stack, и Queue.
 * - Методы:
 *   - Stack: push(), pop(), peek() (LIFO)
 *   - Queue: offer(), poll(), peek() (FIFO)
 *   - Deque: addFirst/Last, removeFirst/Last
 */
private fun demoQueueStackDeque() {
    val deque = ArrayDeque<Int>()

    // Queue (FIFO) - Очередь в магазине
    deque.addLast(1)
    deque.addLast(2)
    println("Queue poll: ${deque.removeFirst()}") // 1 (первый пришел, первый ушел)

    // Stack (LIFO) - Стопка тарелок
    deque.clear()
    deque.addLast(10)
    deque.addLast(20)
    println("Stack pop: ${deque.removeLast()}") // 20 (последний пришел, первый ушел)
}

/*
 * PriorityQueue (Heap / Куча): помнить
 * - Структура данных, где на вершине всегда МИНИМАЛЬНЫЙ (или макс) элемент.
 * - Используется в алгоритмах Дейкстры, Прима, Хаффмана.
 * - poll() забирает корень -> O(log N).
 */
private fun demoPriorityQueue() {
    val minHeap = PriorityQueue<Int>()
    minHeap.addAll(listOf(5, 1, 3, 2))

    // Вытаскиваем элементы (будут выходить в порядке возрастания)
    print("PriorityQueue poll: ")
    while (minHeap.isNotEmpty()) {
        print("${minHeap.poll()} ")
    }
    println()

    // Max Heap (через компаратор)
    val maxHeap = PriorityQueue<Int>(compareByDescending { it })
    maxHeap.addAll(listOf(1, 5, 3))
    println("MaxHeap peek: ${maxHeap.peek()}") // 5
}

/*
 * Basic Collection Ops: помнить
 * Функциональный стиль Kotlin заменяет циклы for.
 * - map: трансформация 1 к 1.
 * - filter: отбор по условию.
 * - fold: свертка списка в одно значение (аккумулятор).
 * - any/all/none: предикаты (возвращают Boolean).
 */
private fun demoCollectionOps() {
    val nums = (1..5).toList()

    val squares = nums.map { it * it }
    val evens = nums.filter { it % 2 == 0 }

    // Fold (аналог reduce, но безопаснее и мощнее)
    // acc - аккумулятор, i - текущий элемент
    val sum = nums.fold(0) { acc, i -> acc + i }

    println("Basic ops: evens=$evens, sum=$sum")

    // groupBy: создает Map<Key, List<Value>>
    // Очень часто нужно в задачах "сгруппировать анаграммы" или "посчитать частоты"
    val words = listOf("apple", "apricot", "banana", "blueberry")
    val grouped = words.groupBy { it.first() } // Group by first letter
    println("Grouped: $grouped")
}

/*
 * Advanced Ops: помнить (Must have для LeetCode)
 * - flatten / flatMap: убирают вложенность списков.
 * - zip: объединяет два списка в пары.
 * - chunked: делит список на части (пагинация).
 * - windowed: скользящее окно (sliding window algo).
 */
private fun demoAdvancedCollectionOps() {
    // 1. Flatten
    val nested = listOf(listOf(1, 2), listOf(3, 4))
    println("Flatten: ${nested.flatten()}") // [1, 2, 3, 4]

    // 2. Zip
    val names = listOf("Alice", "Bob")
    val ages = listOf(25, 30)
    val zipped = names.zip(ages) // List<Pair<String, Int>>
    println("Zipped: $zipped")

    // 3. Chunked vs Windowed
    val list = (1..6).toList()
    println("Chunked(2): ${list.chunked(2)}")       // [[1,2], [3,4], [5,6]] - не пересекаются
    println("Windowed(2): ${list.windowed(2, 1)}") // [[1,2], [2,3], [3,4]...] - пересекаются (скользящее окно)

    // 4. Partition (разделение на два списка)
    val (even, odd) = list.partition { it % 2 == 0 }
    println("Partition: even=$even, odd=$odd")
}
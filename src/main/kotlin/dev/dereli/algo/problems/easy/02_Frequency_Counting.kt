package dev.dereli.algo.problems.easy

/**
 * Valid Anagram
 *
 * Условие:
 * - Даны строки s и t.
 * - Нужно вернуть true, если t является анаграммой s:
 *   т.е. содержит те же символы в тех же количествах (порядок не важен).
 *
 * Ключевая идея (Frequency Count / баланс частот):
 * - Если длины разные -> сразу false.
 * - Используем IntArray(26) как счётчик для букв 'a'..'z'.
 * - За один проход по индексам:
 *   - увеличиваем частоту символа из s
 *   - уменьшаем частоту символа из t
 * - В конце все значения должны стать 0 -> строки содержат одинаковые буквы в одинаковых количествах.
 *
 * Почему это работает:
 * - Для каждого символа мы добавляем вклад из s и вычитаем вклад из t.
 * - Если строки анаграммы, суммарный баланс по каждому символу равен 0.
 *
 * Ограничение:
 * - Реализация рассчитана на символы 'a'..'z' (как в классическом варианте LeetCode).
 * - Для “любых символов” нужен HashMap<Char, Int>.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1) (26 — константа)
 */
private fun isValidAnagram(s: String, t: String): Boolean {
    if (s.length != t.length) return false
    val fq = IntArray(26)
    for (i in s.indices) {
        fq[s[i] - 'a']++
        fq[t[i] - 'a']--
    }
    return !fq.any({ it != 0 })
}

/**
 * First Unique Character in a String
 *
 * Условие:
 * - Дана строка s.
 * - Нужно вернуть индекс первого символа, который встречается ровно один раз.
 * - Если такого символа нет — вернуть -1.
 *
 * Ключевая идея (Frequency Count + второй проход):
 * - Сначала считаем частоты всех символов.
 * - Затем вторым проходом идём слева направо и ищем первый символ с частотой 1.
 *
 * Реализация:
 * - Так как считаем, что строка состоит из 'a'..'z', используем IntArray(26).
 *   Индекс символа: (ch - 'a').
 *
 * Почему это работает:
 * - Первый проход даёт точные частоты каждого символа.
 * - Второй проход гарантирует, что мы найдём именно первый (самый левый) уникальный символ.
 *
 * Сложность:
 * - Time: O(n) (2 прохода)
 * - Memory: O(1) (26 — константа)
 */

private fun firstUniqueCharInStr(s: String): Int {
    val fq = IntArray(26)
    for (ch in s) {
        fq[ch - 'a']++
    }
    for ((idx, ch) in s.withIndex()) {
        if (fq[ch - 'a'] == 1) return idx
    }
    return -1
}

/**
 * Ransom Note
 *
 * Условие:
 * - Даны строки ransomNote и magazine.
 * - Нужно вернуть true, если ransomNote можно составить из букв magazine.
 * - Каждую букву из magazine можно использовать не более одного раза.
 *
 * Ключевая идея (Frequency Count):
 * - Считаем частоты букв 'a'..'z' в ransomNote (сколько "нужно").
 * - Считаем частоты букв 'a'..'z' в magazine (сколько "есть").
 * - Для каждой буквы проверяем: need <= have.
 *
 * Почему это работает:
 * - Если для какой-то буквы её нужно больше, чем есть в magazine, собрать ransomNote невозможно.
 * - Если для всех букв need <= have, то все буквы можно "выдать" из magazine и собрать ransomNote.
 *
 * Ограничение:
 * - Реализация рассчитана на символы 'a'..'z'.
 *
 * Сложность:
 * - Time: O(n + m)
 * - Memory: O(1) (26 — константа)
 */
private fun ransomNote(ransomNote: String, magazine: String): Boolean {
    val fqRansomNote = IntArray(26)
    val fqMagazine = IntArray(26)
    for (ch in ransomNote) {
        fqRansomNote[ch - 'a']++
    }
    for (ch in magazine) {
        fqMagazine[ch - 'a']++
    }
    for ((idx, freq) in fqRansomNote.withIndex()) {
        if (freq > fqMagazine[idx]) return false
    }
    return true
}

/**
 * Majority Element
 *
 * Условие:
 * - Дан массив nums.
 * - Нужно вернуть majority element — элемент, который встречается строго больше n/2 раз.
 * - Гарантируется, что такой элемент существует.
 *
 * Идея (Boyer–Moore Voting):
 * - Держим "кандидата" candidate и счётчик count.
 * - Идём по массиву:
 *   - если count == 0 -> назначаем текущий num новым candidate
 *   - если num == candidate -> count++
 *   - иначе -> count--
 *
 * Почему это работает (интуиция):
 * - Мы "погашаем" пары разных элементов: один голос за кандидата и один против.
 * - Так как majority element встречается > n/2, его невозможно полностью погасить:
 *   после всех сокращений он остаётся кандидатом.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1)
 */
private fun majorityElement(nums: IntArray): Int {
    var candidate = Int.MIN_VALUE
    var count = 0
    for (num in nums) {
        if (count == 0) candidate = num
        count += if (num == candidate) 1 else -1
    }
    return candidate
}

/**
 * Find All Anagrams in a String
 *
 * Условие:
 * - Даны строки s и p.
 * - Нужно вернуть все стартовые индексы подстрок s, которые являются анаграммами p.
 * - Анаграмма: те же символы в тех же количествах (порядок не важен).
 *
 * Идея (Sliding Window + Frequency Count):
 * - Так как длина анаграммы фиксирована (m = p.length), используем окно длины m.
 * - Считаем частоты символов p в массиве pfreq[26].
 * - Считаем частоты символов первого окна s[0..m-1] в массиве sfreq[26].
 * - Далее двигаем окно вправо на 1:
 *   - символ, который выходит слева (s[left]) -> уменьшаем sfreq
 *   - символ, который входит справа (s[right]) -> увеличиваем sfreq
 * - Если sfreq и pfreq совпадают по всем 26 буквам -> текущее окно является анаграммой -> добавляем left.
 *
 * Почему это работает:
 * - Окно всегда содержит ровно m символов.
 * - sfreq хранит точные количества каждой буквы внутри текущего окна.
 * - Подстрока является анаграммой p тогда и только тогда, когда частоты всех букв совпадают:
 *   sfreq[c] == pfreq[c] для каждой буквы c.
 * - При сдвиге окна меняются ровно две частоты (вышедшего и вошедшего символа),
 *   поэтому обновление sfreq делается за O(1) на шаг.
 *
 * Сложность:
 * - Time: O(n * 26) ~ O(n), где n = s.length (сравнение частот — константное, 26 букв).
 * - Memory: O(1) (два массива на 26 элементов).
 */
private fun findAnagrams(s: String, p: String): List<Int> {
    if (p.length > s.length) return listOf()
    var left = 0
    var right = p.lastIndex
    val sfreq = IntArray(26)
    val pfreq = IntArray(26)
    for (char in p) {
        pfreq[char - 'a']++
    }

    for (i in left..right) {
        sfreq[s[i] - 'a']++
    }

    val result = ArrayList<Int>()
    while (right < s.length) {
        if (sfreq.contentEquals(pfreq)) result.add(left)
        sfreq[s[left] - 'a']--
        left++
        right++
        if (right < s.length) {
            sfreq[s[right] - 'a']++
        }
    }
    return result
}

/**
 * Top K Frequent Elements
 *
 * Условие:
 * - Дан массив nums и число k.
 * - Нужно вернуть k элементов, которые встречаются чаще всего.
 * - Порядок ответа не важен.
 *
 * Идея (Frequency Map + Bucket по частоте):
 * 1) Считаем частоты через HashMap<Int, Int>: value -> freq.
 * 2) Создаём buckets, где индекс = частота:
 *    buckets[f] хранит список всех чисел, которые встретились ровно f раз.
 *    Максимальная частота не больше nums.size, поэтому buckets делаем размера n+1.
 * 3) Идём по buckets с конца (от большой частоты к маленькой) и собираем элементы,
 *    пока не наберём k.
 *
 * Почему это работает:
 * - Bucket группирует элементы по их частоте.
 * - Проход с конца гарантирует, что мы берём сначала самые частые элементы.
 * - Сортировка не нужна: частота ограничена n, поэтому раскладываем по индексам.
 *
 * Сложность:
 * - Time: O(n) в среднем (подсчёт + раскладка + проход по buckets).
 * - Memory: O(n) (map + buckets).
 */
private fun topKFrequent(nums: IntArray, k: Int): IntArray {
    val hm = HashMap<Int, Int>()
    val bucket = Array(nums.size + 1) { ArrayList<Int>() }
    for (num in nums) {
        hm[num] = hm.getOrDefault(num, 0) + 1
    }

    for ((num, freq) in hm) {

        bucket[freq].add(num)
    }

    val answ = ArrayList<Int>()
    var answK = k
    for (i in bucket.size - 1 downTo 1) {
        if (answK < 1) break
        for (num in bucket[i]) {
            if (answK > 0) {
                answ.add(num)
                answK--
            } else break
        }
    }
    return answ.toIntArray()
}


fun main() {
    println(majorityElement(intArrayOf(2, 2, 1, 1, 1, 2, 2)))
}
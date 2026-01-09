package dev.dereli.algo.problems
/**
* Contains Duplicate
*
* Идея:
* - Идём по массиву и складываем элементы в HashSet.
* - HashSet хранит только уникальные значения.
* - Если элемент уже есть в set -> значит, нашли повтор -> сразу возвращаем true.
*
* Почему так хорошо:
* - Проверка наличия в HashSet и добавление работают в среднем за O(1).
* - Мы можем выйти раньше, не проходя до конца, если дубликат встретился рано.
*
* Сложность:
* - Time: O(n)
* - Memory: O(n)
*/
fun containsDuplicate(nums: IntArray): Boolean {
    val hs = HashSet<Int>(nums.size)
    for (num in nums) {
        if (hs.contains(num)) return true
        hs.add(num)
    }
    return false
}

/**
 * Valid Palindrome (упрощённая версия: только буквы a..z)
 *
 * Идея two pointers:
 * - Ставим указатели i слева и j справа.
 * - Двигаем i вправо, пока не найдём "валидный" символ.
 * - Двигаем j влево, пока не найдём "валидный" символ.
 * - Сравниваем символы; если разные -> не палиндром.
 * - Если одинаковые -> сдвигаем оба указателя и продолжаем.
 *
 * ВАЖНО:
 * - Сейчас валидными считаются только символы 'a'..'z'.
 * - В классической задаче нужно пропускать всё, кроме букв и цифр.
 *   Тогда вместо проверок 'a'..'z' лучше использовать:
 *   `s[i].isLetterOrDigit()` и сравнивать через `lowercaseChar()`.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(n) из-за lowercase() (создаёт новую строку)
 */
fun isPalindrome(s: String): Boolean {
    val lowerStr = s.lowercase()
    var i = 0
    var j = lowerStr.lastIndex
    while (i < j) {
        if (lowerStr[i] !in 'a'..'z') {
            i++
            continue
        }
        if (lowerStr[j] !in 'a'..'z') {
            j--
            continue
        }
        if (lowerStr[i] != lowerStr[j]) return false
        i++
        j--
    }
    return true
}

/**
 * Majority Element (Boyer–Moore Voting)
 *
 * Идея:
 * - Majority element встречается > n/2, значит его "голосов" больше, чем всех остальных вместе.
 * - Держим кандидата и счётчик:
 *   - если count == 0 -> назначаем текущий элемент кандидатом
 *   - если текущий элемент == кандидат -> count++
 *   - иначе -> count--
 *
 * Интуиция:
 * - Пары разных элементов "взаимно уничтожаются" (count--).
 * - У majority элементов останется "избыточное" количество, поэтому финальный кандидат будет majority.
 *
 * Сложность:
 * - Time: O(n) (один проход)
 * - Memory: O(1)
 */
fun majorityElement(nums: IntArray): Int {
    var candidate = 0
    var count = 0
    for (num in nums) {
        if (count == 0) candidate = num
        count += if (num == candidate) 1 else -1
    }
    return candidate
}

/**
 * First Unique Character in a String (частоты + второй проход)
 *
 * Идея:
 * - Сначала считаем частоту каждого символа (frequency count).
 * - Потом второй раз идём по строке и ищем первый символ, у которого freq == 1.
 *
 * Предположение:
 * - Строка состоит из 'a'..'z', поэтому удобно использовать массив IntArray(26).
 *
 * Сложность:
 * - Time: O(n) (два прохода)
 * - Memory: O(1) (26 — константа)
 */
fun firstUniqueChar(s: String): Int {
    val fq = IntArray(26)
    for (ch in s) {
        fq[ch - 'a'] += 1
    }
    for ((index, ch) in s.withIndex()) {
        if (fq[ch - 'a'] == 1) return index
    }
    return -1
}

fun main() {
    println(majorityElement(intArrayOf(2, 2, 1, 1, 1, 2, 2)))
}
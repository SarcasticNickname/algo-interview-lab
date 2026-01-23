package dev.dereli.algo.problems.easy

import kotlin.math.abs

/**
 * Valid Palindrome
 *
 * Условие:
 * - Дана строка s.
 * - Нужно вернуть true, если строка является палиндромом,
 *   учитывая только буквы/цифры и игнорируя регистр.
 *
 * Идея (Two Pointers):
 * - Ставим два указателя: left в начало, right в конец.
 * - Пока left < right:
 *   - Пропускаем все символы, которые не являются буквой или цифрой.
 *   - Сравниваем нормализованные символы (в нижнем регистре).
 *   - Если не равны -> сразу false.
 *   - Если равны -> сдвигаем оба указателя к центру.
 *
 * Почему это работает:
 * - Мы сравниваем симметричные “значимые” символы слева и справа.
 * - Всё лишнее (пробелы, знаки) игнорируем, регистр не влияет.
 *
 * Сложность:
 * - Time: O(n) (каждый указатель проходит строку максимум один раз).
 * - Memory: O(n), если заранее делаем lowercase-копию строки (можно O(1), если по месту).
 */
private fun isPalindrome(s: String): Boolean {
    var left = 0
    var right = s.lastIndex
    while (left < right) {
        if (!s[left].lowercaseChar().isLetterOrDigit()) {
            left++
            continue
        }
        if (!s[right].lowercaseChar().isLetterOrDigit()) {
            right--
            continue
        }
        if (s[left].lowercaseChar() != s[right].lowercaseChar()) return false
        left++
        right--
    }
    return true
}

/**
 * Reverse String
 *
 * Условие:
 * - Дан массив символов s (CharArray).
 * - Нужно развернуть массив "на месте" (in-place), не создавая новый.
 *
 * Идея (Two Pointers + swap):
 * - Ставим два указателя: left в начало, right в конец.
 * - Пока left < right:
 *   - меняем местами s[left] и s[right]
 *   - сдвигаем left++, right-- к центру
 *
 * Почему это работает:
 * - Каждый шаг ставит на своё место пару симметричных элементов.
 * - После n/2 обменов массив полностью развёрнут.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1)
 */
private fun reverseString(s: CharArray) {
    var left = 0
    var right = s.lastIndex
    var tmp: Char
    while (left < right) {
        tmp = s[left]
        s[left] = s[right]
        s[right] = tmp
        left++
        right--
    }
}

/**
 * Remove Duplicates from Sorted Array
 *
 * Условие:
 * - Дан отсортированный массив nums (non-decreasing).
 * - Нужно удалить дубликаты "на месте", сохранив порядок уникальных.
 * - Вернуть k — количество уникальных элементов.
 * - После первых k элементов содержимое массива не важно.
 *
 * Идея (Two Pointers: slow/fast):
 * - slow — индекс последнего уникального элемента в "сжатой" части массива.
 * - fast — индекс текущего элемента, который мы читаем.
 * - Так как массив отсортирован, дубликаты стоят рядом.
 *
 * Алгоритм:
 * - Инициализируем slow = 0 (первый элемент уже уникальный), fast = 1.
 * - Пока fast < n:
 *   - если nums[fast] == nums[slow] -> это дубликат, просто fast++
 *   - иначе нашли новый уникальный:
 *       - увеличиваем slow и записываем nums[fast] в nums[slow]
 *       - fast++
 * - В итоге первые (slow + 1) элементов — уникальные.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1)
 */
private fun removeDuplicates(nums: IntArray): Int {
    if (nums.size < 2) return nums.size
    var fast = 1
    var slow = 0
    while (fast < nums.size) {
        if (nums[fast] == nums[slow]) {
            fast++
            continue
        }
        nums[++slow] = nums[fast++]
    }
    return ++slow
}

/**
 * Move Zeroes
 *
 * Условие:
 * - Дан массив nums.
 * - Нужно переместить все нули в конец массива,
 *   сохранив относительный порядок ненулевых элементов.
 * - Сделать это in-place, без дополнительного массива.
 *
 * Идея (Two Pointers: slow/fast):
 * - fast проходит по массиву и читает элементы.
 * - slow указывает позицию, куда нужно записать следующий ненулевой элемент.
 *
 * Алгоритм:
 * 1) Проходим fast слева направо:
 *    - если nums[fast] != 0 -> записываем его в nums[slow] и увеличиваем slow
 *    - если nums[fast] == 0 -> пропускаем
 * 2) После прохода все ненулевые элементы "сжаты" в начало [0..slow-1] в исходном порядке.
 * 3) Оставшуюся часть массива slow..end заполняем нулями.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1)
 */
private fun moveZeroes(nums: IntArray) {
    if (nums.size < 2) return
    var slow = 0
    var fast = 0
    while (fast < nums.size) {
        if (nums[fast] == 0) {
            fast++
            continue
        }
        nums[slow++] = nums[fast++]
    }
    while (slow < nums.size) {
        nums[slow++] = 0
    }
}

/**
 * Squares of a Sorted Array
 *
 * Условие:
 * - Дан отсортированный массив nums (non-decreasing), могут быть отрицательные числа.
 * - Нужно вернуть новый массив квадратов, тоже отсортированный по возрастанию.
 *
 * Идея (Two Pointers с двух концов):
 * - Самые большие квадраты дают элементы с наибольшим модулем.
 * - В отсортированном массиве по модулю "кандидаты" на максимум находятся на краях:
 *   слева (самые отрицательные) и справа (самые положительные).
 * - Ставим left = 0, right = n-1 и заполняем result с конца:
 *   - сравниваем abs(nums[left]) и abs(nums[right])
 *   - больший по модулю даёт больший квадрат -> кладём его в result[i]
 *   - двигаем соответствующий указатель
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(n) (результирующий массив)
 */
private fun sortedSquares(nums: IntArray): IntArray {
    var left = 0
    var right = nums.lastIndex
    val result = IntArray(nums.size)
    for (i in result.lastIndex downTo 0) {
        if (abs(nums[left]) > abs(nums[right])) {
            result[i] = nums[left] * nums[left]
            left++
        } else {
            result[i] = nums[right] * nums[right]
            right--
        }
    }
    return result
}

/**
 * Is Subsequence
 *
 * Условие:
 * - Даны строки s и t.
 * - Нужно вернуть true, если s является подпоследовательностью t
 *   (можно удалить из t некоторые символы, не меняя порядок оставшихся).
 *
 * Идея (Two Pointers):
 * - Два индекса:
 *   - sInd — текущая позиция в s (что мы хотим "сопоставить")
 *   - tInd — текущая позиция в t (где ищем нужный символ)
 * - Идём по t слева направо:
 *   - если s[sInd] == t[tInd] -> символ найден, двигаем оба указателя
 *   - иначе -> двигаем только tInd, продолжаем искать
 * - Если в конце sInd дошёл до s.length, значит все символы s найдены в t в нужном порядке.
 *
 * Почему это работает:
 * - tInd идёт только вперёд, поэтому порядок символов сохраняется.
 * - Мы увеличиваем sInd только когда нашли совпадение — значит каждый символ s
 *   действительно встречается в t после предыдущего.
 *
 * Сложность:
 * - Time: O(|t|) (каждый символ t рассматривается максимум один раз)
 * - Memory: O(1)
 */
private fun isSubsequence(s: String, t: String): Boolean {
    var sInd = 0
    var tInd = 0
    while (sInd < s.length && tInd < t.length) {
        if (s[sInd] != t[tInd]) {
            tInd++
            continue
        } else {
            sInd++
            tInd++
        }
    }
    return sInd == s.length
}

/**
 * Two Sum II (Input Array Is Sorted)
 *
 * Условие:
 * - Дан отсортированный массив nums и число target.
 * - Нужно вернуть индексы двух разных элементов, сумма которых равна target.
 * - Индексы в ответе 1-based (то есть +1 к обычным индексам массива).
 * - Гарантируется, что решение ровно одно.
 *
 * Идея (Two Pointers):
 * - Ставим два указателя:
 *   left = 0 (начало массива), right = n - 1 (конец массива).
 * - Пока left < right:
 *   - sum = nums[left] + nums[right]
 *   - если sum > target -> сумма слишком большая, уменьшаем right
 *   - если sum < target -> сумма слишком маленькая, увеличиваем left
 *   - иначе нашли пару -> возвращаем (left+1, right+1)
 *
 * Почему это работает:
 * - Массив отсортирован:
 *   - сдвиг right влево уменьшает сумму
 *   - сдвиг left вправо увеличивает сумму
 * - Так мы монотонно приближаемся к target и находим единственное решение за один проход.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1)
 */
private fun twoSumSorted(nums: IntArray, target: Int): IntArray {
    var left = 0
    var right = nums.lastIndex
    val result = IntArray(2)
    var sum: Int
    while (left < right) {
        sum = nums[left] + nums[right]
        if (sum > target) {
            right--
            continue
        } else if (sum < target) {
            left++
            continue
        } else {
            result[0] = left + 1
            result[1] = right + 1
            break
        }
    }
    return result
}
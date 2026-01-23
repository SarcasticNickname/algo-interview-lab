package dev.dereli.algo.problems.easy

/**
 * Two Sum
 *
 * Условие:
 * - Дан массив nums и число target.
 * - Нужно вернуть индексы двух разных элементов, сумма которых == target.
 * - Гарантируется, что решение ровно одно.
 *
 * Идея (one-pass HashMap):
 * - Идём слева направо.
 * - Для текущего x = nums[i] считаем need = target - x (какое число нужно, чтобы добить до target).
 * - Если need уже встречалось раньше, то в map лежит его индекс -> нашли пару.
 * - Иначе запоминаем текущий элемент: map[x] = i.
 *
 * Почему так работает:
 * - map хранит "уже виденные числа -> их индекс".
 * - Проверка наличия за O(1) в среднем.
 * - Порядок важен: сначала проверяем, потом кладём (чтобы не взять один и тот же элемент дважды).
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(n)
 */
private fun twoSum(nums: IntArray, target: Int): IntArray {
    val hm = HashMap<Int, Int>()
    var need: Int
    for (i in nums.indices) {
        need = target - nums[i]
        if (hm.containsKey(need)) {
            return intArrayOf(hm[need]!!, i)
        }
        hm[nums[i]] = i
    }
    return intArrayOf()
}

/**
 * Intersection of Two Arrays
 *
 * Условие:
 * - Даны два массива nums1 и nums2.
 * - Нужно вернуть массив уникальных элементов, которые встречаются и в nums1, и в nums2.
 * - Порядок элементов в ответе не важен.
 *
 * Идея (Set + удаление для уникальности):
 * - Берём меньший массив и кладём все его элементы в HashSet (это "кандидаты на пересечение").
 * - Идём по большему массиву:
 *   - если элемент есть в set -> он присутствует в обоих массивах, добавляем в result
 *   - сразу удаляем этот элемент из set, чтобы не добавить его повторно (уникальность ответа)
 *
 * Почему так хорошо:
 * - HashSet даёт contains/remove в среднем за O(1).
 * - Память минимизируем: set строим по меньшему массиву.
 *
 * Сложность:
 * - Time: O(n + m)
 * - Memory: O(min(n, m))
 */
private fun arrayIntersect(nums1: IntArray, nums2: IntArray): IntArray {
    val hs = HashSet<Int>(minOf(nums1.size, nums2.size))
    val minArray = if (nums1.size < nums2.size) nums1 else nums2
    val maxArray = if (nums1.size < nums2.size) nums2 else nums1
    for (num in minArray) {
        hs.add(num)
    }
    val result = ArrayList<Int>(minArray.size)
    for (num in maxArray) {
        if (hs.contains(num)) {
            result.add(num)
            hs.remove(num)
        }
    }
    return result.toIntArray()
}

/**
 * Happy Number
 *
 * Условие:
 * - Число n называется "счастливым", если повторяя процесс
 *   "заменить число на сумму квадратов его цифр",
 *   мы в итоге придём к 1.
 * - Если последовательность зацикливается (никогда не приходит к 1) -> число не счастливое.
 *
 * Идея (HashSet для детекта цикла):
 * - Строим последовательность: n -> next(n) -> next(next(n)) -> ...
 * - На каждой итерации:
 *   - если x == 1 -> true
 *   - если x уже встречалось (есть в set) -> мы попали в цикл -> false
 *   - иначе добавляем x в set и переходим к следующему значению
 *
 * Почему повтор означает "false":
 * - Функция next(x) детерминирована: если мы снова увидели то же x, дальше всё будет повторяться по кругу.
 *
 * next(x):
 * - Считаем сумму квадратов цифр через цикл:
 *   digit = x % 10, x /= 10
 *
 * Сложность:
 * - Time: O(k * d), где k — число шагов до 1 или до цикла, d — кол-во цифр (обычно мало, почти константа)
 * - Memory: O(k) на HashSet для посещённых значений
 */
private fun happyNumber(n: Int): Boolean {
    fun nextNumber(num: Int): Int {
        var x = num
        var sum = 0
        var digit: Int
        while (x > 0) {
            digit = x % 10
            sum += digit * digit
            x /= 10
        }
        return sum
    }

    var x = n
    val hs = HashSet<Int>()
    var sum: Int = 0
    while (x != 1) {
        if (hs.contains(x)) return false
        hs.add(x)
        x = nextNumber(x)
    }
    return true
}

/**
 * Isomorphic Strings
 *
 * Условие:
 * - Даны две строки s1 и s2.
 * - Нужно проверить, можно ли заменить символы в s1 так, чтобы получить s2.
 *
 * Требования (биекция):
 * 1) Каждый символ из s1 должен всегда отображаться в один и тот же символ s2.
 * 2) Два разных символа из s1 не могут отображаться в один и тот же символ s2 ("без слипания").
 *
 * Идея (две HashMap для проверки в обе стороны):
 * - hm1: отображение s1Char -> s2Char
 * - hm2: отображение s2Char -> s1Char
 *
 * Алгоритм:
 * - Если s1 и s2 разной длины -> сразу false.
 * - Идём по индексам:
 *   - если символ s1[i] уже есть в hm1, то hm1[s1[i]] обязан быть равен s2[i]
 *   - если символ s2[i] уже есть в hm2, то hm2[s2[i]] обязан быть равен s1[i]
 *   - иначе записываем новое соответствие в обе карты
 *
 * Почему две карты:
 * - Одна карта гарантирует "один символ -> один символ".
 * - Вторая карта гарантирует, что разные символы не укажут на один и тот же символ (биекция).
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(k), где k — число уникальных символов (в худшем случае O(n))
 */
private fun isomorphicStrings(s1: String, s2: String): Boolean {
    if (s1.length != s2.length) return false
    val hm1 = HashMap<Char, Char>()
    val hm2 = HashMap<Char, Char>()
    for (i in s1.indices) {
        if (hm1.containsKey(s1[i])) {
            if (hm1[s1[i]] != s2[i]) return false
            if (hm2[s2[i]] != s1[i]) return false
            continue
        }
        if (hm2.containsKey(s2[i])) return false
        hm1[s1[i]] = s2[i]
        hm2[s2[i]] = s1[i]
    }
    return true
}

/**
 * Word Pattern
 *
 * Условие:
 * - Дана строка pattern (например "abba") и строка s из слов (например "dog cat cat dog").
 * - Нужно проверить, что слова следуют шаблону pattern.
 *
 * Требование (биекция):
 * - Одна и та же буква pattern всегда соответствует одному и тому же слову.
 * - Разные буквы не могут соответствовать одному и тому же слову.
 *
 * Идея (HashMap + HashSet):
 * - Разбиваем s на массив слов.
 * - Если кол-во слов != длине pattern -> false.
 * - Держим:
 *   - map: Char -> String (какому слову соответствует буква)
 *   - set usedWords: какие слова уже заняты (чтобы не было "слипания")
 *
 * Алгоритм:
 * - Идём по словам:
 *   - если буква уже есть в map -> слово должно совпасть
 *   - если буквы ещё нет:
 *       - если слово уже в usedWords -> другое правило уже заняло это слово -> false
 *       - иначе записываем соответствие и помечаем слово как занятое
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(n)
 */
private fun wordPattern(pattern: String, s: String): Boolean {
    val words = s.split(" ")
    if (pattern.length != words.size) return false

    val charToWord = HashMap<Char, String>()
    val assignedWords = HashSet<String>()
    for ((idx, word) in words.withIndex()) {
        if (charToWord.containsKey(pattern[idx])) {
            if (charToWord[pattern[idx]] != word) return false
            continue
        }
        if (assignedWords.contains(word)) return false
        assignedWords.add(word)
        charToWord[pattern[idx]] = word
    }
    return true
}

/**
 * Valid Sudoku
 *
 * Условие:
 * - Дана доска 9x9 с символами '1'..'9' и '.' (пусто).
 * - Нужно проверить, что текущее состояние НЕ нарушает правила судоку:
 *   1) В каждой строке нет повторов цифр 1..9
 *   2) В каждом столбце нет повторов цифр 1..9
 *   3) В каждом квадрате 3x3 нет повторов цифр 1..9
 * - Решать судоку не нужно, только проверка валидности.
 *
 * Идея (HashSet для каждого блока):
 * - Проверяем отдельно:
 *   - 9 строк
 *   - 9 столбцов
 *   - 9 квадратов 3x3
 * - Для каждой строки/столбца/квадрата используем HashSet:
 *   - если встречаем цифру и она уже в set -> дубликат -> false
 *   - '.' пропускаем
 *   - любой другой символ -> false (защита от некорректного ввода)
 *
 * Проход квадратов 3x3:
 * - Начальные координаты (rowStart, colStart) идут с шагом 3:
 *   rowStart = 0,3,6 и colStart = 0,3,6
 * - Внутри квадрата пробегаем i=rowStart..rowStart+2 и j=colStart..colStart+2
 *
 * Сложность:
 * - Time: O(81) ~ O(1) (фиксированный размер 9x9)
 * - Memory: O(1) (set максимум на 9 элементов)
 */
private fun isValidSudoku(board: Array<CharArray>): Boolean {
    if (board.size != 9) return false
    for (row in board) {
        if (row.size != 9) return false
    }

    val hs = HashSet<Char>()
    // rows
    for (i in 0..8) {
        for (j in 0..8) {
            when (board[i][j]) {
                in '1'..'9' -> {
                    if (hs.contains(board[i][j])) return false
                    hs.add(board[i][j])
                }

                '.' -> continue
                else -> return false
            }
        }
        hs.clear()
    }

    // columns
    for (j in 0..8) {
        for (i in 0..8) {
            when (board[i][j]) {
                in '1'..'9' -> {
                    if (hs.contains(board[i][j])) return false
                    hs.add(board[i][j])
                }

                '.' -> continue
                else -> return false
            }
        }
        hs.clear()
    }

    // quadrants
    for (rowStart in 0..8 step 3) {
        for (columnStart in 0..8 step 3) {
            for (i in rowStart..rowStart + 2) {
                for (j in columnStart..columnStart + 2) {
                    when (board[i][j]) {
                        in '1'..'9' -> {
                            if (hs.contains(board[i][j])) return false
                            hs.add(board[i][j])
                        }

                        '.' -> continue
                        else -> return false
                    }
                }
            }
            hs.clear()
        }
    }
    return true
}

/**
 * Longest Substring Without Repeating Characters
 *
 * Условие:
 * - Дана строка s.
 * - Нужно вернуть длину самой длинной подстроки (НЕПРЕРЫВНОЙ), в которой нет повторяющихся символов.
 *
 * Идея (Sliding Window + HashSet):
 * - Используем "скользящее окно" [l, r), где:
 *   - l — левая граница (inclusive)
 *   - r — правая граница (exclusive), то есть окно включает символы с индексами l..r-1
 * - HashSet хранит все символы, которые сейчас находятся внутри окна.
 *   В каждый момент set соответствует окну и содержит только уникальные символы.
 *
 * Как двигаемся:
 * 1) Пытаемся расширить окно вправо (r):
 *    - смотрим символ ch = s[r]
 * 2) Если ch уже есть в set:
 *    - значит в окне появился повтор, уникальность нарушена
 *    - сдвигаем l вправо и удаляем s[l] из set,
 *      пока ch не перестанет быть в set (то есть пока повтор не исчезнет)
 * 3) Когда ch снова "уникален" для окна:
 *    - добавляем ch в set
 *    - увеличиваем r (окно расширилось)
 *    - обновляем максимум: maxLen = max(maxLen, r - l)
 *
 * Почему это O(n):
 * - r проходит строку один раз слева направо.
 * - l тоже двигается только вперёд и каждый символ удаляется из set максимум один раз.
 * - Значит суммарно не больше 2*n операций добавления/удаления -> O(n).
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(k), где k — число уникальных символов в текущем окне (в худшем случае O(n))
 */
fun longestSubstringWithoutRepeatingChars(s: String): Int {
    val hs = HashSet<Char>()
    if (s.length < 2) return s.length
    var l = 0
    var r = 0
    var maxUniqueSubstrLength = 0
    while (r < s.length) {
        if (hs.contains(s[r])) {
            while (hs.contains(s[r])) {
                hs.remove(s[l])
                l++
            }
        }
        hs.add(s[r])
        r++
        maxUniqueSubstrLength = maxOf(maxUniqueSubstrLength, r - l)
    }
    return maxUniqueSubstrLength
}
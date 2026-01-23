package dev.dereli.algo.problems.easy

import kotlin.math.max

/**
 * Best Time to Buy and Sell Stock
 *
 * Условие:
 * - Дан массив prices, где prices[i] — цена акции в i-й день.
 * - Нужно выбрать один день купить и один день позже продать (одна сделка).
 * - Вернуть максимальную прибыль.
 * - Если получить прибыль невозможно — вернуть 0.
 *
 * Идея (One-pass / держим состояние):
 * - minPriceSoFar — минимальная цена, которую мы уже видели (кандидат на покупку).
 * - bestProfit — максимальная прибыль, найденная на текущий момент.
 * - Идём слева направо по дням:
 *   - если текущая цена меньше minPriceSoFar -> обновляем minPriceSoFar
 *   - иначе считаем прибыль, если продать сегодня: price - minPriceSoFar
 *     и обновляем bestProfit, если стало лучше.
 *
 * Почему это работает:
 * - В каждый день мы рассматриваем лучшую возможную покупку в прошлом (minPriceSoFar)
 *   и продажу сегодня, поэтому не пропускаем оптимальную пару buy < sell.
 * - Проход слева направо гарантирует, что покупка всегда раньше продажи.
 *
 * Сложность:
 * - Time: O(n) (один проход по массиву).
 * - Memory: O(1) (только пару переменных состояния).
 */
private fun maxProfit(prices: IntArray): Int {
    var minPriceSoFar = Int.MAX_VALUE
    var bestProfit = 0
    for (price in prices) {
        if (price < minPriceSoFar) {
            minPriceSoFar = price
        } else {
            val curProfit = price - minPriceSoFar
            if (curProfit > bestProfit) {
                bestProfit = curProfit
            }
        }
    }
    return bestProfit
}

/**
 * Maximum Subarray
 *
 * Условие:
 * - Дан массив целых чисел nums (могут быть отрицательные).
 * - Нужно найти непустой непрерывный подмассив (subarray) с максимальной суммой
 *   и вернуть эту сумму.
 *
 * Идея (Kadane / One-pass DP):
 * - Держим два значения:
 *   - curSum — максимальная сумма подмассива, который ОБЯЗАТЕЛЬНО заканчивается
 *     в текущем элементе.
 *   - bestSum — глобальный максимум среди всех curSum.
 *
 * Переход (на каждом элементе num):
 * - Есть два варианта для подмассива, заканчивающегося здесь:
 *   1) продолжить предыдущий: curSum + num
 *   2) начать новый с текущего: num
 * - Берём лучшее:
 *   curSum = max(num, curSum + num)
 * - Обновляем ответ:
 *   bestSum = max(bestSum, curSum)
 *
 * Почему это работает:
 * - Если предыдущая сумма curSum отрицательная, она только ухудшает результат,
 *   поэтому выгоднее начать подмассив заново с текущего элемента.
 * - Мы рассматриваем лучший подмассив, заканчивающийся в каждой позиции,
 *   и берём максимум среди них.
 *
 * Сложность:
 * - Time: O(n) (один проход по массиву).
 * - Memory: O(1) (только переменные состояния).
 */
private fun maxSubArray(nums: IntArray): Int {
    var bestSum = Int.MIN_VALUE
    var curSum = 0
    for (num in nums) {
        curSum = max(num, curSum + num)
        bestSum = max(bestSum, curSum)
    }
    return bestSum
}

/**
 * Climbing Stairs
 *
 * Условие:
 * - Есть лестница из n ступенек.
 * - За шаг можно подняться на 1 или на 2 ступеньки.
 * - Нужно вернуть количество разных способов добраться до ступеньки n.
 *
 * DP-идея:
 * - dp[i] = количество способов добраться до i-й ступеньки.
 * - На ступеньку i можно прийти только:
 *   - с i-1 (шаг 1)
 *   - с i-2 (шаг 2)
 * - Значит переход:
 *   dp[i] = dp[i - 1] + dp[i - 2]
 *
 * База:
 * - dp[0] = 1 (один способ “стоять на старте”, удобно для формулы)
 * - dp[1] = 1
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(n) для массива dp
 */
private fun climbStairsDp(n: Int): Int {
    if (n < 2) return 1
    val dp = IntArray(n + 1)
    dp[0] = 1
    dp[1] = 1
    for (i in 2..n) {
        dp[i] = dp[i - 1] + dp[i - 2]
    }
    return dp[n]
}

/**
 * Climbing Stairs (Rolling / O(1) memory)
 *
 * Идея:
 * - Нам не нужен весь dp-массив, потому что dp[i] зависит только от dp[i-1] и dp[i-2].
 * - Храним два последних значения:
 *   - prev2 = dp[i-2]
 *   - prev1 = dp[i-1]
 * - На каждом шаге считаем новое значение и “сдвигаем окно”.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1)
 */
private fun climbStairsRolling(n: Int): Int {
    if (n < 2) return 1
    var prev2 = 1
    var prev1 = 1
    for (i in 2..n) {
        val inter = prev2
        prev2 = prev1
        prev1 += inter
    }
    return prev1
}

/**
 * House Robber
 *
 * Условие:
 * - Дан массив nums, где nums[i] — деньги в i-м доме.
 * - Нельзя грабить два соседних дома.
 * - Нужно вернуть максимальную сумму, которую можно забрать.
 *
 * DP-идея:
 * - dp[i] = максимальная сумма, которую можно получить из домов 0..i.
 *
 * Переход:
 * - Для дома i есть два варианта:
 *   1) Не грабим i -> dp[i - 1]
 *   2) Грабим i -> dp[i - 2] + nums[i] (так как i-1 трогать нельзя)
 * - dp[i] = max(dp[i - 1], dp[i - 2] + nums[i])
 *
 * База:
 * - dp[0] = nums[0]
 * - dp[1] = max(nums[0], nums[1])
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(n) (массив dp)
 */
private fun robDp(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    if (nums.size < 2) return nums[0]
    if (nums.size == 2) return (max(nums[0], nums[1]))
    val dp = IntArray(nums.size)
    dp[0] = nums[0]
    dp[1] = max(nums[0], nums[1])
    for (i in 2..nums.lastIndex) {
        dp[i] = max(dp[i - 1], dp[i - 2] + nums[i])
    }
    return dp.last()
}

/**
 * House Robber (Rolling / O(1) memory)
 *
 * Идея:
 * - dp[i] зависит только от dp[i-1] и dp[i-2], поэтому массив не нужен.
 * - Храним два значения:
 *   - prev2 = dp[i-2]
 *   - prev1 = dp[i-1]
 * - На каждом i считаем текущее dp и “сдвигаем окно”.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(1)
 */
private fun robRolling(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    if (nums.size < 2) return nums[0]
    if (nums.size == 2) return (max(nums[0], nums[1]))
    var prev2 = nums[0]
    var prev1 = max(nums[0], nums[1])
    for (i in 2..nums.lastIndex) {
        val inter = prev2
        prev2 = prev1
        prev1 = max(prev1, inter + nums[i])
    }
    return prev1
}

/**
 * Jump Game
 *
 * Условие:
 * - Дан массив nums, где nums[i] — максимальная длина прыжка с позиции i.
 * - Стартуем с индекса 0.
 * - Нужно вернуть true, если можно добраться до последнего индекса, иначе false.
 *
 * Идея (Greedy / держим максимальную достижимость):
 * - dp[i] будем понимать как самый дальний индекс, до которого можно добраться,
 *   рассматривая позиции от 0 до i.
 * - На каждом шаге i:
 *   - если i > dp[i-1], значит позиция i недостижима -> сразу false
 *   - иначе обновляем дальность:
 *     dp[i] = max(dp[i - 1], i + nums[i])
 *
 * Почему это работает:
 * - dp[i-1] хранит текущую “границу достижимости”.
 * - Если текущий индекс внутри границы, то мы реально можем оказаться на i
 *   и попробовать расширить границу прыжком i + nums[i].
 * - Если индекс вышел за границу, дальше пройти уже нельзя.
 *
 * Сложность:
 * - Time: O(n)
 * - Memory: O(n) из-за массива dp (можно O(1), если хранить только farthest).
 */
private fun canJump(nums: IntArray): Boolean {
    val dp = IntArray(nums.size)
    dp[0] = nums[0]
    for (i in 1..nums.lastIndex) {
        if (i > dp[i - 1]) return false
        dp[i] = max((dp[i - 1]), (i + nums[i]))
    }
    return true
}

/**
 * Can Place Flowers
 *
 * Идея (жадный алгоритм, один проход):
 * - Идём слева направо по массиву.
 * - Сажаем цветок в позицию i только если:
 *   1) flowerbed[i] == 0
 *   2) слева пусто (i == 0 или flowerbed[i-1] == 0)
 *   3) справа пусто (i == lastIndex или flowerbed[i+1] == 0)
 * - Когда сажаем, ставим flowerbed[i] = 1, чтобы это учитывалось в следующих проверках.
 * - Как только посадили n цветов — сразу возвращаем true (ранний выход).
 *
 * Почему жадность корректна:
 * - Посадка в i влияет только локально: блокирует максимум i+1.
 *   Любое корректное решение всё равно не может посадить цветы одновременно в i и i+1,
 *   поэтому выбор “сажаем при первой возможности” не уменьшает число возможных посадок.
 *
 * Сложность:
 * - Время: O(m), где m = flowerbed.size
 * - Память: O(1)
 */
private fun canPlaceFlowers(flowerbed: IntArray, n: Int): Boolean {
    if (n == 0) return true
    var need = n
    for (i in flowerbed.indices) {
        if ((flowerbed[i] == 0) && (i == 0 || flowerbed[i - 1] == 0) && (i == flowerbed.lastIndex || flowerbed[i + 1] == 0)) {
            flowerbed[i] = 1
            need--
            if (need == 0) return true
        }
    }
    return false
}
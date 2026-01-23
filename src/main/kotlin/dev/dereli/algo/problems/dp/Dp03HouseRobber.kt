package dev.dereli.algo.problems.dp

import kotlin.math.max

fun rob(nums: IntArray): Long {
    if (nums.size == 0) return 0L
    if (nums.size == 1) return nums[0].toLong()
    val dp = IntArray(nums.size)
    dp[0] = nums[0]
    dp[1] = max(nums[0], nums[1])
    for (i in 2..nums.lastIndex) {
        dp[i] = max(dp[i - 1], dp[i - 2] + nums[i])
    }
    return dp.last().toLong()
}

fun main() {
    println(rob(intArrayOf(2, 1, 1, 2)))
}
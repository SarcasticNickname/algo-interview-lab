package dev.dereli.algo.problems.dp

fun canPartition(nums: IntArray): Boolean {
    val arraySum = nums.sum()
    if (arraySum % 2 != 0) return false
    val target = arraySum / 2
    val dp = BooleanArray(target + 1)
    dp[0] = true
    for (num in nums) {
        for (i in target downTo num) {
            dp[i] = dp[i] || dp[i - num]
        }
        if (dp[target]) return true
    }
    return dp[target]
}

fun main() {
    println(canPartition(intArrayOf(2, 3, 4, 5)))
}
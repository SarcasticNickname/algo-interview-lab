package dev.dereli.algo.problems.dp

import kotlin.math.min

fun minCostClimbingStairs(cost: IntArray): Long {
    if (cost.size == 0) return 0L
    if (cost.size == 1) return cost[0].toLong()
    val dp = LongArray(cost.size)
    dp[0] = cost[0].toLong()
    dp[1] = cost[1].toLong()
    for (i in 2..cost.lastIndex) {
        dp[i] = cost[i].toLong() + min(dp[i - 1], dp[i - 2])
    }
    return min(dp.last(), dp[dp.lastIndex - 1])
}

fun main() {
    println(minCostClimbingStairs(intArrayOf(10, 15, 20)))
}
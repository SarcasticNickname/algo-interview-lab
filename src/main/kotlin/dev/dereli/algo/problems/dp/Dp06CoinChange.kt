package dev.dereli.algo.problems.dp

import kotlin.math.min

fun coinChange(coins: IntArray, amount: Int): Int {
    val inf = amount + 1
    val dp = IntArray(amount + 1) { inf }
    dp[0] = 0
    for (i in 1..amount) {
        for (coin in coins) {
            if ((i - coin) > -1 && dp[i - coin] != inf) {
                dp[i] = min(dp[i], dp[i - coin] + 1)
            }
        }
    }
    return if (dp[amount] != inf) dp[amount] else -1
}

fun main() {
    println(coinChange(intArrayOf(2), 3))
}
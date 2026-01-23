package dev.dereli.algo.problems.dp

fun climbStairs(n: Int): Int {
    if (n == 0) return 0
    val dp = IntArray(n + 1)
    dp[0] = 1
    dp[1] = 1
    for (i in 2..n) {
        dp[i] = dp[i - 1] + dp[i - 2]
    }
    return dp[n]
}

fun main() {
    println(climbStairs(20))
}
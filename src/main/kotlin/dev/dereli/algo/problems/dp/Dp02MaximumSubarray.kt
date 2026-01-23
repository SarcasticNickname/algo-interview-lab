package dev.dereli.algo.problems.dp

import kotlin.math.max

fun maxSubArray(nums: IntArray): Long {
    var bestEnd: Long = nums[0].toLong()
    var bestOverall: Long = bestEnd
    for (i in 1 until nums.size) {
        val x = nums[i].toLong()
        bestEnd = max(x, bestEnd + x)
        bestOverall = max(bestEnd, bestOverall)
    }
    return bestOverall
}

fun main() {
    println(maxSubArray(intArrayOf(1, 2, -3, 4, -5, 6, 7, 8, 9)))
}
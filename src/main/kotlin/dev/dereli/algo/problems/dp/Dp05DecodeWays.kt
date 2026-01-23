package dev.dereli.algo.problems.dp

fun numDecodings(s: String): Long {
    if (s.isEmpty()) return 0L
    val dp = LongArray(s.length + 1)
    dp[0] = 1L
    dp[1] = if (s[0] != '0') 1L else 0L
    for (i in 2..s.length) {
        // Если не 0 - расшифровывается, а значит добавляем способы i - 1
        if (s[i - 1] != '0') dp[i] += dp[i - 1]
        // Конструируем число из 2 символов
        val two = (s[i - 2] - '0') * 10 + (s[i - 1] - '0')
        // Если валидное, то добавляем способы и i - 2
        if (two in 10..26) dp[i] += dp[i - 2]
    }
    // В конце возвращаем способы dp[n], получится вся подстрока, то есть строка
    return dp.last()
}

fun main() {
    println(numDecodings("226"))
}
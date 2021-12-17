import java.io.File

val keyTrackerSignalStrengthRange = 0..50

fun main() {
    val input = File("inputs/Day1.txt").readLines().map { it.toInt() }

    // Challenge 1
    val depthIncreaseCount = (1 until input.size).fold(0) { acc, index ->
        val lastDepth = input[index - 1]
        val currentDepth = input[index]
        if (currentDepth > lastDepth) acc + 1 else acc
    }

    println(depthIncreaseCount)

    // Challenge 2
    val depthIncreaseCount3Measurement = (3 until input.size).fold(0) { acc, index ->
        val lastPast3DepthsSum = listOf(
            input[index - 3],
            input[index - 2],
            input[index - 1]
        ).sum()
        val thisPast3DepthsSum = listOf(
            input[index - 2],
            input[index - 1],
            input[index]
        ).sum()
        if (thisPast3DepthsSum > lastPast3DepthsSum) acc + 1 else acc
    }

    println(depthIncreaseCount3Measurement)
}

import java.io.File
import kotlin.math.ceil

typealias BitStringSelector = (List<String>) -> String

class DiagnosticReport(private val rawData: List<String>) {

    enum class BitStringType {
        MostCommon, LeastCommon
    }

    enum class TieBreakStrategy {
        Skew0, Skew1
    }

    private fun Int.incrementIfOne(bit: Char): Int = if (bit == '1') this + 1 else this

    private fun getColOnesCounts(data: List<String>): List<Int> = data
        .map { it.toList() }
        .fold(List(data[0].length) { 0 }) { acc, bitRow ->
            acc.mapIndexed { colIndex, colOnesCount ->
                colOnesCount.incrementIfOne(bitRow[colIndex])
            }
        }

    private fun getBitStringSelector(
        bitStringType: BitStringType,
        tieBreakStrategy: TieBreakStrategy
    ): BitStringSelector = { data: List<String> ->
        getColOnesCounts(data).joinToString("") { colOnesCount ->
            val collectionSizeMidpoint = data.size / 2.0
            val majorityThreshold = ceil(collectionSizeMidpoint)
            val isTie = collectionSizeMidpoint == majorityThreshold && colOnesCount.toDouble() == majorityThreshold

            if (isTie) {
                when (tieBreakStrategy) {
                    TieBreakStrategy.Skew0 -> "0"
                    TieBreakStrategy.Skew1 -> "1"
                }
            } else {
                when (bitStringType) {
                    BitStringType.MostCommon -> if (colOnesCount >= majorityThreshold) "1" else "0"
                    BitStringType.LeastCommon -> if (colOnesCount >= majorityThreshold) "0" else "1"
                }
            }
        }
    }

    fun computeGammaRate(): String = getBitStringSelector(BitStringType.MostCommon, TieBreakStrategy.Skew1)(rawData)
    fun computeEpisilonRate(): String = getBitStringSelector(BitStringType.LeastCommon, TieBreakStrategy.Skew1)(rawData)

    private fun computeLifeSupportUnit(bitStringSelector: BitStringSelector): String {
        var currentBitString: String
        var ratingDataSet = rawData.toList()
        var rating = ""

        for (colIndex in 0 until rawData[0].length) {
            currentBitString = bitStringSelector(ratingDataSet)
            val newRatingDataSet = ratingDataSet.filter { it[colIndex] == currentBitString[colIndex] }

            when (newRatingDataSet.size) {
                1 -> { // If new data set size is 1, that's our rating
                    rating = newRatingDataSet.first()
                    break
                }
                0 -> { // If all were filtered in new set, that means last item in old set is our rating
                    rating = ratingDataSet.last()
                    break
                }
                else -> { // Otherwise, filter on a smaller list in the next iteration of the loop
                    ratingDataSet = newRatingDataSet
                    continue
                }
            }
        }

        return rating
    }

    fun computeOxygenGeneratorRating(): String = computeLifeSupportUnit(
        getBitStringSelector(BitStringType.MostCommon, TieBreakStrategy.Skew1)
    )

    fun computeC02ScrubberRating(): String = computeLifeSupportUnit(
        getBitStringSelector(BitStringType.LeastCommon, TieBreakStrategy.Skew0)
    )
}

fun String.toDecimal() = this.toInt(2)

fun main() {
    // Raw data from example (Rates should be gamma = 22, episilon = 9, multiplied = 198:
//    val rawData = listOf(
//        "00100",
//        "11110",
//        "10110",
//        "10111",
//        "10101",
//        "01111",
//        "00111",
//        "11100",
//        "10000",
//        "11001",
//        "00010",
//        "01010",
//    )

    val rawData: List<String> = File("inputs/Day3.txt").readLines()
    val report = DiagnosticReport(rawData)

    println("=== CHALLENGE 1 ===")
    val gammaRate = report.computeGammaRate()
    println("Gamma: $gammaRate")
    val gammaDecimal = gammaRate.toDecimal()
    println("Gamma in Decimal: $gammaDecimal")
    val episilonRate = report.computeEpisilonRate()
    println("Episilon: $episilonRate")
    val episilonDecimal = episilonRate.toDecimal()
    println("Episilon in Decimal: $episilonDecimal")
    println("Multiplied decimal rates (Answer to Challenge 1): ${gammaDecimal * episilonDecimal}")

    println("\n=== CHALLENGE 2 ===")
    val oxygenGenRate = report.computeOxygenGeneratorRating()
    println("Oxygen Generation Rate: $oxygenGenRate")
    val oxygenGenRateDecimal = oxygenGenRate.toDecimal()
    println("Oxygen Generation Rate in Decimal: $oxygenGenRateDecimal")
    val c02ScrubRate = report.computeC02ScrubberRating()
    println("C02 Scrubber Rate: $episilonRate")
    val c02ScrubDecimal = c02ScrubRate.toDecimal()
    println("C02 Scrubber Rate in Decimal: $c02ScrubDecimal")
    println("Multiplied decimal rates (Answer to Challenge 2): ${oxygenGenRateDecimal * c02ScrubDecimal}")
}
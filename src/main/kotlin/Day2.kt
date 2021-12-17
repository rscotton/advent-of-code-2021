import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.io.File

enum class Direction {
    Forward, Up, Down
}

fun deserializeDirection(directionStr: String): Direction = when (directionStr) {
    "forward" -> Direction.Forward
    "down" -> Direction.Down
    "up" -> Direction.Up
    else -> throw NotImplementedException()
}

class Submarine(val mode: MoveMode) {
    enum class MoveMode {
        Simple, WithAim
    }

    // State properties
    private var _currentCoordinates = Pair(0, 0)
    val currentCoordinates: Pair<Int, Int>
        get() = _currentCoordinates

    private var _aim = 0
    val aim: Int
        get() = _aim

    fun move(direction: Direction, distance: Int): Submarine {
        when (mode) {
            MoveMode.Simple -> moveSimple(direction, distance)
            MoveMode.WithAim -> moveWithAim(direction, distance)
        }

        return this
    }

    private fun moveSimple(direction: Direction, distance: Int) {
        val (curX, curY) = _currentCoordinates

        // Update current coordinates state property
        _currentCoordinates = when (direction) {
            Direction.Forward -> Pair(curX + distance, curY)
            Direction.Up -> Pair(curX, curY - distance)
            Direction.Down -> Pair(curX, curY + distance)
        }
    }

    private fun moveWithAim(direction: Direction, distance: Int) {
        val (curX, curY) = _currentCoordinates

        // Update state properties
        _currentCoordinates = when (direction) {
            Direction.Forward -> Pair(curX + distance, curY + (aim * distance))
            Direction.Up -> currentCoordinates
            Direction.Down -> currentCoordinates
        }

        _aim = when (direction) {
            Direction.Up -> aim - distance
            Direction.Down -> aim + distance
            Direction.Forward -> aim
        }
    }
}

fun main() {
    val allMoves: List<Pair<Direction, Int>> = File("inputs/Day2.txt").readLines()
        .map { it.trim().split(" ") } // List of List<String, String>("forward", "5")
        .map { moveDetails ->
            Pair(deserializeDirection(moveDetails[0]), moveDetails[1].toInt())
        }

    // CHALLENGE 1
    val mySimpleSub = Submarine(Submarine.MoveMode.Simple)

    // Sanity check: should equal (15, 10)
//    mySimpleSub
//        .move(Direction.Forward, 5)
//        .move(Direction.Down, 5)
//        .move(Direction.Forward, 8)
//        .move(Direction.Up, 3)
//        .move(Direction.Down, 8)
//        .move(Direction.Forward, 2)
//    println(mySimpleSub.currentCoordinates)

    allMoves.forEach { (direction, amount) -> mySimpleSub.move(direction, amount) }

    println("=== SIMPLE SUB ===")
    println("Coordinates: ${mySimpleSub.currentCoordinates}")
    val (curX, curY) = mySimpleSub.currentCoordinates
    println("Multiplied (Answer to Challenge 1): ${curX * curY}")

    // CHALLENGE 2
    val mySubWithAim = Submarine(Submarine.MoveMode.WithAim)

    // Sanity check: should equal (15, 60)
//    mySubWithAim
//        .move(Direction.Forward, 5)
//        .move(Direction.Down, 5)
//        .move(Direction.Forward, 8)
//        .move(Direction.Up, 3)
//        .move(Direction.Down, 8)
//        .move(Direction.Forward, 2)
//    println(mySubWithAim.currentCoordinates)

    allMoves.forEach { (direction, amount) -> mySubWithAim.move(direction, amount) }

    println("=== SUB WITH AIM ===")
    println("Coordinates: ${mySubWithAim.currentCoordinates}")
    val (curXwithAim, curYwithAim) = mySubWithAim.currentCoordinates
    println("Multiplied (Answer to Challenge 2): ${curXwithAim * curYwithAim}")
}
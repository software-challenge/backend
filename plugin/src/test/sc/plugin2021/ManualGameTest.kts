import org.junit.jupiter.api.assertDoesNotThrow
import sc.plugin2021.Color
import sc.plugin2021.Game
import sc.plugin2021.Move
import sc.plugin2021.SetMove
import sc.plugin2021.helper.MoveParser
import sc.plugin2021.util.printShapes
import kotlin.system.exitProcess

assertDoesNotThrow { MoveParser.selfCheck() }

loop()

fun loop() {
    Color.GREEN.team
    while (true) {
        val game = Game()
        var current = game.onPlayerJoined()
        game.onPlayerJoined()
        game.start()
        println("First piece is: ${game.gameState.startPiece}")
        while (true) {
            println(game.gameState)
            println("Enter a move (see helper.MoveParser) or command (`:reset` or `:stop`)")
            print("> ")
            
            val input: String = readLine() ?: continue
            if (input == ":reset") break
            if (input == ":stop") exitProcess(0)
            if (input.first() == ':') {
                println("Unknown command. Expect a move, `:reset` or `:stop`")
                continue
            }
            
            var move: Move
            try {
                move = MoveParser.parse(input)
            } catch (e: Exception) {
                println(e)
                continue
            }
            println("$input -> $move")
            
            try {
                game.onAction(game.gameState.getPlayer(move.color.team)!!, move)
                current = game.gameState.getOpponent(current)!!
            } catch (e: Exception) {
                println(e)
                if (move is SetMove) {
                    println("Piece was:")
                    printShapes(move.piece.shape)
                }
            }
        }
    }
}

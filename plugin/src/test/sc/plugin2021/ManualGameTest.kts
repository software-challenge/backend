import io.kotest.assertions.throwables.shouldNotThrowAny
import sc.plugin2021.Game
import sc.plugin2021.Move
import sc.plugin2021.SetMove
import sc.plugin2021.helper.MoveParser
import sc.plugin2021.util.printShapes
import kotlin.system.exitProcess

shouldNotThrowAny { MoveParser }

loop()

fun loop() {
    //Color.GREEN.team
    while (true) {
        val game = Game()
        var current = game.onPlayerJoined()
        game.onPlayerJoined()
        game.start()
        println("First piece is: ${game.currentState.startPiece}")
        while (true) {
            println(game.currentState)
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
                game.onAction(game.currentState.getPlayer(move.color.team), move)
                current = game.currentState.getOpponent(current)
            } catch (e: Exception) {
                if (move is SetMove) {
                    println("Piece was:")
                    printShapes(move.piece.shape)
                }
            }
        }
    }
}

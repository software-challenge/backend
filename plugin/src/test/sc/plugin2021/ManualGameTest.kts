import sc.plugin2021.Color
import sc.plugin2021.Game
import sc.plugin2021.helper.MoveParser
import kotlin.system.exitProcess

loop()

fun loop() {
    Color.GREEN.team
    while (true) {
        val game = Game()
        var current = game.onPlayerJoined()
        game.onPlayerJoined()
        game.start()
        while (true) {
            println(game.gameState)
            print(game.gameState.board)
            println("Enter a move (see helper.MoveParser) or command (`:reset` or `:stop`)")
            print("> ")
    
            val input: String = readLine()!!
            if (input == ":reset") break
            if (input == ":stop")  exitProcess(0)
            
            val move = MoveParser.parse(input)
            println("$input -> $move")
            
            try {
                game.onAction(game.gameState.getPlayer(move.color.team), move)
                current = game.gameState.getOpponent(current)!!
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}

import sc.plugin2021.Game
import sc.plugin2021.helper.MoveParser
import kotlin.system.exitProcess


while (true) {
    val game = Game()
    var current = game.onPlayerJoined()
    game.onPlayerJoined()
    do {
        println(game.gameState)
        println(game.gameState.board)
        print("> ")
        
        val input: String = readLine().let{"????"}
        if (input == "stop") exitProcess(0)
        if (input == "????") continue
        
        game.onAction(current, MoveParser.parse(input))
        
        current = game.gameState.getOpponent(current)!!
    } while (input != "reset")
}
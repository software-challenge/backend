package sc.player2021

import org.slf4j.LoggerFactory
import sc.player2021.logic.Logic
import sc.plugin2021.AbstractClient

class SimpleClient(host: String, port: Int, reservation: String): AbstractClient(host, port) {
    companion object {
        val logger = LoggerFactory.getLogger(SimpleClient::class.java)
    }
    
    val logic = Logic(this)
    
    init {
        handler = logic
        
        if (reservation.isEmpty())
            joinAnyGame()
        else
            joinPreparedGame(reservation)
    }
    
}
package sc.plugin2025

import kotlin.math.sqrt

object GameRuleLogic {
    /**
     * Berechnet wie viele Karotten für einen Zug der Länge
     * `moveCount` benötigt werden.
     *
     * @param moveCount Anzahl der Felder, um die bewegt wird
     * @return Anzahl der benötigten Karotten
     */
    fun calculateCarrots(moveCount: Int): Int =
        (moveCount * (moveCount + 1)) / 2
    
    /**
     * Berechnet, wie viele Züge mit `carrots` Karotten möglich sind.
     *
     * @param carrots maximal ausgegebene Karotten
     * @return Felder um die maximal bewegt werden kann
     */
    fun calculateMoveableFields(carrots: Int): Int {
        return when {
            carrots >= 990 -> 44
            carrots < 1 -> 0
            //-0.48 anstelle von -0.5 um Rundungsfehler zu vermeiden
            else -> (sqrt((2.0 * carrots) + 0.25) - 0.48).toInt()
        }
    }
    
    /*
    /**
     * Überprüft `Advance` Aktionen auf ihre Korrektheit.
     * Folgende Spielregeln werden beachtet:
     *
     * - Der Spieler muss genügend Karotten für den Zug besitzen
     * - Wenn das Ziel erreicht wird, darf der Spieler nach dem Zug maximal 10 Karotten übrig haben
     * - Man darf nicht auf Igelfelder ziehen
     * - Salatfelder dürfen nur betreten werden, wenn man noch Salate essen muss
     * - Hasenfelder dürfen nur betreten werden, wenn man noch Karte ausspielen kann
     *
     * @param state GameState
     * @param distance relativer Abstand zur aktuellen Position des Spielers
     * @return true, falls ein Vorwärtszug möglich ist
     */
    fun isValidToAdvance(state: GameState, distance: Int): Boolean {
        if(distance <= 0) {
            return false
        }
        val player: Hare = state.currentPlayer
        if(mustEatSalad(state)) {
            return false
        }
        var valid = true
        val requiredCarrots = calculateCarrots(distance)
        valid = valid && (requiredCarrots <= player.getCarrots())
        
        val newPosition: Int = player.getFieldIndex() + distance
        valid = valid && !state.isOccupied(newPosition)
        val type: Field = state.board.getTypeAt(newPosition)
        when(type) {
            INVALID -> valid = false
            Field.SALAD -> valid = valid && player.getSalads() > 0
            Field.HARE -> {
                var state2: GameState? = null
                try {
                    state2 = state.clone()
                } catch(e: CloneNotSupportedException) {
                    e.printStackTrace()
                }
                state2.setLastAction(Advance(distance))
                state2!!.currentPlayer.setFieldIndex(newPosition)
                state2.currentPlayer.changeCarrotsBy(-requiredCarrots)
                valid = valid && canPlayAnyCard(state2)
            }
            
            Field.GOAL -> {
                val carrotsLeft: Int = player.getCarrots() - requiredCarrots
                valid = valid && carrotsLeft <= 10
                valid = valid && player.getSalads() === 0
            }
            
            Field.HEDGEHOG -> valid = false
            Field.CARROT, Field.POSITION_1, Field.START, Field.POSITION_2 -> {}
            else -> throw IllegalStateException("Unknown Type $type")
            
        }
        return valid
    }
    
    /**
     * Überprüft, ob ein Spieler aussetzen darf. Er darf dies, wenn kein anderer Zug möglich ist.
     * @param state GameState
     * @return true, falls der derzeitige Spieler keine andere Aktion machen kann.
     */
    fun isValidToSkip(state: GameState): Boolean {
        return !canDoAnything(state)
    }
    
    /**
     * Überprüft, ob ein Spieler einen Zug (keinen Aussetzug)
     * @param state GameState
     * @return true, falls ein Zug möglich ist.
     */
    private fun canDoAnything(state: GameState): Boolean {
        return (canPlayAnyCard(state) || isValidToFallBack(state)
                || isValidToExchangeCarrots(state, 10)
                || isValidToExchangeCarrots(state, -10)
                || isValidToEat(state) || canAdvanceToAnyField(state))
    }
    
    /**
     * Überprüft ob der derzeitige Spieler zu irgendeinem Feld einen Vorwärtszug machen kann.
     * @param state GameState
     * @return true, falls der Spieler irgendeinen Vorwärtszug machen kann
     */
    private fun canAdvanceToAnyField(state: GameState): Boolean {
        val fields = calculateMoveableFields(state.currentPlayer.carrots)
        for(i in 0..fields) {
            if(isValidToAdvance(state, i)) {
                return true
            }
        }
        
        return false
    }
    
    /**
     * Überprüft `EatSalad` Züge auf Korrektheit. Um einen Salat
     * zu verzehren muss der Spieler sich:
     *
     * - auf einem Salatfeld befinden
     * - noch mindestens einen Salat besitzen
     * - vorher kein Salat auf diesem Feld verzehrt wurde
     *
     * @param state GameState
     * @return true, falls ein Salat gegessen werden darf
     */
    fun isValidToEat(state: GameState): Boolean {
        val player: Hare = state.currentPlayer
        var valid = true
        val currentField: Field = state.getTypeAt(player.getFieldIndex())
        
        valid = valid && (currentField == Field.SALAD)
        valid = valid && (player.getSalads() > 0)
        valid = valid && !playerMustAdvance(state)
        
        return valid
    }
    
    /**
     * Überprüft ab der derzeitige Spieler im nächsten Zug einen Vorwärtszug machen muss.
     * @param state GameState
     * @return true, falls der derzeitige Spieler einen Vorwärtszug gemacht werden muss
     */
    fun playerMustAdvance(state: GameState?): Boolean {
        val player: Hare = state!!.currentPlayer
        val type: Field = state.getTypeAt(player.getFieldIndex())
        
        if(type == Field.HEDGEHOG || type == Field.START) {
            return true
        }
        
        val lastAction: Action = state.getLastNonSkipAction(player)
        
        if(lastAction != null) {
            if(lastAction is EatSalad) {
                return true
            } else if(lastAction is Card) {
                // the player has to leave a hare field in next turn
                if((lastAction as Card).getType() === Card.EAT_SALAD) {
                    return true
                } else if((lastAction as Card).getType() === Card.TAKE_OR_DROP_CARROTS) // the player has to leave the hare field
                {
                    return true
                }
            }
        }
        
        return false
    }
    
    /**
     * Überprüft ob der derzeitige Spieler 10 Karotten nehmen oder abgeben kann.
     * @param state GameState
     * @param n 10 oder -10 je nach Fragestellung
     * @return true, falls die durch n spezifizierte Aktion möglich ist.
     */
    fun isValidToExchangeCarrots(state: GameState, n: Int): Boolean {
        val player: Hare = state.currentPlayer
        val valid: Boolean = state.getTypeAt(player.getFieldIndex()).equals(Field.CARROT)
        if(n == 10) {
            return valid
        }
        if(n == -10) {
            return if(player.getCarrots() >= 10) {
                valid
            } else {
                false
            }
        }
        return false
    }
    
    /**
     * Überprüft `FallBack` Züge auf Korrektheit
     *
     * @param state GameState
     * @return true, falls der currentPlayer einen Rückzug machen darf
     */
    fun isValidToFallBack(state: GameState): Boolean {
        if(mustEatSalad(state)) {
            return false
        }
        var valid = true
        val newPosition: Int = state.getPreviousFieldByType(
            Field.HEDGEHOG, state.currentPlayer
                .getFieldIndex()
        )
        valid = valid && (newPosition != -1)
        valid = valid && !state.isOccupied(newPosition)
        return valid
    }
    
    /**
     * Überprüft ob der derzeitige Spieler die `FALL_BACK` Karte spielen darf.
     * @param state GameState
     * @return true, falls die `FALL_BACK` Karte gespielt werden darf
     */
    fun isValidToPlayFallBack(state: GameState?): Boolean {
        val player: Hare = state!!.currentPlayer
        var valid = (!playerMustAdvance(state) && state.isOnHareField()
                     && state.isFirst(player))
        
        valid = valid && player.ownsCardOfType(Card.FALL_BACK)
        
        val o: Hare = state.getOpponent(player)
        val nextPos: Int = o.getFieldIndex() - 1
        if(nextPos == 0) {
            return false
        }
        
        val type: Field = state.getTypeAt(nextPos)
        when(type) {
            INVALID, Field.HEDGEHOG -> valid = false
            Field.START -> {}
            Field.SALAD -> valid = valid && player.getSalads() > 0
            Field.HARE -> {
                var state2: GameState? = null
                try {
                    state2 = state.clone()
                } catch(e: CloneNotSupportedException) {
                    e.printStackTrace()
                }
                state2.setLastAction(Card(Card.FALL_BACK))
                state2!!.currentPlayer.setFieldIndex(nextPos)
                state2.currentPlayer.setCards(player.getCardsWithout(Card.FALL_BACK))
                valid = valid && canPlayAnyCard(state2)
            }
            
            Field.CARROT, Field.POSITION_1, Field.POSITION_2 -> {}
            else -> throw IllegalStateException("Unknown Type $type")
        }
        return valid
    }
    
    /**
     * Überprüft ob der derzeitige Spieler die `HURRY_AHEAD` Karte spielen darf.
     * @param state GameState
     * @return true, falls die `HURRY_AHEAD` Karte gespielt werden darf
     */
    fun isValidToPlayHurryAhead(state: GameState?): Boolean {
        val player: Hare = state!!.currentPlayer
        var valid = (!playerMustAdvance(state) && state.isOnHareField()
                     && !state.isFirst(player))
        valid = valid && player.ownsCardOfType(Card.HURRY_AHEAD)
        
        val o: Hare = state.getOpponent(player)
        val nextPos: Int = o.getFieldIndex() + 1
        
        val type: Field = state.getTypeAt(nextPos)
        when(type) {
            INVALID, Field.HEDGEHOG -> valid = false
            Field.SALAD -> valid = valid && player.getSalads() > 0
            Field.HARE -> {
                var state2: GameState? = null
                try {
                    state2 = state.clone()
                } catch(e: CloneNotSupportedException) {
                    e.printStackTrace()
                }
                state2.setLastAction(Card(Card.HURRY_AHEAD))
                state2!!.currentPlayer.setFieldIndex(nextPos)
                state2.currentPlayer.setCards(player.getCardsWithout(Card.HURRY_AHEAD))
                valid = valid && canPlayAnyCard(state2)
            }
            
            Field.GOAL -> valid = valid && canEnterGoal(state)
            Field.CARROT, Field.POSITION_1, Field.POSITION_2, Field.START -> {}
            else -> throw IllegalStateException("Unknown Type $type")
        }
        return valid
    }
    
    /**
     * Überprüft ob der derzeitige Spieler die `TAKE_OR_DROP_CARROTS` Karte spielen darf.
     * @param state GameState
     * @param n 20 für nehmen, -20 für abgeben, 0 für nichts tun
     * @return true, falls die `TAKE_OR_DROP_CARROTS` Karte gespielt werden darf
     */
    fun isValidToPlayTakeOrDropCarrots(state: GameState?, n: Int): Boolean {
        val player: Hare = state!!.currentPlayer
        var valid = (!playerMustAdvance(state) && state.isOnHareField()
                     && player.ownsCardOfType(Card.TAKE_OR_DROP_CARROTS))
        
        valid = valid && (n == 20 || n == -20 || n == 0)
        if(n < 0) {
            valid = valid && ((player.getCarrots() + n) >= 0)
        }
        return valid
    }
    
    /**
     * Überprüft ob der derzeitige Spieler die `EAT_SALAD` Karte spielen darf.
     * @param state GameState
     * @return true, falls die `EAT_SALAD` Karte gespielt werden darf
     */
    fun isValidToPlayEatSalad(state: GameState?): Boolean {
        val player: Hare = state!!.currentPlayer
        return (!playerMustAdvance(state) && state.isOnHareField()
                && player.ownsCardOfType(Card.EAT_SALAD)) && player.getSalads() > 0
    }
    
    /**
     * Überprüft ob der derzeitige Spieler irgendeine Karte spielen kann.
     * TAKE_OR_DROP_CARROTS wird nur mit 20 überprüft
     * @param state GameState
     * @return true, falls das Spielen einer Karte möglich ist
     */
    private fun canPlayAnyCard(state: GameState?): Boolean {
        for(card in state!!.currentPlayer.getCards()) {
            if(canPlayCard(state, card)) return true
        }
        
        return false
    }
    
    private fun canPlayCard(state: GameState?, card: Card): Boolean {
        return when(card) {
            Card.EAT_SALAD -> isValidToPlayEatSalad(state)
            Card.FALL_BACK -> isValidToPlayFallBack(state)
            Card.HURRY_AHEAD -> isValidToPlayHurryAhead(state)
            Card.TAKE_OR_DROP_CARROTS -> isValidToPlayTakeOrDropCarrots(state, 20)
            else -> throw IllegalArgumentException("Unknown CardType $card")
        }
    }
    
    /**
     * Überprüft ob der derzeitige Spieler die Karte spielen kann.
     * @param state derzeitiger GameState
     * @param c Karte die gespielt werden soll
     * @param n Wert fuer TAKE_OR_DROP_CARROTS
     * @return true, falls das Spielen der entsprechenden Karte möglich ist
     */
    fun isValidToPlayCard(state: GameState?, c: Card, n: Int): Boolean {
        return if(c == Card.TAKE_OR_DROP_CARROTS) isValidToPlayTakeOrDropCarrots(
            state,
            n
        )
        else canPlayCard(state, c)
    }
    
    fun mustEatSalad(state: GameState): Boolean {
        val player: Hare = state.currentPlayer
        // check whether player just moved to salad field and must eat salad
        val field: Field = state.board.getTypeAt(player.getFieldIndex())
        if(field == Field.SALAD) {
            if(player.getLastNonSkipAction() is Advance) {
                return true
            } else if(player.getLastNonSkipAction() is Card) {
                if((player.getLastNonSkipAction() as Card).getType() === Card.FALL_BACK ||
                   (player.getLastNonSkipAction() as Card).getType() === Card.HURRY_AHEAD
                ) {
                    return true
                }
            }
        }
        return false
    }
    
    /**
     * Gibt zurück, ob der derzeitige Spieler eine Karte spielen kann.
     * @param state derzeitiger GameState
     * @return true, falls eine Karte gespielt werden kann
     */
    fun canPlayCard(state: GameState): Boolean {
        return state.fieldOfCurrentPlayer() === Field.HARE && canPlayAnyCard(state)
    }
    */
}

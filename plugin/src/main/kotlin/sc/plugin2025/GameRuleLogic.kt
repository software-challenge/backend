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
        if (distance <= 0) return false

        val player: Hare = state.currentPlayer
        if (mustEatSalad(state)) return false

        val requiredCarrots = calculateCarrots(distance)
        val hasEnoughCarrots = requiredCarrots <= player.carrots

        val newPosition: Int = player.position + distance
        val isNotOnOtherPlayer = state.otherPlayer.position != newPosition

        return when (state.board.getField(newPosition)) {
            Field.SALAD -> player.salads > 0
            Field.HARE -> {
                val advanceMove = Advance(distance)
                val nextState: GameState = state.performMove(advanceMove) as GameState
                canPlayAnyCard(nextState)
            }

            Field.GOAL -> player.carrots - requiredCarrots <= 10 && player.salads == 0
            Field.HEDGEHOG -> false
            else -> true
        } && hasEnoughCarrots && isNotOnOtherPlayer
    }

    /**
     * Überprüft, ob ein Spieler aussetzen darf. Er darf dies, wenn kein anderer Zug möglich ist.
     * @param state GameState
     * @return true, falls der derzeitige Spieler keine andere Aktion machen kann.
     */
    fun isValidToSkip(state: GameState): Boolean =
        !canDoAnything(state)

    /**
     * Überprüft, ob ein Spieler einen Zug (keine Aussetzung)
     * @param state GameState
     * @return true, falls ein Zug möglich ist.
     */
    private fun canDoAnything(state: GameState): Boolean =
        (canPlayAnyCard(state) || state.mayFallBack() || isValidToExchangeCarrots(state, 10)
         || isValidToExchangeCarrots(state, -10) || state.mayEatSalad() || canAdvanceToAnyField(state))

    /**
     * Überprüft, ob der derzeitige Spieler zu irgendeinem Feld einen Vorwärtszug machen kann.
     * @param state GameState
     * @return true, falls der Spieler irgendeinen Vorwärtszug machen kann
     */
    private fun canAdvanceToAnyField(state: GameState): Boolean =
        (0..calculateMoveableFields(state.currentPlayer.carrots))
            .any { isValidToAdvance(state, it) }

    /**
     * Überprüft ab der derzeitige Spieler im nächsten Zug einen Vorwärtszug machen muss.
     * @param state GameState
     * @return true, falls der derzeitige Spieler einen Vorwärtszug gemacht werden muss
     */
    fun playerMustAdvance(state: GameState?): Boolean {
        val player: Hare = state!!.currentPlayer
        val type: Field = state.board.getField(player.position)

        if (type == Field.HEDGEHOG || type == Field.START) return true

        val lastAction: HuIMove? = player.lastAction

        if (lastAction is EatSalad) return true
        else if (lastAction is CardAction) {
            // the player has to leave a hare field in next turn
            if (lastAction.card === Card.EAT_SALAD) return true
            // the player has to leave the hare field
            else if (lastAction.card === Card.TAKE_OR_DROP_CARROTS) return true
        }
        return false
    }


    /**
     * Überprüft, ob der derzeitige Spieler 10 Karotten nehmen oder abgeben kann.
     * @param state GameState
     * @param n 10 oder -10 je nach Fragestellung
     * @return true, falls die durch n spezifizierte Aktion möglich ist.
     */
    fun isValidToExchangeCarrots(state: GameState, n: Int) = with(state) {
        val player = currentPlayer
        val valid = board.getField(player.position) == Field.CARROTS
        n == 10 && valid || (n == -10 && player.carrots >= 10 && valid)
    }


    /**
     * Überprüft, ob der derzeitige Spieler die `FALL_BACK` Karte spielen darf.
     * @param state GameState
     * @return true, falls die `FALL_BACK` Karte gespielt werden darf
     */
    fun isValidToPlayFallBack(state: GameState): Boolean {
        val player: Hare = state.currentPlayer

        val mustNotAdvance = !playerMustAdvance(state)
        val isOnHare = state.board.getField(player.position) == Field.HARE
        // TODO()  val isFirst = state.players.firstOrNull() == player // das ist denke falsch, aber ich war mir nicht sicher was `isFirst` war
        val hasFallback = player.getCards().any { it == Card.FALL_BACK }

        val nextPos: Int = state.otherPlayer.position - 1
        if (nextPos == 0) return false

        return when (state.board.getField(nextPos)) {
            Field.SALAD -> player.salads > 0
            Field.HARE -> {
                val fallBack = CardAction.PlayCard(Card.FALL_BACK)
                val nextState: GameState = state.performMove(fallBack) as GameState
                canPlayAnyCard(nextState)
            }

            Field.HEDGEHOG -> false
            else -> true
        } && mustNotAdvance && isOnHare && hasFallback
    }

    /**
     * Überprüft, ob der derzeitige Spieler die `HURRY_AHEAD` Karte spielen darf.
     * @param state GameState
     * @return true, falls die `HURRY_AHEAD` Karte gespielt werden darf
     */
    fun isValidToPlayHurryAhead(state: GameState): Boolean {
        val player: Hare = state.currentPlayer

        val mustNotAdvance = !playerMustAdvance(state)
        val isOnHare = state.board.getField(player.position) == Field.HARE
        // TODO() val isFirst = state.players.firstOrNull() == player // das ist denke falsch, aber ich war mir nicht sicher was `isFirst` war
        val hasHurry = player.getCards().any { it == Card.HURRY_AHEAD }

        val nextPos: Int = state.otherPlayer.position + 1
        if (nextPos == 0) return false

        return when (state.board.getField(nextPos)) {
            Field.SALAD -> player.salads > 0
            Field.HARE -> {
                val fallBack = CardAction.PlayCard(Card.HURRY_AHEAD)
                val nextState: GameState = state.performMove(fallBack) as GameState
                canPlayAnyCard(nextState)
            }

            Field.GOAL -> player.carrots - calculateCarrots(nextPos - player.position) <= 10 && player.salads == 0
            Field.HEDGEHOG -> false
            else -> true
        } && mustNotAdvance && isOnHare && hasHurry
    }

    /**
     * Überprüft, ob der derzeitige Spieler die `TAKE_OR_DROP_CARROTS` Karte spielen darf.
     * @param state GameState
     * @param n 20 für Nehmen, -20 für Abgeben, 0 für nichts tun
     * @return true, falls die `TAKE_OR_DROP_CARROTS` Karte gespielt werden darf
     */
    fun isValidToPlayTakeOrDropCarrots(state: GameState, n: Int): Boolean {
        val player: Hare = state.currentPlayer

        val mustNotAdvance = !playerMustAdvance(state)
        val isOnHare = state.board.getField(player.position) == Field.HARE
        val hasCarrots = player.getCards().any { it == Card.TAKE_OR_DROP_CARROTS }

        return mustNotAdvance && isOnHare && hasCarrots &&
               (n == 20 || n == -20 || n == 0) &&
                if (n < 0) (player.carrots + n) >= 0 else true
    }

    /**
     * Überprüft, ob der derzeitige Spieler die `EAT_SALAD` Karte spielen darf.
     * @param state GameState
     * @return true, falls die `EAT_SALAD` Karte gespielt werden darf
     */
    fun isValidToPlayEatSalad(state: GameState, player: Hare = state.currentPlayer): Boolean =
        state.board.getField(player.position) == Field.HARE &&
        player.getCards().any { it == Card.EAT_SALAD } &&
        player.salads > 0
    
}

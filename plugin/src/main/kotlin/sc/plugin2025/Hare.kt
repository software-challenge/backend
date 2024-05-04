package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.Team
import sc.framework.PublicCloneable
import sc.plugin2025.util.HuIConstants

data class Hare(
    @XStreamAsAttribute val team: Team,
    @XStreamAsAttribute var position: Int = 0,
    @XStreamAsAttribute var salads: Int = HuIConstants.INITIAL_SALADS,
    @XStreamAsAttribute var carrots: Int = HuIConstants.INITIAL_CARROTS,
    @XStreamAsAttribute var lastAction: Action? = null,
    private val cards: ArrayList<Card> = arrayListOf(*Card.values()),
): PublicCloneable<Hare> {
    fun getCards(): List<Card> = cards
    fun hasCard(card: Card) = cards.contains(card)
    fun removeCard(card: Card) = cards.remove(card)
    
    val inGoal: Boolean
        get() = position == HuIConstants.NUM_FIELDS - 1
    
    val canEnterGoal: Boolean
        get() = carrots <= 10 && salads == 0
    
    fun eatSalad() = salads--
    
    override fun clone(): Hare = copy()
}

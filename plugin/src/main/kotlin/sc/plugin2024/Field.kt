package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeDirection
import sc.api.plugins.IField

@XStreamAlias("field")
sealed class Field: IField<Field> {
    override val isEmpty
        get() = true
    override fun clone() = this
    
    override fun toString() = javaClass.simpleName
    
    val letter: Char
        get() = javaClass.simpleName.first()
    
    /** Wasserfeld, auf ihm kann sich normal bewegt werden */
    @XStreamAlias("water")
    object WATER : Field() {
        private fun readResolve(): Any = WATER
    }
    
    /** Inselfeld, es kann nicht überwunden werden und kein Spieler kann darauf stehen */
    @XStreamAlias("island")
    object BLOCKED: Field() {
        private fun readResolve(): Any = BLOCKED
        override val isEmpty
            get() = false
    }

    /** Passagierfeld mit Anleger */
    @XStreamAlias("passenger")
    data class PASSENGER(@XStreamAsAttribute val direction: CubeDirection = CubeDirection.values().random(), @XStreamAsAttribute var passenger: Int = 1): Field() {
        override val isEmpty
            get() = false
        override fun clone() = PASSENGER(direction, passenger)
        override fun toString() = "PASSENGER${direction.ordinal}${passenger}"
    }

    /** Ein Zielfeld */
    @XStreamAlias("goal")
    object GOAL: Field() {
        private fun readResolve(): Any = GOAL
    }
    
    /** Ein Sandbankfeld */
    @XStreamAlias("sandbank")
    object SANDBANK: Field() {
        private fun readResolve(): Any = SANDBANK
    }
}
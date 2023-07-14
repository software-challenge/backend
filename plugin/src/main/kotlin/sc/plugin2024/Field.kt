package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeDirection
import sc.api.plugins.IField

@XStreamAlias("field")
sealed class Field: IField<Field> {
    override val isEmpty = true
    override fun clone() = this
    
    /** Wasserfeld, auf ihm kann sich normal bewegt werden */
    object WATER : Field()
    
    /** Inselfeld, es kann nicht überwunden werden und kein Spieler kann darauf stehen */
    object BLOCKED: Field() {
        override val isEmpty = false
    }

    /** Passagierfeld mit Anleger */
    class PASSENGER(@XStreamAsAttribute val direction: CubeDirection, @XStreamAsAttribute var passenger: Int = 1): Field() {
        override val isEmpty = false
        override fun clone() = PASSENGER(direction, passenger)
    }

    /** Ein Zielfeld */
    object GOAL: Field()

    /** Ein Sandbankfeld */
    object SANDBANK: Field()
}
package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.HexDirection
import sc.api.plugins.IField

@XStreamAlias("field")
sealed class FieldType: IField<FieldType> {
    override val isEmpty = true
    override fun clone() = this
    
    /** Wasserfeld, auf ihm kann sich normal bewegt werden */
    object WATER : FieldType()
    
    /** Inselfeld, es kann nicht überwunden werden und kein Spieler kann darauf stehen */
    object BLOCKED: FieldType() {
        override val isEmpty = false
    }

    /** Passagierfeld mit Anleger */
    class PASSENGER(val direction: HexDirection, var passenger: Int = 1): FieldType() {
        override val isEmpty = false
        override fun clone() = PASSENGER(direction, passenger)
    }

    /** Ein Zielfeld */
    object GOAL: FieldType()

    /** Ein Sandbankfeld */
    object SANDBANK: FieldType()
}
package sc.plugin2027

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField

/**
 * Die Farbe eines [Field]s.
 * Kann entweder eine der vier [Color]s haben oder leer sein.
 */
@XStreamAlias(value = "content")
enum class FieldContent(val letter: Char): IField {
    BLUE('B'),
    YELLOW('Y'),
    RED('R'),
    GREEN('G'),
    EMPTY('-');
    
    /**
     * Konvertiert einen Feldinhalt in die entsprechende Spielerfarbe, oder null, wenn das Feld leer ist.
     *
     * @return [Color], der diesem Feldinhalt entspricht, oder null, wenn das Feld leer ist.
     */
    fun toTeamColor(): Color? = when(this) {
        BLUE   -> Color.BLUE
        YELLOW -> Color.YELLOW
        RED    -> Color.RED
        GREEN  -> Color.GREEN
        EMPTY  -> null
    }
    /**
     * Gibt zurück, ob das Feld leer ist, d.h. keinen Inhalt hat.
     *
     * @return true, wenn das Feld leer ist, sonst false
     */
    override val isEmpty: Boolean
        get() = this == EMPTY
}
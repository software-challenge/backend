package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * Die Farbe eines [Field]s.
 * Kann entweder eine der vier [Color]s haben oder leer sein.
 */
@XStreamAlias(value = "content")
enum class FieldContent(val letter: Char) {
    BLUE('B'),
    YELLOW('Y'),
    RED('R'),
    GREEN('G'),
    EMPTY('-');

    /** Prüfe, ob der Inhalt des Felds leer ist. */
    fun empty(): Boolean = this == EMPTY

    /** Konvertiert zur entsprechenden Farbe oder null wenn leer. */
    operator fun unaryPlus(): Color? = when(this) {
        BLUE   -> Color.BLUE
        YELLOW -> Color.YELLOW
        RED    -> Color.RED
        GREEN  -> Color.GREEN
        EMPTY  -> null
    }
}

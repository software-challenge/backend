package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias

@XStreamAlias(value = "content")
enum class FieldContent(val letter: Char) {
    BLUE('B'),
    YELLOW('Y'),
    RED('R'),
    GREEN('G'),
    EMPTY('-');
    
    fun empty(): Boolean = this == EMPTY
    
    override fun toString(): String = letter.toString()
    
    operator fun unaryPlus(): Color? = when(this) {
        BLUE   -> Color.BLUE
        YELLOW -> Color.YELLOW
        RED    -> Color.RED
        GREEN  -> Color.GREEN
        EMPTY  -> null
    }
}

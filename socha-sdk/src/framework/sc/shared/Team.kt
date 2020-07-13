package sc.shared

enum class Team(val index: Int, val displayName: String) {
    
    ONE(0, "One"),
    TWO(1, "Two");
    
    val letter = name.first()
    
    fun opponent(): Team =
            when (this) {
                ONE -> TWO
                TWO -> ONE
            }
    
}

package sc.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import sc.api.plugins.IField
import sc.api.plugins.RectangularBoard

enum class Field: IField<Field> {
    EMPTY,
    ONE,
    TWO;
    
    override val isEmpty: Boolean
        get() = this == EMPTY
    
    override fun copy(): Field = this
}

class RectangularBoardTest: FunSpec({
    test("deep copy") {
        val board = RectangularBoard(arrayOf(arrayOf(Field.EMPTY, Field.ONE, Field.TWO)))
        board.fieldsEmpty() shouldBe false
        //val clone = board.clone()
        //clone.shouldBe(board)
        //clone.shouldNotBeSameInstanceAs(board)
        board[1, 0] = Field.EMPTY
        //clone.shouldNotBe(board)
        board[2, 0] = Field.EMPTY
        board.fieldsEmpty() shouldBe true
        //clone.fieldsEmpty() shouldBe false
    }
})
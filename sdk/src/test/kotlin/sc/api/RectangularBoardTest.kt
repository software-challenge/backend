package sc.api

import sc.api.plugins.IField

enum class Field: IField {
    EMPTY,
    ONE,
    TWO;
    
    override val isEmpty: Boolean
        get() = this == EMPTY
}

//class RectangularBoardTest: FunSpec({
//    test("deep copy") {
//        val board = RectangularBoard(arrayOf(arrayOf(Field.EMPTY, Field.ONE, Field.TWO)))
//        board.fieldsEmpty() shouldBe false
//        //val clone = board.clone()
//        //clone.shouldBe(board)
//        //clone.shouldNotBeSameInstanceAs(board)
//        board[1, 0] = Field.EMPTY
//        //clone.shouldNotBe(board)
//        board[2, 0] = Field.EMPTY
//        board.fieldsEmpty() shouldBe true
//        //clone.fieldsEmpty() shouldBe false
//    }
//})
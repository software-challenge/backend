package sc.plugin2020

import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import sc.plugin2020.util.CubeCoordinates
import sc.plugin2020.util.GameRuleLogic

class GameRuleTest: StringSpec({
    "board creation" {
        val board = Board()
        board.getField(0, 0, 0) shouldNotBe null
        board shouldBe board.clone()
        board.fields.filter { it.isObstructed } shouldHaveSize 3
    }
    "neighbours & distances" {
        val field1 = CubeCoordinates(0, 0, 0)
        val field2 = CubeCoordinates(1, -1, 0)
        val field3 = CubeCoordinates(2, -1, -1)
        field1.distanceTo(field3) shouldBe 2
        field1.compareTo(field3) shouldBe -2
        field2.compareTo(field1) shouldBe 1
        GameRuleLogic.isNeighbour(field1, field2) shouldBe true
        GameRuleLogic.isNeighbour(field1, field3) shouldBe false
        GameRuleLogic.isNeighbour(field2, field3) shouldBe true
    }
    "getNeighbours" {
        val result = GameRuleLogic.getNeighbours(Board(), CubeCoordinates(-2, 1)).toTypedArray()
        val expected = arrayOf(CubeCoordinates(-2, 2), CubeCoordinates(-1, 1), CubeCoordinates(-1, 0), CubeCoordinates(-2, 0), CubeCoordinates(-3, 1), CubeCoordinates(-3, 2))
        result contentEquals expected
    }
})
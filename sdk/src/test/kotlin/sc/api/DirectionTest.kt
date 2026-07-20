package sc.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import sc.api.plugins.CubeDirection
import sc.api.plugins.Direction
import sc.api.plugins.HexDirection
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream

class DirectionTest: FunSpec({
    context("Direction") {
        test("XML") {
            Direction.LEFT shouldSerializeTo "<direction>LEFT</direction>"
            testXStream.fromXML("<direction>UP_RIGHT</direction>").javaClass shouldBe Direction::class.java
        }
    }
    context("HexDirections") {
        test("produce correct opposites") {
            listOf(
                HexDirection.LEFT to HexDirection.RIGHT,
                HexDirection.UP_LEFT to HexDirection.DOWN_RIGHT,
                HexDirection.DOWN_LEFT to HexDirection.UP_RIGHT,
            ).forAll {
                it.first shouldNotBe it.second
                it.first.opposite() shouldBe it.second
                it.second.opposite() shouldBe it.first
                it.first.rotatedBy(3) shouldBe it.second
                it.first.rotatedBy(-3) shouldBe it.second
                it.second.rotatedBy(-3) shouldBe it.first
            }
        }
        test("simple rotation") {
            HexDirection.RIGHT.rotatedBy(1) shouldBe HexDirection.RIGHT.rotatedBy(-5)
            HexDirection.RIGHT.rotatedBy(1) shouldBe HexDirection.DOWN_RIGHT
            HexDirection.RIGHT.turnCountTo(HexDirection.DOWN_RIGHT) shouldBe 1
        }
    }
    context("CubeDirections") {
        test("produce correct opposites") {
            listOf(
                CubeDirection.LEFT to CubeDirection.RIGHT,
                CubeDirection.UP_LEFT to CubeDirection.DOWN_RIGHT,
                CubeDirection.DOWN_LEFT to CubeDirection.UP_RIGHT,
            ).forAll {
                it.first shouldNotBe it.second
                it.first.opposite() shouldBe it.second
                it.second.opposite() shouldBe it.first
                it.first.rotatedBy(3) shouldBe it.second
                it.first.rotatedBy(-3) shouldBe it.second
                it.second.rotatedBy(-3) shouldBe it.first
                
                it.first.vector.rotatedBy(3) shouldBe it.second.vector
                it.second.vector shouldBe it.first.vector.unaryMinus()
            }
        }
        test("simple rotation") {
            CubeDirection.RIGHT.rotatedBy(1) shouldBe CubeDirection.RIGHT.rotatedBy(-5)
            CubeDirection.RIGHT.rotatedBy(1) shouldBe CubeDirection.DOWN_RIGHT
            CubeDirection.RIGHT.rotatedBy(-1) shouldBe CubeDirection.UP_RIGHT
            CubeDirection.RIGHT.turnCountTo(CubeDirection.DOWN_RIGHT) shouldBe 1
            CubeDirection.RIGHT.turnCountTo(CubeDirection.UP_RIGHT) shouldBe -1
            CubeDirection.RIGHT.turnCountTo(CubeDirection.LEFT) shouldBe 3
        }
    }
})
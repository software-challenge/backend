package sc.plugin2021

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BoardTest : WordSpec({
    val changingFields = setOf(
            Field(Coordinates(2, 3), FieldContent.YELLOW),
            Field(Coordinates(19, 2), FieldContent.YELLOW),
            Field(Coordinates(8, 3), FieldContent.YELLOW),
            Field(Coordinates(4, 9), FieldContent.YELLOW)
    )
    "Boards" When {
        val one = Board()
        val two = Board()
        "compared" should {
            "equal each other" {
                one shouldBe two
            }
        }
        "they differ" should {
            changingFields.forEach { two[it.coordinates] = it.content }
            "not equal each other" {
                one shouldNotBe two
            }
            "produce a list of differences" {
                one.compare(two) shouldBe changingFields
            }
        }
        "both are equally modified" should {
            changingFields.forEach { two[it.coordinates] = it.content }
            changingFields.forEach { one[it.coordinates] = it.content }
            "equal each other" {
                one shouldBe two
            }
            "produce an empty changelist" {
                one.compare(two) shouldHaveSize 0
            }
        }
        "converted to string" should {
            one[0, 0] = FieldContent.RED
            one[1, 3] = FieldContent.GREEN
            one[5, 9] = FieldContent.BLUE
            one[8, 6] = FieldContent.YELLOW
            val view = one.toString()
            "produce an accurate view" {
                view shouldBe """
                    R - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - G - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - Y - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - B - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                    - - - - - - - - - - - - - - - - - - - -
                """.trimIndent()
            }
        }
    }
})

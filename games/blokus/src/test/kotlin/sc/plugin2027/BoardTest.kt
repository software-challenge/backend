package sc.plugin2027

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.api.plugins.Coordinates

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
            changingFields.forEach { two[it.coordinates].content = it.content }
            "not equal each other" {
                one shouldNotBe two
            }
            println("one" + one)
            println("two" + two)
            println("changingFields" + changingFields)
            // I am not sure about this one.
            "produce a list of differences" {
                print(one.compare(two))
                one.compare(two) shouldBe changingFields
            }
        }
        "both are equally modified" should {
            changingFields.forEach { two[it.coordinates].content = it.content }
            changingFields.forEach { one[it.coordinates].content = it.content }
            "equal each other" {
                one shouldBe two
            }
            "produce an empty changelist" {
                one.compare(two) shouldHaveSize 0
            }
        }
        "converted to string" should {
            // FIXME here the board returns a FieldContent and not a field. How was this done?
            // FIXME is this the way it should work?
            one[0, 0] = Field(Coordinates(0, 0), FieldContent.RED)
            one[1, 3] = Field(Coordinates(1, 3), FieldContent.GREEN)
            one[5, 9] = Field(Coordinates(5, 9), FieldContent.BLUE)
            one[8, 6] = Field(Coordinates(8, 6), FieldContent.YELLOW)
            val view = one.prettyString()
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

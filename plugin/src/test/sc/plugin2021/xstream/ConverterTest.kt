package sc.plugin2021.xstream

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import sc.plugin2021.*
import sc.plugin2021.helper.shouldSerializeTo

/** Test the [BoardConverter] and [GameState] XML. */
class ConverterTest: WordSpec({
    val fields = setOf(
            Field(Coordinates(0, 0), FieldContent.RED),
            Field(Coordinates(1, 3), FieldContent.GREEN),
            Field(Coordinates(8, 6), FieldContent.YELLOW),
            Field(Coordinates(5, 9), FieldContent.BLUE)
    )
    "Boards" When {
        val board = Board()
        "empty" should {
            board.isEmpty() shouldBe true
            "serialise to empty tag" {
                board shouldSerializeTo "<board/>"
            }
        }
        "not empty" Should {
            fields.forEach { board[it.coordinates] = it.content }
            board.isEmpty() shouldBe false
            "serialise accordingly" {
                board shouldSerializeTo """
                    <board>
                      <field x="0" y="0" content="RED"/>
                      <field x="1" y="3" content="GREEN"/>
                      <field x="8" y="6" content="YELLOW"/>
                      <field x="5" y="9" content="BLUE"/>
                    </board>""".trimIndent()
            }
        }
    }
    "GameStates" When {
        val state = GameState(startPiece = PieceShape.PENTO_L)
        val board = state.board
        fields.forEach { board[it.coordinates] = it.content }
        "serialised" Should {
            "be encoded properly" {
                state shouldSerializeTo """
                    <state turn="0" round="1" startPiece="PENTO_L">
                      <startTeam class="team">ONE</startTeam>
                      <board>
                        <field x="0" y="0" content="RED"/>
                        <field x="1" y="3" content="GREEN"/>
                        <field x="8" y="6" content="YELLOW"/>
                        <field x="5" y="9" content="BLUE"/>
                      </board>
                      <blueShapes class="linked-hash-set">
                        <shape>MONO</shape>
                        <shape>DOMINO</shape>
                        <shape>TRIO_L</shape>
                        <shape>TRIO_I</shape>
                        <shape>TETRO_O</shape>
                        <shape>TETRO_T</shape>
                        <shape>TETRO_I</shape>
                        <shape>TETRO_L</shape>
                        <shape>TETRO_Z</shape>
                        <shape>PENTO_L</shape>
                        <shape>PENTO_T</shape>
                        <shape>PENTO_V</shape>
                        <shape>PENTO_S</shape>
                        <shape>PENTO_Z</shape>
                        <shape>PENTO_I</shape>
                        <shape>PENTO_P</shape>
                        <shape>PENTO_W</shape>
                        <shape>PENTO_U</shape>
                        <shape>PENTO_R</shape>
                        <shape>PENTO_X</shape>
                        <shape>PENTO_Y</shape>
                      </blueShapes>
                      <yellowShapes class="linked-hash-set">
                        <shape>MONO</shape>
                        <shape>DOMINO</shape>
                        <shape>TRIO_L</shape>
                        <shape>TRIO_I</shape>
                        <shape>TETRO_O</shape>
                        <shape>TETRO_T</shape>
                        <shape>TETRO_I</shape>
                        <shape>TETRO_L</shape>
                        <shape>TETRO_Z</shape>
                        <shape>PENTO_L</shape>
                        <shape>PENTO_T</shape>
                        <shape>PENTO_V</shape>
                        <shape>PENTO_S</shape>
                        <shape>PENTO_Z</shape>
                        <shape>PENTO_I</shape>
                        <shape>PENTO_P</shape>
                        <shape>PENTO_W</shape>
                        <shape>PENTO_U</shape>
                        <shape>PENTO_R</shape>
                        <shape>PENTO_X</shape>
                        <shape>PENTO_Y</shape>
                      </yellowShapes>
                      <redShapes class="linked-hash-set">
                        <shape>MONO</shape>
                        <shape>DOMINO</shape>
                        <shape>TRIO_L</shape>
                        <shape>TRIO_I</shape>
                        <shape>TETRO_O</shape>
                        <shape>TETRO_T</shape>
                        <shape>TETRO_I</shape>
                        <shape>TETRO_L</shape>
                        <shape>TETRO_Z</shape>
                        <shape>PENTO_L</shape>
                        <shape>PENTO_T</shape>
                        <shape>PENTO_V</shape>
                        <shape>PENTO_S</shape>
                        <shape>PENTO_Z</shape>
                        <shape>PENTO_I</shape>
                        <shape>PENTO_P</shape>
                        <shape>PENTO_W</shape>
                        <shape>PENTO_U</shape>
                        <shape>PENTO_R</shape>
                        <shape>PENTO_X</shape>
                        <shape>PENTO_Y</shape>
                      </redShapes>
                      <greenShapes class="linked-hash-set">
                        <shape>MONO</shape>
                        <shape>DOMINO</shape>
                        <shape>TRIO_L</shape>
                        <shape>TRIO_I</shape>
                        <shape>TETRO_O</shape>
                        <shape>TETRO_T</shape>
                        <shape>TETRO_I</shape>
                        <shape>TETRO_L</shape>
                        <shape>TETRO_Z</shape>
                        <shape>PENTO_L</shape>
                        <shape>PENTO_T</shape>
                        <shape>PENTO_V</shape>
                        <shape>PENTO_S</shape>
                        <shape>PENTO_Z</shape>
                        <shape>PENTO_I</shape>
                        <shape>PENTO_P</shape>
                        <shape>PENTO_W</shape>
                        <shape>PENTO_U</shape>
                        <shape>PENTO_R</shape>
                        <shape>PENTO_X</shape>
                        <shape>PENTO_Y</shape>
                      </greenShapes>
                      <lastMoveMono class="linked-hash-map"/>
                      <validColors class="linked-hash-set">
                        <color>BLUE</color>
                        <color>YELLOW</color>
                        <color>RED</color>
                        <color>GREEN</color>
                      </validColors>
                      <first displayName="">
                        <color class="team">ONE</color>
                      </first>
                      <second displayName="">
                        <color class="team">TWO</color>
                      </second>
                    </state>""".trimIndent()
            }
        }
    }
})

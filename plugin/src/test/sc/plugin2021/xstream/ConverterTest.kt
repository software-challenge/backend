package sc.plugin2021.xstream

import io.kotest.assertions.*
import io.kotest.assertions.show.Printed
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import sc.framework.plugins.Player
import sc.helpers.checkSerialization
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2021.*
import java.io.EOFException
import java.io.StringReader
import java.util.EnumMap

/** Test the [BoardConverter] and [GameState] XML. */
class ConverterTest: FunSpec({
    isolationMode = IsolationMode.SingleInstance
    val customBoard = Board(
            Field(Coordinates(0, 0), FieldContent.RED),
            Field(Coordinates(1, 3), FieldContent.GREEN),
            Field(Coordinates(8, 6), FieldContent.YELLOW),
            Field(Coordinates(5, 9), FieldContent.BLUE))
    
    context("Board serialization") {
        val board = Board()
        test("to empty tag") {
            board.isEmpty() shouldBe true
            board shouldSerializeTo "<board/>"
        }
        test("with content") {
            customBoard shouldSerializeTo """
                <board>
                  <field x="0" y="0" content="RED"/>
                  <field x="1" y="3" content="GREEN"/>
                  <field x="8" y="6" content="YELLOW"/>
                  <field x="5" y="9" content="BLUE"/>
                </board>""".trimIndent()
        }
    }
    context("GameState serialization") {
        val states = listOf(
                GameState(Player(Team.ONE, "Bob"), Player(Team.TWO, "Alice"), PieceShape.PENTO_I) to """
                    <state turn="0" round="1" startPiece="PENTO_I">
                      <startTeam class="team">ONE</startTeam>
                      <blueShapes>
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
                      <yellowShapes>
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
                      <redShapes>
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
                      <greenShapes>
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
                      <validColors>
                        <color>BLUE</color>
                        <color>YELLOW</color>
                        <color>RED</color>
                        <color>GREEN</color>
                      </validColors>
                      <first displayName="Bob">
                        <color class="team">ONE</color>
                      </first>
                      <second displayName="Alice">
                        <color class="team">TWO</color>
                      </second>
                      <board/>
                      <lastMoveMono/>
                    </state>""".trimIndent(),
                Color.values().associateWithTo(EnumMap(Color::class.java)) { color ->
                    LinkedHashSet(PieceShape.values().filterNot { it.size > color.ordinal })
                }.let { undeployedPieceShapes ->
                    GameState(
                            startPiece = PieceShape.PENTO_L,
                            lastMove = SetMove(Piece(Color.BLUE, PieceShape.MONO)),
                            board = customBoard,
                            turn = 70,
                            lastMoveMono = hashMapOf(Color.BLUE to true),
                            blueShapes = undeployedPieceShapes.getValue(Color.BLUE),
                            yellowShapes = undeployedPieceShapes.getValue(Color.YELLOW),
                            redShapes = undeployedPieceShapes.getValue(Color.RED),
                            greenShapes = undeployedPieceShapes.getValue(Color.GREEN),
                            validColors = ArrayList(Color.values().filterNot { it == Color.BLUE }),
                    )
                } to """
                    <state turn="70" round="18" startPiece="PENTO_L">
                      <startTeam class="team">ONE</startTeam>
                      <blueShapes/>
                      <yellowShapes>
                        <shape>MONO</shape>
                      </yellowShapes>
                      <redShapes>
                        <shape>MONO</shape>
                        <shape>DOMINO</shape>
                      </redShapes>
                      <greenShapes>
                        <shape>MONO</shape>
                        <shape>DOMINO</shape>
                        <shape>TRIO_L</shape>
                        <shape>TRIO_I</shape>
                      </greenShapes>
                      <validColors>
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
                      <board>
                        <field x="0" y="0" content="RED"/>
                        <field x="1" y="3" content="GREEN"/>
                        <field x="8" y="6" content="YELLOW"/>
                        <field x="5" y="9" content="BLUE"/>
                      </board>
                      <lastMove class="sc.plugin2021.SetMove">
                        <piece color="BLUE" kind="MONO" rotation="NONE" isFlipped="false">
                          <position x="0" y="0"/>
                        </piece>
                      </lastMove>
                      <lastMoveMono>
                        <entry>
                          <color>BLUE</color>
                          <boolean>true</boolean>
                        </entry>
                      </lastMoveMono>
                    </state>""".trimIndent()
        )
        val reader = testXStream.createObjectInputStream(StringReader("<protocol>${states.joinToString("") { it.second }}</protocol>"))
        states.forEach { (state, xml) ->
            test(state.toString()) {
                checkSerialization(testXStream, state, xml) { obj, deserialized ->
                    if(obj != deserialized)
                        throw failure(Expected(Printed(obj.longString())), Actual(Printed(deserialized.longString())))
                }
                reader.readObject() shouldBe state
            }
        }
        shouldThrow<EOFException> {
            reader.readObject()
        }
   
        test("update round from turn") {
            val state = GameState(turn = 10)
            testXStream.toXML(state) shouldContain "round=\"3\""
            state.turn = 70
            testXStream.toXML(state) shouldContain  "round=\"18\""
            state.round shouldBe 18
        }
    }
})

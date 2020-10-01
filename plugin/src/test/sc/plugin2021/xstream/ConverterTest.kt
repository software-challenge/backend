package sc.plugin2021.xstream

import io.kotest.core.spec.style.StringSpec
import sc.plugin2021.FieldContent
import sc.plugin2021.GameState
import sc.plugin2021.PieceShape
import sc.plugin2021.helper.shouldSerializeTo

/** Test the [BoardConverter] and [GameState] XML. */
class ConverterTest: StringSpec({
    "GameState with Board conversion" {
        val state = GameState(startPiece = PieceShape.PENTO_L)
        val board = state.board
        board shouldSerializeTo "<board/>"
        
        board[0, 0] = FieldContent.RED
        board[1, 3] = FieldContent.GREEN
        board[8, 6] = FieldContent.YELLOW
        board[5, 9] = FieldContent.BLUE
        
        val boardXML = """
              <board>
                <field x="0" y="0" content="RED"/>
                <field x="1" y="3" content="GREEN"/>
                <field x="8" y="6" content="YELLOW"/>
                <field x="5" y="9" content="BLUE"/>
              </board>"""
        board shouldSerializeTo boardXML.trimIndent()
        
        state shouldSerializeTo """
            <state currentColorIndex="0" turn="0" round="1" startPiece="PENTO_L">
              <startTeam class="team">ONE</startTeam>$boardXML
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
              <orderedColors>
                <color>BLUE</color>
                <color>YELLOW</color>
                <color>RED</color>
                <color>GREEN</color>
              </orderedColors>
              <first displayName="">
                <color class="team">ONE</color>
              </first>
              <second displayName="">
                <color class="team">TWO</color>
              </second>
              <startColor>BLUE</startColor>
            </state>""".trimIndent()
    }
})

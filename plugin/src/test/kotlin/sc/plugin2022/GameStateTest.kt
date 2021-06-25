package sc.plugin2022

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldHaveLineCount
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import java.util.EnumMap

class GameStateTest: FunSpec({
    context("XML Serialization") {
        test("empty state") {
            GameState(Board(HashMap())) shouldSerializeTo """
                <state turn="0">
                  <startTeam class="team">ONE</startTeam>
                  <board>
                    <pieces/>
                  </board>
                  <ambers enum-type="team"/>
                </state>
            """.trimIndent()
        }
        test("random state") {
            testXStream.toXML(GameState()) shouldHaveLineCount 72
        }
        test("later state") {
            GameState(makeBoard(5 y 6 to "R2", 0 y 0 to "m"),
                    27,
                    Move(4 y 4, 5 y 6),
                    EnumMap(mapOf(Team.ONE to 1, Team.TWO to 0))) shouldSerializeTo """
                <state turn="27">
                  <startTeam class="team">ONE</startTeam>
                  <board>
                    <pieces>
                      <entry>
                        <coordinates x="0" y="0"/>
                        <piece type="Moewe" team="TWO" count="1"/>
                      </entry>
                      <entry>
                        <coordinates x="5" y="6"/>
                        <piece type="Robbe" team="ONE" count="2"/>
                      </entry>
                    </pieces>
                  </board>
                  <lastMove>
                    <from x="4" y="4"/>
                    <to x="5" y="6"/>
                  </lastMove>
                  <ambers enum-type="team">
                    <entry>
                      <team>ONE</team>
                      <int>1</int>
                    </entry>
                    <entry>
                      <team>TWO</team>
                      <int>0</int>
                    </entry>
                  </ambers>
                </state>
            """.trimIndent()
        }
    }
})
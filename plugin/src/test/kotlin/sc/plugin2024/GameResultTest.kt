package sc.plugin2024

import io.kotest.core.spec.style.WordSpec
import sc.api.plugins.CubeDirection
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.MoveMistake
import sc.shared.InvalidMoveException
import sc.shared.Violation

class GameResultTest: WordSpec({
    "Result XML" should {
        val game = Game()
        "work when empty" {
            game.getResult() shouldSerializeTo """
                <result>
                  <definition>
                    <fragment name="Siegpunkte">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Punkte">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Passagiere">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <scores/>
                </result>
            """.trimIndent()
        }
        game.onPlayerJoined()
        game.onPlayerJoined()
        "work with tie result" {
            game.getResult() shouldSerializeTo """
                <result>
                  <definition>
                    <fragment name="Siegpunkte">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Punkte">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Passagiere">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <scores>
                    <entry>
                      <player team="ONE"/>
                      <score>
                        <part>1</part>
                        <part>0</part>
                        <part>0</part>
                      </score>
                    </entry>
                    <entry>
                      <player team="TWO"/>
                      <score>
                        <part>1</part>
                        <part>0</part>
                        <part>0</part>
                      </score>
                    </entry>
                  </scores>
                </result>
            """.trimIndent()
        }
        game.players.first().displayName = "Alice"
        game.players.last().displayName = "Bob"
        "work with full result" {
            game.currentState.performMoveDirectly(Move(Accelerate(2), Advance(3)))
            game.currentState.performMoveDirectly(Move(Accelerate(1), Advance(1), Turn(CubeDirection.DOWN_RIGHT), Advance(1)))
            game.players.first().violation = Violation.RULE_VIOLATION(InvalidMoveException(MoveMistake.NO_ACTIONS, Move()))
            game.getResult() shouldSerializeTo """
                <result>
                  <definition>
                    <fragment name="Siegpunkte">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Punkte">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Passagiere">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <scores>
                    <entry>
                      <player name="Alice" team="ONE"/>
                      <score>
                        <part>0</part>
                        <part>0</part>
                        <part>0</part>
                      </score>
                    </entry>
                    <entry>
                      <player name="Bob" team="TWO"/>
                      <score>
                        <part>2</part>
                        <part>2</part>
                        <part>0</part>
                      </score>
                    </entry>
                  </scores>
                  <winner team="TWO" regular="false" reason="Regelverletzung von Alice: Der Zug enthält keine Aktionen bei &apos;Move[]&apos;"/>
                </result>
            """.trimIndent()
        }
    }
    "WinConditions" should {
        // TODO
    }
})
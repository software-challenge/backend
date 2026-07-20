package sc.plugin2025

import io.kotest.core.spec.style.WordSpec
import sc.framework.plugins.TwoPlayerGame
import sc.helpers.shouldSerializeTo
import sc.plugin2025.util.GamePlugin
import sc.shared.InvalidMoveException
import sc.shared.Violation

class GameResultTest: WordSpec({
    "Result XML" should {
        val game = TwoPlayerGame(GamePlugin(), GameState())
        "work when empty" {
            game.getResult() shouldSerializeTo """
                <result>
                  <definition>
                    <fragment name="Siegpunkte">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Feldnummer">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Karotten">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <scores/>
                  <winner regular="true" reason="Beide Spieler sind gleichauf"/>
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
                    <fragment name="Feldnummer">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Karotten">
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
                        <part>68</part>
                      </score>
                    </entry>
                    <entry>
                      <player team="TWO"/>
                      <score>
                        <part>1</part>
                        <part>0</part>
                        <part>68</part>
                      </score>
                    </entry>
                  </scores>
                  <winner regular="true" reason="Beide Spieler sind gleichauf"/>
                </result>
            """.trimIndent()
        }
        game.players.first().displayName = "Alice"
        game.players.last().displayName = "Bob"
        game.currentState.performMoveDirectly(Advance(10))
        "work with violation result" {
            game.players.first().violation =
                Violation.RULE_VIOLATION(InvalidMoveException(HuIMoveMistake.MUST_EAT_SALAD, FallBack))
            game.getResult() shouldSerializeTo """
                <result>
                  <definition>
                    <fragment name="Siegpunkte">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Feldnummer">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Karotten">
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
                        <part>0</part>
                        <part>68</part>
                      </score>
                    </entry>
                  </scores>
                  <winner team="TWO" regular="false" reason="Regelverletzung von Alice: Auf einem Salatfeld muss ein Salat gegessen werden bei &apos;Zurückfallen&apos;"/>
                </result>
            """.trimIndent()
        }
        "work with regular result" {
            game.getResult() shouldSerializeTo """
                <result>
                  <definition>
                    <fragment name="Siegpunkte">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Feldnummer">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                    <fragment name="Karotten">
                      <aggregation>AVERAGE</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <scores>
                    <entry>
                      <player name="Alice" team="ONE"/>
                      <score>
                        <part>2</part>
                        <part>10</part>
                        <part>13</part>
                      </score>
                    </entry>
                    <entry>
                      <player name="Bob" team="TWO"/>
                      <score>
                        <part>0</part>
                        <part>0</part>
                        <part>68</part>
                      </score>
                    </entry>
                  </scores>
                  <winner team="ONE" regular="true" reason="Alice ist weiter vorne."/>
                </result>
            """.trimIndent()
        }
    }
})
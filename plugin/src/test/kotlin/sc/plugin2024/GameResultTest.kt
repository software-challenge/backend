package sc.plugin2024

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.MoveMistake
import sc.shared.InvalidMoveException
import sc.shared.Violation
import sc.shared.WinCondition
import sc.shared.WinReasonTie

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
                  <winner regular="true" reason="Beide Teams sind gleichauf"/>
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
                  <winner regular="true" reason="Beide Teams sind gleichauf"/>
                </result>
            """.trimIndent()
        }
        game.players.first().displayName = "Alice"
        game.players.last().displayName = "Bob"
        game.currentState.performMoveDirectly(Move(Accelerate(2), Advance(3)))
        game.currentState.performMoveDirectly(Move(Accelerate(1), Advance(1), Turn(CubeDirection.DOWN_RIGHT), Advance(1)))
        "work with violation result" {
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
                  <winner team="TWO" regular="false" reason="Regelverletzung von Alice: Der Zug enthält keine Aktionen bei &apos;Zug[]&apos;"/>
                </result>
            """.trimIndent()
        }
        game.currentState.performMoveDirectly(Move(Accelerate(-1), Turn(CubeDirection.DOWN_RIGHT), Advance(1)))
        "work with regular result" {
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
                        <part>2</part>
                        <part>3</part>
                        <part>0</part>
                      </score>
                    </entry>
                    <entry>
                      <player name="Bob" team="TWO"/>
                      <score>
                        <part>0</part>
                        <part>2</part>
                        <part>0</part>
                      </score>
                    </entry>
                  </scores>
                  <winner team="ONE" regular="true" reason="Alice hat mehr Punkte."/>
                </result>
            """.trimIndent()
        }
    }
    "Game Result" should {
        val boardXML = """
            <board nextDirection="RIGHT">
             <segment direction="RIGHT">
               <center q="0" r="0" s="0"/>
               <column>
                 <water/>
                 <water/>
                 <water/>
                 <water/>
                 <water/>
               </column>
               <column>
                 <water/>
                 <water/>
                 <water/>
                 <water/>
                 <water/>
               </column>
               <column>
                 <water/>
                 <water/>
                 <water/>
                 <water/>
                 <water/>
               </column>
               <column>
                 <water/>
                 <goal/>
                 <goal/>
                 <goal/>
                 <water/>
               </column>
             </segment>
           </board>
        """.trimIndent()
        val state = GameState(testXStream.fromXML(boardXML) as Board)
        val game = Game(state)
        game.onPlayerJoined()
        game.onPlayerJoined()
        "be correct on finish" {
            state.ships.forEach {
                it.position = CubeCoordinates(2, it.team.index - 1)
            }
            state.currentShip.points = state.calculatePoints(state.currentShip)
            state.otherShip.points = state.calculatePoints(state.otherShip)
            game.currentWinner() shouldBe WinCondition(null, WinReasonTie)
            game.getResult().win?.winner shouldBe null
            state.ships.last().passengers = 2
            game.getResult().win?.winner shouldBe Team.TWO
        }
    }
    "WinConditions" should {
        // TODO
    }
})
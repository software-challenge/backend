package sc.plugin2020

import org.junit.Assert
import org.junit.Test
import sc.plugin2020.util.Configuration
import sc.plugin2020.util.CubeCoordinates
import sc.plugin2020.util.TestGameUtil
import sc.plugin2020.util.TestJUnitUtil
import sc.protocol.responses.RoomPacket

class XMLTest {
    
    @Test
    fun gamestateToXmlTest() {
        val state = GameState()
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----------------" +
                "  --BG--------------" +
                " --------------------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        state.board.getField(0, 0).pieces.add(Piece(Team.RED, PieceType.ANT))
        state.board.getField(0, 0).pieces.add(Piece(Team.BLUE, PieceType.BEE))
        TestGameUtil.updateUndeployedPiecesFromBoard(state, true)
        TestJUnitUtil.assertContentEquals(listOf(Piece(Team.RED, PieceType.ANT)), state.getDeployedPieces(Team.RED))
        TestJUnitUtil.assertContentEquals(listOf(Piece(Team.BLUE, PieceType.BEE), Piece(Team.BLUE, PieceType.GRASSHOPPER)), state.getDeployedPieces(Team.BLUE))
        Assert.assertEquals(PieceType.BEE, state.board.getField(0, 0).topPiece?.type)
        state.second.displayName = "aBluePlayer"
        state.turn = 3
        Assert.assertEquals(Team.BLUE, state.currentTeam)
        state.lastMove = SetMove(Piece(Team.BLUE, PieceType.GRASSHOPPER), CubeCoordinates(-2, 4))
        val xstream = Configuration.xStream
        val xml = """
            |<state turn="3" currentTeam="BLUE">
            |  <startTeam class="sc.plugin2020.Team">RED</startTeam>
            |  <first displayName="">
            |    <color class="sc.plugin2020.Team">RED</color>
            |  </first>
            |  <second displayName="aBluePlayer">
            |    <color class="sc.plugin2020.Team">BLUE</color>
            |  </second>
            |  <board>
            |    <fields>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <field x="-5" y="0" z="5" isObstructed="false"/>
            |      <field x="-5" y="1" z="4" isObstructed="false"/>
            |      <field x="-5" y="2" z="3" isObstructed="false"/>
            |      <field x="-5" y="3" z="2" isObstructed="false"/>
            |      <field x="-5" y="4" z="1" isObstructed="false"/>
            |      <field x="-5" y="5" z="0" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <field x="-4" y="-1" z="5" isObstructed="false"/>
            |      <field x="-4" y="0" z="4" isObstructed="false"/>
            |      <field x="-4" y="1" z="3" isObstructed="false"/>
            |      <field x="-4" y="2" z="2" isObstructed="false"/>
            |      <field x="-4" y="3" z="1" isObstructed="false"/>
            |      <field x="-4" y="4" z="0" isObstructed="false"/>
            |      <field x="-4" y="5" z="-1" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <field x="-3" y="-2" z="5" isObstructed="false"/>
            |      <field x="-3" y="-1" z="4" isObstructed="false"/>
            |      <field x="-3" y="0" z="3" isObstructed="false"/>
            |      <field x="-3" y="1" z="2" isObstructed="false"/>
            |      <field x="-3" y="2" z="1" isObstructed="false"/>
            |      <field x="-3" y="3" z="0" isObstructed="false"/>
            |      <field x="-3" y="4" z="-1" isObstructed="false"/>
            |      <field x="-3" y="5" z="-2" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <null/>
            |      <null/>
            |      <field x="-2" y="-3" z="5" isObstructed="false"/>
            |      <field x="-2" y="-2" z="4" isObstructed="false"/>
            |      <field x="-2" y="-1" z="3" isObstructed="false"/>
            |      <field x="-2" y="0" z="2" isObstructed="false"/>
            |      <field x="-2" y="1" z="1" isObstructed="false"/>
            |      <field x="-2" y="2" z="0" isObstructed="false"/>
            |      <field x="-2" y="3" z="-1" isObstructed="false"/>
            |      <field x="-2" y="4" z="-2" isObstructed="false">
            |        <piece type="GRASSHOPPER">
            |          <owner class="sc.plugin2020.Team">BLUE</owner>
            |        </piece>
            |      </field>
            |      <field x="-2" y="5" z="-3" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <null/>
            |      <field x="-1" y="-4" z="5" isObstructed="false"/>
            |      <field x="-1" y="-3" z="4" isObstructed="false"/>
            |      <field x="-1" y="-2" z="3" isObstructed="false"/>
            |      <field x="-1" y="-1" z="2" isObstructed="false"/>
            |      <field x="-1" y="0" z="1" isObstructed="false"/>
            |      <field x="-1" y="1" z="0" isObstructed="false"/>
            |      <field x="-1" y="2" z="-1" isObstructed="false"/>
            |      <field x="-1" y="3" z="-2" isObstructed="false"/>
            |      <field x="-1" y="4" z="-3" isObstructed="false"/>
            |      <field x="-1" y="5" z="-4" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <field x="0" y="-5" z="5" isObstructed="false"/>
            |      <field x="0" y="-4" z="4" isObstructed="false"/>
            |      <field x="0" y="-3" z="3" isObstructed="false"/>
            |      <field x="0" y="-2" z="2" isObstructed="false"/>
            |      <field x="0" y="-1" z="1" isObstructed="false"/>
            |      <field x="0" y="0" z="0" isObstructed="false">
            |        <piece type="ANT">
            |          <owner class="sc.plugin2020.Team">RED</owner>
            |        </piece>
            |        <piece type="BEE">
            |          <owner class="sc.plugin2020.Team">BLUE</owner>
            |        </piece>
            |      </field>
            |      <field x="0" y="1" z="-1" isObstructed="false"/>
            |      <field x="0" y="2" z="-2" isObstructed="false"/>
            |      <field x="0" y="3" z="-3" isObstructed="false"/>
            |      <field x="0" y="4" z="-4" isObstructed="false"/>
            |      <field x="0" y="5" z="-5" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <field x="1" y="-5" z="4" isObstructed="false"/>
            |      <field x="1" y="-4" z="3" isObstructed="false"/>
            |      <field x="1" y="-3" z="2" isObstructed="false"/>
            |      <field x="1" y="-2" z="1" isObstructed="false"/>
            |      <field x="1" y="-1" z="0" isObstructed="false"/>
            |      <field x="1" y="0" z="-1" isObstructed="false"/>
            |      <field x="1" y="1" z="-2" isObstructed="false"/>
            |      <field x="1" y="2" z="-3" isObstructed="false"/>
            |      <field x="1" y="3" z="-4" isObstructed="false"/>
            |      <field x="1" y="4" z="-5" isObstructed="false"/>
            |      <null/>
            |    </fields>
            |    <fields>
            |      <field x="2" y="-5" z="3" isObstructed="false"/>
            |      <field x="2" y="-4" z="2" isObstructed="false"/>
            |      <field x="2" y="-3" z="1" isObstructed="false"/>
            |      <field x="2" y="-2" z="0" isObstructed="false"/>
            |      <field x="2" y="-1" z="-1" isObstructed="false"/>
            |      <field x="2" y="0" z="-2" isObstructed="false"/>
            |      <field x="2" y="1" z="-3" isObstructed="false"/>
            |      <field x="2" y="2" z="-4" isObstructed="false"/>
            |      <field x="2" y="3" z="-5" isObstructed="false"/>
            |      <null/>
            |      <null/>
            |    </fields>
            |    <fields>
            |      <field x="3" y="-5" z="2" isObstructed="false"/>
            |      <field x="3" y="-4" z="1" isObstructed="false"/>
            |      <field x="3" y="-3" z="0" isObstructed="false"/>
            |      <field x="3" y="-2" z="-1" isObstructed="false"/>
            |      <field x="3" y="-1" z="-2" isObstructed="false"/>
            |      <field x="3" y="0" z="-3" isObstructed="false"/>
            |      <field x="3" y="1" z="-4" isObstructed="false"/>
            |      <field x="3" y="2" z="-5" isObstructed="false"/>
            |      <null/>
            |      <null/>
            |      <null/>
            |    </fields>
            |    <fields>
            |      <field x="4" y="-5" z="1" isObstructed="false"/>
            |      <field x="4" y="-4" z="0" isObstructed="false"/>
            |      <field x="4" y="-3" z="-1" isObstructed="false"/>
            |      <field x="4" y="-2" z="-2" isObstructed="false"/>
            |      <field x="4" y="-1" z="-3" isObstructed="false"/>
            |      <field x="4" y="0" z="-4" isObstructed="false"/>
            |      <field x="4" y="1" z="-5" isObstructed="false"/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |    </fields>
            |    <fields>
            |      <field x="5" y="-5" z="0" isObstructed="false"/>
            |      <field x="5" y="-4" z="-1" isObstructed="false"/>
            |      <field x="5" y="-3" z="-2" isObstructed="false"/>
            |      <field x="5" y="-2" z="-3" isObstructed="false"/>
            |      <field x="5" y="-1" z="-4" isObstructed="false"/>
            |      <field x="5" y="0" z="-5" isObstructed="false"/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |    </fields>
            |  </board>
            |  <undeployedRedPieces>
            |    <piece type="BEE">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="SPIDER">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="SPIDER">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="SPIDER">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="GRASSHOPPER">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="GRASSHOPPER">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="BEETLE">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="BEETLE">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="ANT">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <piece type="ANT">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |  </undeployedRedPieces>
            |  <undeployedBluePieces>
            |    <piece type="SPIDER">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="SPIDER">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="SPIDER">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="GRASSHOPPER">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="BEETLE">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="BEETLE">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="ANT">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="ANT">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <piece type="ANT">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |  </undeployedBluePieces>
            |  <lastMove class="setmove">
            |    <piece type="GRASSHOPPER">
            |      <owner class="sc.plugin2020.Team">BLUE</owner>
            |    </piece>
            |    <destination x="-2" y="4" z="-2"/>
            |  </lastMove>
            |</state>""".trimMargin()
        Assert.assertEquals(xml, xstream.toXML(state))
        val fromXml = xstream.fromXML(xml) as GameState
        Assert.assertEquals(state, fromXml)
        TestJUnitUtil.assertContentEquals(state.getDeployedPieces(Team.RED), fromXml.getDeployedPieces(Team.RED))
        TestJUnitUtil.assertContentEquals(state.getDeployedPieces(Team.BLUE), fromXml.getDeployedPieces(Team.BLUE))
    }
    
    @Test
    fun moveToXmlTest() {
        val move = SetMove(Piece(Team.RED, PieceType.ANT), CubeCoordinates(1, 2, -3))
        val roomId = "42"
        val xstream = Configuration.xStream
        val xml = xstream.toXML(RoomPacket(roomId, move))
        val expect = """
            |<room roomId="$roomId">
            |  <data class="setmove">
            |    <piece type="ANT">
            |      <owner class="sc.plugin2020.Team">RED</owner>
            |    </piece>
            |    <destination x="1" y="2" z="-3"/>
            |  </data>
            |</room>""".trimMargin()
        Assert.assertEquals(expect, xml)
    }
    
    @Test
    fun xmlToDragMoveTest() {
        val xstream = Configuration.xStream
        val xml = """
            <room roomId="42">
              <data class="dragmove">
                <start>
                  <x>0</x>
                  <y>-1</y>
                  <z>1</z>
                </start>
                <destination>
                  <x>1</x>
                  <y>2</y>
                  <z>-3</z>
                </destination>
              </data>
            </room>"""
        val room = xstream.fromXML(xml) as RoomPacket
        val expect = DragMove(CubeCoordinates(0, -1, 1), CubeCoordinates(1, 2, -3))
        Assert.assertEquals(expect, room.data)
    }
    
    @Test
    fun xmlToSetMoveTest() {
        val xstream = Configuration.xStream
        val xml = """
            <room roomId="64a0482c-f368-4e33-9684-d5106228bb75">
              <data class="setmove">
                <piece type="BEETLE">
                  <owner class="sc.plugin2020.Team">RED</owner>
                </piece>
                <destination x="-2" y="0" z="2"/>
              </data>
            </room>"""
        val packet = xstream.fromXML(xml) as RoomPacket
        val expect = SetMove(Piece(Team.RED, PieceType.BEETLE), CubeCoordinates(-2, 0, 2))
        Assert.assertEquals(expect, packet.data)
    }
    
    @Test
    fun xmlToSkipMoveTest() {
        val xstream = Configuration.xStream
        val xml = """
            <room roomId="64a0482c-f368-4e33-9684-d5106228bb75">
              <data class="skipmove">
              </data>
            </room>"""
        val packet = xstream.fromXML(xml) as RoomPacket
        val expect = SkipMove
        Assert.assertEquals(expect, packet.data)
    }
    
}
package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.PlayerColor;
import sc.plugin2015.gui.renderer.FrameRenderer;

public class GameEndedDialog {

	public static void draw(FrameRenderer parent) {
		parent.pushStyle();		
		parent.pushMatrix();
		parent.textSize(22);
		//Grey out Game Area
		parent.fill(GuiConstants.colorGreyOut);
		parent.rect(0, 0, parent.getWidth(), parent.getHeight());
		
		//Box Groß
		float x = parent.getWidth() * GuiConstants.GAME_ENDED_SIZE;
		float y =  parent.getHeight() * GuiConstants.GAME_ENDED_SIZE;
		parent.fill(GuiConstants.colorLightGrey);
		parent.translate(x,y);
		parent.rect(0, 0, x, y, 7);
		
		//Box klein
		parent.fill(GuiConstants.colorHexFields);
		parent.rect(0, 0, x, parent.textAscent()+ 2* parent.textDescent(),7);
		
		//##Text
		//#Game Ended
		parent.pushMatrix();
			parent.fill(GuiConstants.colorText);
			String msg = "Das Spiel ist zu Ende!";		
			parent.translate((x - parent.textWidth(msg))/2, parent.textAscent() + parent.textDescent()); //mittig positionieren
			parent.text(msg, 0, 0);
		parent.popMatrix();
		
		//# Winner
		PlayerColor winner = parent.currentGameState.winner();
		if (winner == PlayerColor.RED) {
			msg = parent.currentGameState.getPlayerNames()[0] + " hat gewonnen!";
		} else if (winner == PlayerColor.BLUE) {
			msg = parent.currentGameState.getPlayerNames()[1] + " hat gewonnen!";
		}
		parent.pushMatrix();
			parent.translate((x - parent.textWidth(msg))/2, 3 * parent.textAscent() + parent.textDescent()); //mittig positionieren
			parent.text(msg,0,0);		
		parent.popMatrix();
		
		
		parent.popMatrix();
		parent.popStyle();
	}

}

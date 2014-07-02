package sc.plugin2015.gui.renderer.primitives;

import java.nio.charset.Charset;

import processing.core.PFont;
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
		// message für winningreason
		String winningReason = parent.currentGameState.winningReason();
		//message für endefenster
		String msg = "Das Spiel ist zu Ende!";
		String message = "Das Spiel ging unendschieden aus!";
		PlayerColor winner = parent.currentGameState.winner();
		if (winner == PlayerColor.RED) {
			message = parent.currentGameState.getPlayerNames()[0] + " hat gewonnen!";
		} else if (winner == PlayerColor.BLUE) {
			message = parent.currentGameState.getPlayerNames()[1] + " hat gewonnen!";
		}
		//Box Groß
		//float x = parent.getWidth() * GuiConstants.GAME_ENDED_SIZE;
		float y = parent.getHeight() * GuiConstants.GAME_ENDED_SIZE;
		
		float x = parent.getWidth();
		parent.fill(GuiConstants.colorLightGrey);
		parent.translate((parent.getWidth() - x)/2,y);
		parent.rect(0, 0, x, y, 7);
		
		//Box klein
		parent.fill(GuiConstants.colorHexFields);
		parent.rect(0, 0, x, parent.textAscent()+ 2* parent.textDescent(),7);
		
		//##Text
		//#Game Ended
		parent.pushMatrix();
			parent.fill(GuiConstants.colorText);
					
			parent.translate((x - parent.textWidth(msg))/2, parent.textAscent() + parent.textDescent()); //mittig positionieren
			parent.text(msg, 0, 0);
		parent.popMatrix();
		
		//# Winner
		
		parent.pushMatrix();
			parent.translate((x - parent.textWidth(message))/2, 3 * parent.textAscent() + parent.textDescent()); //mittig positionieren
			parent.text(message,0,0);		
		parent.popMatrix();
		//# Winning Reason
		parent.pushMatrix();
			
			PFont test = parent.createFont("Arial", 22);
			parent.textFont(test);
			parent.translate((x - parent.textWidth(winningReason))/2, 5 * parent.textAscent() + parent.textDescent());
			if(winningReason.contains("Beide Spieler sind")){
				byte[] b = winningReason.getBytes();
				String aUml = "\u00e4";
				b[59]= aUml.getBytes()[0];
				winningReason = new String(b);
			}
			parent.text(winningReason, 0, 0);

			
			
			
		parent.popMatrix();
		
		
		parent.popMatrix();
		parent.popStyle();
	}

}

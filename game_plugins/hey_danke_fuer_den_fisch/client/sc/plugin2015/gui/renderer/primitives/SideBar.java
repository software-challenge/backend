package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.PlayerColor;
import sc.plugin2015.gui.renderer.FrameRenderer;
import sc.plugin2015.gui.renderer.RenderConfiguration;

/**
 * Zeichnet Spielerinformationen (Punkte, Schollen) sowie die Pinguine am Anfang
 * des Spieles.
 * 
 * @author felix
 * 
 */
public class SideBar extends PrimitiveBase {

	public SideBar(FrameRenderer parent) {
		super(parent);
	}

	@Override
	public void draw() {
		parent.pushStyle();

		parent.stroke(1.0f); // Umrandung
		parent.fill(GuiConstants.colorSideBarBG);

		parent.pushMatrix();
		parent.translate(parent.getWidth() * GuiConstants.SIDE_BAR_START_X,
				GuiConstants.SIDE_BAR_START_Y);
		parent.rect(0, 0, parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH,
				parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT);
		// Text
		// erster Spieler
		parent.textFont(GuiConstants.fonts[2]);
	  	parent.textSize(GuiConstants.fontSizes[2]);
	  	
	  	if(parent.currentGameState != null && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.RED)
	  		parent.fill(GuiConstants.colorRed);
	  	else
	  		parent.fill(GuiConstants.colorBlack);
	  	
		String redName = "";
		int redPoints = 0;
		int redFields = 0;
		if (parent.currentGameState != null) {
			redName = parent.currentGameState.getRedPlayer().getDisplayName();
			redPoints = parent.currentGameState.getRedPlayer().getPoints();
			redFields = parent.currentGameState.getRedPlayer().getFields();
		}
		parent.translate(20, parent.textAscent() + 20);
		// passe Textgröße an
		/*int preferredTextSize = (int) (30f/parent.textWidth(redName) * (parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH - 25));
		if(!(preferredTextSize > 30))
			parent.textSize(preferredTextSize);*/
		
		parent.text(redName, 0, 0);
		// Punkte + Schollen
		parent.textFont(GuiConstants.fonts[1]);
		parent.textSize(GuiConstants.fontSizes[1]);
		
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.text("Fische: " + redPoints, 0, 0);
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.text("Schollen: " + redFields, 0, 0);

		// Blauer Spieler.
		parent.textFont(GuiConstants.fonts[2]);
	  	parent.textSize(GuiConstants.fontSizes[2]);
	  	
		parent.translate(0, parent.textAscent() + parent.textDescent());
		if(parent.currentGameState != null && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.BLUE)
	  		parent.fill(GuiConstants.colorBlue);
	  	else
	  		parent.fill(GuiConstants.colorBlack);
		String blueName = "";
		int bluePoints = 0;
		int blueFields = 0;
		if (parent.currentGameState != null) {
			blueName = parent.currentGameState.getBluePlayer().getDisplayName();
			bluePoints = parent.currentGameState.getBluePlayer().getPoints();
			blueFields = parent.currentGameState.getBluePlayer().getFields();
		}
		
		// passe Textgröße an
		/*preferredTextSize = (int) (30f/parent.textWidth(blueName) * (parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH - 25));
		if(!(preferredTextSize > 30))
			parent.textSize(preferredTextSize);*/
				

		parent.text(blueName, 0, 0);
		// Punkte + Schollen
		parent.textSize(GuiConstants.fontSizes[1]);
		parent.textFont(GuiConstants.fonts[1]);
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.textSize(25);
		parent.text("Fische: " + bluePoints, 0, 0);
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.text("Schollen: " + blueFields, 0, 0);

		// Debug Ausgabe
		if (RenderConfiguration.optionDebug) {
			parent.translate(0, parent.textAscent() + parent.textDescent());
			parent.textFont(GuiConstants.fonts[1]);
			parent.textSize(GuiConstants.fontSizes[1]);
			parent.fill(GuiConstants.colorDarkGrey);
			parent.text(parent.frameRate, 0, 0);
			if(parent.currentGameState != null && parent.currentGameState.getLastMove() != null)
				for(int i = 0; i < parent.currentGameState.getLastMove().getHints().size(); i++){
					parent.translate(0, parent.textAscent() + parent.textDescent());
					parent.text(parent.currentGameState.getLastMove().getHints().get(i).getContent(), 0, 0);
				}
		}
		
		parent.popMatrix();

		parent.popStyle();
	}

}

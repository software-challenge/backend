package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.gui.renderer.FrameRenderer;

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
		// TODO Auto-generated method stub
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
		parent.textSize(30);
		parent.fill(GuiConstants.colorRed);
		String redName = "";
		int redPoints = 0;
		int redFields = 0;
		if (parent.currentGameState != null) {
			redName = parent.currentGameState.getRedPlayer()
					.getDisplayName();
			redPoints = parent.currentGameState.getRedPlayer().getPoints();
			redFields = parent.currentGameState.getRedPlayer().getFields();
		}
		parent.translate(20, parent.textAscent() + 20);
		parent.text(redName, 0, 0);
		// Punkte + Schollen
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.textSize(25);
		parent.text("Punkte: " + redPoints, 0, 0);
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.text("Schollen: " + redFields,0,0);
		

		// parent.
		parent.textSize(30);
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.fill(GuiConstants.colorBlue);
		String blueName = "";
		int bluePoints = 0;
		int blueFields = 0;
		if(parent.currentGameState != null){
			blueName = parent.currentGameState.getBluePlayer()
					.getDisplayName();
			bluePoints = parent.currentGameState.getBluePlayer().getPoints();
			blueFields = parent.currentGameState.getBluePlayer().getFields();
		}
		
		parent.text(blueName, 0, 0);
		// Punkte + Schollen
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.textSize(25);
		parent.text("Punkte: " + bluePoints, 0, 0);
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.text("Schollen: " + blueFields,0,0);
		

		parent.popMatrix();

		parent.popStyle();
	}

}

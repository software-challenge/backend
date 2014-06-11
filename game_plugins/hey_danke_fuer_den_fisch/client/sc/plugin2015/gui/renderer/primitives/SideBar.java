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
		//Text
		//erster Spieler
		parent.textSize(30);
		parent.fill(GuiConstants.colorRed);
		String redName = parent.currentGameState.getRedPlayer().getDisplayName();
		parent.translate(20, parent.textAscent() + 20);
		parent.text(redName, 0, 0);
		//Punkte + Schollen
		parent.translate(0, parent.textAscent() + parent.textDescent());
		parent.textSize(25);
		parent.text("Punkte: ", 0, 0);
		
		//parent.
		
		parent.fill(GuiConstants.colorBlue);
		String blueName = parent.currentGameState.getRedPlayer().getDisplayName();
		
		parent.popMatrix();

		parent.popStyle();
	}

}

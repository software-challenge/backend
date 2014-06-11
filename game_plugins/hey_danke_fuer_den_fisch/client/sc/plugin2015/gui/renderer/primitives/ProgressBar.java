package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.GamePlugin;
import sc.plugin2015.gui.renderer.FrameRenderer;

/**
 * Zeichnet den Spielverlauf. Rundenanzahl + Bar
 * 
 * @author felix
 * 
 */
public class ProgressBar extends PrimitiveBase {

	public ProgressBar(FrameRenderer par) {
		super(par);
	}

	@Override
	public void draw() {
		parent.pushStyle();

		// Umrandung
		parent.pushMatrix();
		parent.translate(0, parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT);
		parent.fill(GuiConstants.colorSideBarBG);
		parent.rect(0, 0, parent.getWidth(), parent.getHeight()
				* GuiConstants.PROGRESS_BAR_HEIGHT);
		// Text
		
		parent.translate(10, 10);
		parent.textSize(18);
		parent.fill(GuiConstants.colorText);
		parent.text("Runde:", 0, 0);
		parent.translate(0, parent.textDescent() + parent.textAscent());
		
		int round = 0;
		if(parent.currentGameState != null){
			round = parent.currentGameState.getRound();
		}
		parent.text(round, 0, 0);
		parent.translate(parent.textWidth(String.valueOf(round)), 0);
		parent.text("/", 0, 0);
		parent.translate(parent.textWidth("/"), 0);
		parent.text(GamePlugin.MAX_TURN_COUNT, 0, 0);
		parent.popMatrix();
		
		
		// Statusbalken
		parent.pushMatrix();

		parent.stroke(1.0f); // Umrandung
		parent.fill(GuiConstants.colorDarkGrey); // Filling

		parent.translate(50, parent.getHeight() - 30);
		parent.rect(0, 0, parent.getWidth() - 60, 20, 7);

		parent.popMatrix();

		parent.popStyle();
	}
}

package sc.plugin2016.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

/**
 * @author soeren
 * 
 */

public class GuiConstants {

	// ###### Colors
	public static int calcColor(int alpha, int r, int g, int b) {
		int col = 0;
		col |= alpha << 24;
		col |= r << 16;
		col |= g << 8;
		col |= b;
		return col;
	}

	public static final int colorBackGround = calcColor(200, 200, 200, 200);
	public static final int colorRed = calcColor(255, 200, 0, 0);
	public static final int colorBlue = calcColor(255, 0, 0, 200);
	public static final int colorLightRed = calcColor(255, 205, 92, 92);
  public static final int colorLightBlue = calcColor(255, 92, 92, 205);
	public static final int colorGreen = calcColor(255, 0, 255, 0);
	public static final int colorLightGrey = calcColor(255, 100, 100, 100);
	public static final int colorLightLightGrey = calcColor(255, 130, 130, 130);
	public static final int colorGrey = calcColor(255, 50, 68, 70);
	public static final int colorDarkGrey = calcColor(255, 30, 30, 30);
	public static final int colorBlack = calcColor(255, 0, 0, 0);
	public static final int colorSideBarBG = calcColor(200, 255, 255, 255);
	public static final int colorHexFields = calcColor(240, 21, 160, 177);
	public static final int colorHexFieldsHighlight = calcColor(240, 21, 195, 177);
	public static final int colorText = calcColor(255, 0, 0, 0);
	public static final int colorGreyOut = calcColor(100, 30, 30, 30);
  public static final int colorSwampFields = calcColor(255, 107, 142, 35);
  public static final int colorHighLighted = calcColor(255, 40, 205, 50);
  
	

	public static final int frameBorderSize = 5;

	// not needed
	/*
	 * public static final float GUI_BOARD_START_X = 1; public static final
	 * float GUI_BOARD_START_Y = 1;
	 */
	/**
	 * Breite des GuiBoard mit parent.width zu multiplizieren
	 */
	public static final float GUI_BOARD_WIDTH = 0.8f;
	/**
	 * Höhe des GuiBoard zu parent.height zu addieren
	 */
	public static final float GUI_BOARD_HEIGHT = -100;

	/**
	 * startwert der x-Koordinate der Sidebar mit parent.width zu multiplizieren
	 */
	public static final float SIDE_BAR_START_X = 0.8f;
	/**
	 * startwert der y-Koordinate der Sidebar zu 0 zu addieren, ist 0
	 * 
	 */
	public static final float SIDE_BAR_START_Y = 0;
	/**
	 * Breite der Sidebar mit parent.width zu multiplizieren
	 */
	public static final float SIDE_BAR_WIDTH = 0.2f;
	/**
	 * Höhe der Sidebar mit parent.heigth zu multiplizieren
	 */
	public static final float SIDE_BAR_HEIGHT = 0.875f;

	/**
	 * startwert der x-Koordinate der Progressbar zu 0 zu addieren
	 */
	public static final float PROGRESS_BAR_START_X = 50;
	/**
	 * startwert der y-Koordinate der Progressbar zu parent.height zu addieren
	 */
	public static final float PROGRESS_BAR_START_Y = -30;
	/**
	 * Breite der Progressbar zu parent.width zu addieren
	 */
	public static final float PROGRESS_BAR_WIDTH = -60;
	/**
	 * Höhe der Progressbar
	 *
	 */
	public static final float PROGRESS_BAR_HEIGHT = 1 - SIDE_BAR_HEIGHT;

	/**
	 * Anzahl an Pixeln in der Lücke zwischen zwei Hex-Feldern
	 */
	public static final float HEX_FIELD_GAP_SIZE = 0.015f;

	/**
	 * Sourcepath to find the default blue Penguin picture
	 */
	public static final String BLUE_PENGUIN_IMAGE = "resource/game/TuxBlau.png";

	/**
	 * Sourcepath to find the default red Penguin picture
	 */
	public static final String RED_PENGUIN_IMAGE = "resource/game/TuxRot.png";

	/**
	 * Sourcepath to Background Image
	 */
	public static final String BACKGROUND_IMAGE = "resource/game/background.png";
	
	/**
	 * Sourcepath to the image with one fish
	 */
	public static final String ONE_FISH_IMAGE_PATH = "resource/game/one_fish.png";
	
	/**
	 * Sourcepath to the image with two fish
	 */
	public static final String TWO_FISH_IMAGE_PATH = "resource/game/two_fish.png";
	
	/**
	 * Sourcepath to the image with three fish
	 */
	public static final String THREE_FISH_IMAGE_PATH = "resource/game/three_fish.png";

	/**
	 * Relative Size of the Game Ended Dialog
	 */
	public static final float GAME_ENDED_SIZE = 0.3f;

	/*
	 * Fonts 
	 * Wird OpenGL als Renderer genutzt, dann wird Text unscharf
	 * dargestellt. Für jede benutzte Textgröße einen Font gengerieren löst das
	 * Problem.
	 */
	public static int[] fontSizes = { 18, 25, 30 }; //The needed Font Sizes
	public static PFont[] fonts;
	
	public static void generateFonts(PApplet parent){
		fonts = new PFont[fontSizes.length];
		for(int i=0; i< fontSizes.length;i++){
			fonts[i] = parent.createFont("Arial", fontSizes[i]);
		}
	}
	
	public static PImage ONE_FISH_IMAGE = new PImage();
	
	public static PImage TWO_FISH_IMAGE = new PImage();
	
	public static PImage THREE_FISH_IMAGE = new PImage();
	
	public static PImage ONE_FISH_IMAGE_ORIGINAL = new PImage();
	
	public static PImage TWO_FISH_IMAGE_ORIGINAL = new PImage();
	
	public static PImage THREE_FISH_IMAGE_ORIGINAL = new PImage();

}

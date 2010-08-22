package sc.plugin_schaefchen.gui.renderer;

public class RenderConfiguration {

	public static final int ANTIALIASING = 0;
	public static final int TRANSPARANCY = 1;
	public static final int SIMEPLE_SHAPES = 2;
	public static final int BACKGROUND = 3;
	public static final int DRAG_N_DROP = 5;
	public static final int INDICES = 5;

	public static final boolean[] OPTIONS = new boolean[] { true, true, false,
			true };

	public static final String[] OPTION_NAMES = new String[] {
			"Kantenglättung", "Transparenz", "Einfache Geometrie",
			"Hintergrundbild" /* , "Drag'n'Drop", "Indizes zeigen" */};

}

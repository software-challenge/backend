package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.gui.renderer.FrameRenderer;

public abstract class PrimitiveBase {
	
	FrameRenderer parent;

	public PrimitiveBase(FrameRenderer parent) {
		this.parent = parent;
	}
	
	//public abstract void update();
	
	public abstract void draw();

}

package sc.plugin2016.gui.renderer.primitives;

import sc.plugin2016.gui.renderer.FrameRenderer;

public abstract class PrimitiveBase {
	
	FrameRenderer parent;

	public PrimitiveBase(FrameRenderer parent) {
		this.parent = parent;
	}
	
	//public abstract void update();
	
	public abstract void draw();
	
	public void kill() {
		if(parent != null) {
			parent.stop();
			parent.destroy();
		}
	}


}

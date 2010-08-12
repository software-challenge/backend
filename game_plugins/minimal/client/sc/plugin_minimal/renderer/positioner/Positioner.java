package sc.plugin_minimal.renderer.positioner;

public interface Positioner {
	
	public void scale(int size);
	public int[] scaledXs(int n);
	public int[] scaledYs(int n);
	
	

}
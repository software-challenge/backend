package sc.plugin_minimal;

public class HatSize {
	
	private int size0, size1, sizeg; 
	
	public void reset(){
		size0=0;
		size1=0;
		sizeg=0;
	}
	
	public void addSize(PlayerColor c){
		switch (c) {
		case PLAYER1:
			size0++;
			break;
			
		case PLAYER2:
			size1++;
			break;
			
		case NOPLAYER:
			sizeg++;
			break;
		}
	}
	
	public int getSize() {
		return size0+size1+sizeg;	
	}
	
	public int getSize(PlayerColor c){
		int res = 0;
		switch (c) {
		case PLAYER1:
			res = size0;
			break;
			
		case PLAYER2:
			res = size1;
			break;
			
		case NOPLAYER:
			res = sizeg;
			break;
		}
		
		return res;
	}
	
	public String toString(){
		return size0 + "/" + size1 + (sizeg>0 ? "/"+sizeg: "");
	}

	public void add(HatSize other) {
		size0 += other.size0;
		size1 += other.size1;
		sizeg += other.sizeg;
	}
	
	

}
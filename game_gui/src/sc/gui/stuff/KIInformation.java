package sc.gui.stuff;

public class KIInformation {

	private final String[] parameters;
	private final String path;
	
	public KIInformation(String[] parameters, String path) {
		super();
		this.parameters = parameters;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public String[] getParameters() {
		return parameters;
	}
	
	
}

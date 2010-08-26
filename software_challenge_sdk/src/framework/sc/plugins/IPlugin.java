package sc.plugins;


public interface IPlugin<HostType>
{
	public void initialize(HostType host);

	/**
	 * Open handles should be 
	 */
	public void unload();
}

package edu.cau.plugins;


public interface IPlugin<HostType>
{
	public void initialize(HostType host);

	public void unload();
}

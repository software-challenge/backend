package sc.plugin2010.renderer;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class RendererUtil
{
	public static Image getImage(String filename)
	{
		URL url = RendererUtil.class.getClassLoader().getResource(filename);
		return Toolkit.getDefaultToolkit().getImage(url);
	}
}

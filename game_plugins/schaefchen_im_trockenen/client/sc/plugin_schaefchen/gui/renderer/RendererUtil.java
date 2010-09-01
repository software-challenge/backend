package sc.plugin_schaefchen.gui.renderer;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RendererUtil
{
	private static final Logger	logger	= LoggerFactory
												.getLogger(RendererUtil.class);

	public static Image getImage(String filename)
	{
		URL url = RendererUtil.class.getClassLoader().getResource(filename);

		if (url == null)
		{
			return null;
		}

		logger.debug("Loading Image {}", url);

		return (new ImageIcon(url)).getImage();
	}
}

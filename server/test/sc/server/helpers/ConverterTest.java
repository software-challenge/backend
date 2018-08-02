package sc.server.helpers;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import sc.framework.plugins.IPerspectiveAware;
import sc.framework.plugins.IPerspectiveProvider;
import sc.server.Configuration;

public class ConverterTest
{
	public static class HasSecrets implements IPerspectiveProvider,
			IPerspectiveAware
	{
		public static final Object	hacker = new Object();

		public static final Object	goodFriend = new Object();

		private String				secret = "i-am-secret";

		private String				unimportant = "i-am-unimportant";

		private Object				perspective = null;

		@Override
		public Object getPerspective()
		{
			return this.perspective;
		}

		public void setPerspective(Object o)
		{
			this.perspective = o;
		}

		@Override
		public boolean isVisibleFor(Object viewer, String field)
		{
			return !(field.equals("secret") && hacker.equals(viewer));
		}
	}

	@Test
	public void shouldSerializeSensitiveDataForAuthorizedPeople()
	{
		HasSecrets data = new HasSecrets();
		data.setPerspective(HasSecrets.goodFriend);

		XStream xStream = Configuration.getXStream();
		String msg = xStream.toXML(data);

		Assert.assertNotSame(-1, msg.indexOf(data.secret));
		Assert.assertNotSame(-1, msg.indexOf(data.unimportant));
	}

	@Test
	public void shouldSerializeSensitiveDataForObservers()
	{
		HasSecrets data = new HasSecrets();
		data.setPerspective(null);

		XStream xStream = Configuration.getXStream();
		String msg = xStream.toXML(data);

		Assert.assertNotSame(-1, msg.indexOf(data.secret));
		Assert.assertNotSame(-1, msg.indexOf(data.unimportant));
	}

}

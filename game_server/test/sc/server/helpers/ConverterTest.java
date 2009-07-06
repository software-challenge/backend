package sc.server.helpers;

import org.junit.Assert;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import sc.helpers.IPerspectiveProvider;
import sc.server.Configuration;

public class ConverterTest
{
	public static class HasSecrets implements IPerspectiveProvider
	{
		public static final Object	hacker		= new Object();

		public static final Object	goodFriend	= new Object();

		private String				secret		= "i-am-secret";

		private String				unimportant	= "i-am-unimportant";

		private Object				perspective	= null;

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
			if (field.equals("secret") && hacker.equals(viewer))
			{
				return false;
			}

			return true;
		}
	}

	@Test
	public void shouldNotSerializeSensitiveData()
	{
		HasSecrets data = new HasSecrets();
		data.setPerspective(HasSecrets.hacker);

		XStream xStream = Configuration.getXStream();
		String msg = xStream.toXML(data);

		Assert.assertSame(-1, msg.indexOf(data.secret));
		Assert.assertNotSame(-1, msg.indexOf(data.unimportant));
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

	@Test
	public void shouldStillBeAbleToUnmarshal()
	{
		HasSecrets data = new HasSecrets();
		data.secret = "other-secret";
		data.unimportant = "other-unimportant";
		data.setPerspective(HasSecrets.hacker);

		XStream xStream = Configuration.getXStream();
		String msg = xStream.toXML(data);
		HasSecrets readData = (HasSecrets) xStream.fromXML(msg);

		Assert.assertEquals(data.unimportant, readData.unimportant);
		Assert.assertFalse(data.secret.equals(readData.secret));
	}
}

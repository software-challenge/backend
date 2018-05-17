package sc.server.helpers;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import sc.helpers.StringHelper;

public class StringHelperTest
{
	@Test
	public void testJoin()
	{
		String[] data = new String[] { "foo", "fam", "fai" };
		String result = StringHelper.join(Arrays.asList(data), "---");
		Assert.assertEquals("foo---fam---fai", result);

		String[] data2 = new String[0];
		String result2 = StringHelper.join(Arrays.asList(data2), "XYZ");
		Assert.assertEquals("", result2);
	}

}

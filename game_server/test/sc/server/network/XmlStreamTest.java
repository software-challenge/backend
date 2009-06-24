package sc.server.network;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParserException;

import sc.server.protocol.XmlStreamHelper;



public class XmlStreamTest
{
	public final static String SAMPLE_XML =
        "<?xml version=\"1.0\"?>\n"+
        "\n"+
        "<poem xmlns=\"http://www.megginson.com/ns/exp/poetry\">\n"+
        "<title>Roses are Red &amp; Green</title>\n"+
        "<l>Roses are red,</l>\n"+
        "<l>Violets are blue;</l>\n"+
        "<l>Sugar is sweet,</l>\n"+
        "<l>And I love you.</l>\n"+
        "</poem>";
	
	public final static String CDATA_XML =
        "<?xml version=\"1.0\"?>\n"+
        "\n"+
        "<session>\n"+
        "<title><![CDATA[1234567890]]></title>\n"+
        "</session>";

	@Test
	public void partialXmlDocumentTest() throws XmlPullParserException, IOException
	{
		MXParser xpp = new MXParser();
		xpp.setInput(new StringReader(SAMPLE_XML));
		XmlStreamHelper.readProlog(xpp, "poem");
		String first = XmlStreamHelper.readNode(xpp).trim();
		System.out.println(first);
		Assert.assertTrue(first.startsWith("<title>"));
		Assert.assertTrue(first.endsWith("</title>"));
	}
	
	@Test
	public void cdataXmlDocumentTest() throws XmlPullParserException, IOException
	{
		MXParser xpp = new MXParser();
		xpp.setInput(new StringReader(CDATA_XML));
		XmlStreamHelper.readProlog(xpp, "session");
		String first = XmlStreamHelper.readNode(xpp).trim();
		System.out.println(first);
		Assert.assertTrue(first.startsWith("<title>"));
		Assert.assertTrue(first.endsWith("</title>"));
	}
}

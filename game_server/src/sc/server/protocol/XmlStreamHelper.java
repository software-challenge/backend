package sc.server.protocol;

import java.io.IOException;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@Deprecated
public final class XmlStreamHelper
{
	private XmlStreamHelper()
	{
		// hide constructor
	}
	
	public static String readNode(XmlPullParser xpp) throws XmlPullParserException, IOException
	{
		int startDepth = xpp.getDepth();
		StringBuilder builder = new StringBuilder();
		boolean foundStartTag = false;
		
		int eventType = xpp.getEventType();
		do
		{
			if (eventType == XmlPullParser.START_DOCUMENT)
			{
				System.out.println("Start document");
			}
			else if (eventType == XmlPullParser.END_DOCUMENT)
			{
				System.out.println("End document");
			}
			else if (eventType == XmlPullParser.START_TAG)
			{
				processStartElement(builder, xpp);
				foundStartTag = true;
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				processEndElement(builder, xpp);
			}
			else if (eventType == XmlPullParser.TEXT)
			{
				processText(builder, xpp);
			}
			eventType = xpp.next();
		} while (!foundStartTag || (eventType != XmlPullParser.START_TAG && xpp.getDepth() != startDepth));
		
		return builder.toString();
	}

	private static void processText(StringBuilder builder, XmlPullParser xpp)
	{
		builder.append(xpp.getText());
	}

	private static void processEndElement(StringBuilder builder,
			XmlPullParser xpp)
	{
		builder.append("</");
		builder.append(xpp.getName());
		builder.append(">");
	}

	private static void processStartElement(StringBuilder builder,
			XmlPullParser xpp)
	{
		builder.append("<");
		builder.append(xpp.getName());
		for(int i=0; i<xpp.getAttributeCount(); i++)
		{
			builder.append(" ");
			builder.append(xpp.getAttributeName(i));
			builder.append("=\"");
			builder.append(xpp.getAttributeValue(i));
			builder.append("\"");
		}
		builder.append(">");
	}

	public static void readProlog(MXParser xpp, String rootNode) throws XmlPullParserException, IOException
	{
		boolean foundRootNode = false;
		int eventType = xpp.getEventType();
		do
		{
			if (eventType == XmlPullParser.START_DOCUMENT)
			{
				System.out.println("Start document");
			}
			else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(rootNode))
			{
				foundRootNode = true;
			}
			else
			{
				throw new XmlPullParserException("Unexpected Node.");
			}
			eventType = xpp.next();
		} while (!foundRootNode && eventType != XmlPullParser.END_DOCUMENT);
	}
}

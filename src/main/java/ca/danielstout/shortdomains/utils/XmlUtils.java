package ca.danielstout.shortdomains.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils
{
	public static List<Node> getChildNodesWithTag(Element elem, String tag)
	{
		NodeList children = elem.getChildNodes();
		List<Node> list = new ArrayList<>();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child.getNodeName().equals(tag)) list.add(child);
		}
		return list;
	}
}

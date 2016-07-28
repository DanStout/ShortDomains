package ca.danielstout.shortdomains.tld;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ca.danielstout.shortdomains.utils.XmlUtils;

public class WhoisListConverter
{

	private static final Logger log = LoggerFactory.getLogger(WhoisListConverter.class);

	private static final String whoisListUrl = "http://whois-server-list.github.io/whois-server-list/2.2/whois-server-list.xml";
	private static final String jsonOutput = "data/servers.json";

	public void convert()
	{
		InputStream stream = new BufferedInputStream(getUpdatedXml());
		List<TldServerMapping> servs = getServersFromXml(stream);
		writeServersToJson(servs, jsonOutput);
	}

	public InputStream getUpdatedXml()
	{
		try
		{
			return Request.Get(whoisListUrl).execute().returnContent().asStream();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void writeServersToJson(List<TldServerMapping> servs, String outputFile)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try
		{
			mapper.writeValue(new File(outputFile), servs);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private List<TldServerMapping> getServersFromXml(InputStream stream)
	{
		List<TldServerMapping> servs = new ArrayList<>();

		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		DocumentBuilder bldr;
		Document doc;
		try
		{
			bldr = fact.newDocumentBuilder();
			doc = bldr.parse(stream);
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			throw new RuntimeException(e);
		}

		NodeList domains = doc.getElementsByTagName("domain");
		for (int i = 0; i < domains.getLength(); i++)
		{
			Node domainNode = domains.item(i);
			Element domainElem = (Element) domainNode;

			String tld = domainElem.getAttribute("name");
			List<Node> servers = XmlUtils.getChildNodesWithTag(domainElem, "whoisServer");

			if (servers.isEmpty()) continue;

			TldServerMapping serv = new TldServerMapping();
			serv.setTld(tld);
			serv.setServers(new ArrayList<WhoisServer>());

			for (Node serverNode : servers)
			{
				Element serverElem = (Element) serverNode;
				String server = serverElem.getAttribute("host");

				WhoisServer ser = new WhoisServer();
				List<Node> patterns = XmlUtils.getChildNodesWithTag(serverElem, "availablePattern");
				if (patterns.isEmpty()) continue;

				Node patternNode = patterns.get(0);
				String pattern = patternNode.getTextContent();
				pattern = pattern.substring(2, pattern.length() - 2); // remove \Q and \E
				ser.setAvailableText(pattern);

				ser.setServer(server);
				serv.getServers().add(ser);
			}

			if (!serv.getServers().isEmpty())
			{
				log.debug("Added {}", serv);
				servs.add(serv);
			}
		}
		return servs;
	}
}

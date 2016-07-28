package ca.danielstout.shortdomains;

import java.io.IOException;
import java.net.SocketException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.danielstout.shortdomains.checker.DomainChecker;
import ca.danielstout.shortdomains.tld.TldServerMapping;
import ca.danielstout.shortdomains.tld.WhoisServer;
import ca.danielstout.shortdomains.tld.WhoisServerGetter;

public class Domaindemo
{
	private static final Logger log = LoggerFactory.getLogger(Domaindemo.class);

	static int counter = 0;

	private static void whois() throws SocketException, IOException
	{
		WhoisServerGetter get = new WhoisServerGetter();
		String domain = "shortdomains.ca";

		TldServerMapping serv = get.getWhoisForDomain(domain).get();
		WhoisServer ser = serv.getServers().get(0);
		WhoisClient whois = new WhoisClient();
		whois.connect(ser.getAddress());
		String result = whois.query(domain);
		whois.disconnect();

		boolean available = result.contains(ser.getAvailableText());
		log.debug("Available: {}", available);

		String regexExpiry = "(?s).*Expiry date:\\s*([0-9]{4}/[0-9]{2}/[0-9]{2}).*";
		Pattern p = Pattern.compile(regexExpiry);
		Matcher mat = p.matcher(result);
		if (mat.matches())
		{
			String expiry = mat.group(1);
			log.debug("Expires: {}", expiry);
		}
	}

	private static void domainDemo()
	{
		log.debug("Hello");
		DomainChecker checker = new DomainChecker();

		Consumer<String> perDomain = (s) ->
		{
			String domain = s + ".ca";
			boolean available = checker.isDomainAvailable(domain);
			log.debug("Domain '{}' is available: {}", domain, available);
		};
		consumeAllStringsBetweenLength(3, 3, perDomain);
	}

	private static void consumeAllStringsBetweenLength(int min, int max, Consumer<String> consumer)
	{
		char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();

		for (int i = min; i <= max; i++)
		{
			consumeStringsOfLength(letters, "", i, consumer);
		}

	}

	private static void consumeStringsOfLength(char set[], String prefix, int left,
		Consumer<String> consumer)
	{
		if (left == 0)
		{
			consumer.accept(prefix);
			counter++;
			if (counter > 10) System.exit(0);
			return;
		}

		for (int i = 0; i < set.length; ++i)
		{
			String newPrefix = prefix + set[i];
			consumeStringsOfLength(set, newPrefix, left - 1, consumer);
		}
	}
}

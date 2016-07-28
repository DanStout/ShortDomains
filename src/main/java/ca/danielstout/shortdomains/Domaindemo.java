package ca.danielstout.shortdomains;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Domaindemo
{
	private static final Logger log = LoggerFactory.getLogger(Domaindemo.class);

	static int counter = 0;

	private static void domainDemo()
	{
		log.debug("Hello");
		// DomainChecker checker = new DomainChecker();
		//
		// Consumer<String> perDomain = (s) ->
		// {
		// String domain = s + ".ca";
		// boolean available = checker.isDomainAvailable(domain);
		// log.debug("Domain '{}' is available: {}", domain, available);
		// };
		// consumeAllStringsBetweenLength(3, 3, perDomain);
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

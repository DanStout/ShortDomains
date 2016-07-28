package ca.danielstout.shortdomains.checker;

import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainChecker
{
	private final LinkedHashSet<DomainCheckStrategy> strategies = new LinkedHashSet<>();

	private static final Logger log = LoggerFactory.getLogger(DomainChecker.class);

	public DomainChecker()
	{
		strategies.add(new DnsDomainChecker());
	}

	public boolean isDomainAvailable(String domain)
	{
		log.debug("Checking domain: '{}'", domain);
		for (DomainCheckStrategy strat : strategies)
		{
			DomainCheckResult result = strat.checkDomain(domain);
			switch (result)
			{
			case AVAILABLE:
				return true;
			case TAKEN:
				return false;
			case UNSURE:
				continue;
			case CANNOT_CONTINUE:
				break;
			}
		}
		return false;
	}
}

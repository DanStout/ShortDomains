package ca.danielstout.shortdomains.checker;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;

public class DomainChecker
{
	/**
	 * LinkedHashSet preserves insertion order
	 */
	private final Set<DomainCheckStrategy> strategies = new LinkedHashSet<>();
	private final PublicSuffixList suffixes;
	private static final Logger log = LoggerFactory.getLogger(DomainChecker.class);

	@Inject
	public DomainChecker(DnsDomainChecker dnsChecker, WhoisDomainChecker whoisChecker,
		PublicSuffixList suffixList)
	{
		// Ensure we check DNS before WHOIS
		strategies.add(dnsChecker);
		strategies.add(whoisChecker);

		suffixes = suffixList;
	}

	public boolean isDomainAvailable(String domain)
	{
		String realDomain = suffixes.getRegistrableDomain(domain);
		log.debug("Checking domain: '{}' -> '{}'", domain, realDomain);
		if (realDomain == null) return false;

		for (DomainCheckStrategy strat : strategies)
		{
			DomainCheckResult result = strat.checkDomain(realDomain);
			log.debug("Strategy {} returned {}", strat.getClass().getSimpleName(), result);
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

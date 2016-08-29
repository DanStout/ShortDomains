package ca.danielstout.shortdomains.checker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.danielstout.shortdomains.checker.whois.TldServerMapping;
import ca.danielstout.shortdomains.checker.whois.WhoisServer;
import ca.danielstout.shortdomains.checker.whois.WhoisService;

public class WhoisDomainChecker implements DomainCheckStrategy
{
	private static final Logger log = LoggerFactory.getLogger(WhoisDomainChecker.class);
	private final WhoisService whoisServ;

	@Inject
	public WhoisDomainChecker(WhoisService service)
	{
		whoisServ = service;
	}

	@Override
	public DomainCheckResult checkDomain(String domain)
	{
		Optional<TldServerMapping> optMap = whoisServ.getMappingForDomain(domain);
		if (!optMap.isPresent()) return DomainCheckResult.CANNOT_CONTINUE;
		TldServerMapping map = optMap.get();

		for (WhoisServer server : map.getServers())
		{
			Optional<String> optRes = getWhoisResultForDomain(server.getAddress(), domain);
			if (!optRes.isPresent()) continue;
			String result = optRes.get();

			boolean available = StringUtils.containsIgnoreCase(result, server.getAvailableText());
			log.debug("Response from {} contains '{}': {}", server.getAddress(),
				server.getAvailableText(), available);

			String regex = server.getExpiryRegex();
			if (!available && regex != null)
			{
				Pattern pat = Pattern.compile(regex);
				Matcher mat = pat.matcher(result);
				if (mat.matches() && mat.groupCount() > 0)
				{
					String expiry = mat.group(1); // Group 0 is entire string
					log.debug("Found expiry: {}", expiry);
				}
			}

			server.setLastQueried(LocalDateTime.now());
			whoisServ.updateWhoisServer(server);
			// TODO: Add/update the checked_domain for this domain in the DB (including expiry)
			return available ? DomainCheckResult.AVAILABLE : DomainCheckResult.TAKEN;
		}

		return DomainCheckResult.CANNOT_CONTINUE; // None of the servers returned a result
	}

	/**
	 * Attempt to get the WHOIS text for a domain from a specific server, returning an empty
	 * optional if the request fails.
	 */
	private Optional<String> getWhoisResultForDomain(String server, String domain)
	{
		WhoisClient whois = new WhoisClient();
		try
		{
			whois.connect(server);
			String result = whois.query(domain);
			whois.disconnect();
			return Optional.of(result);
		}
		catch (IOException e)
		{
			return Optional.empty();
		}
	}
}

package ca.danielstout.shortdomains.checker;

import java.io.IOException;
import java.util.Optional;

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

		// TODO: loop over all servers
		WhoisServer server = map.getServers().get(0);
		Optional<String> optRes = getWhoisResultForDomain(server.getAddress(), domain);
		if (!optRes.isPresent()) return DomainCheckResult.CANNOT_CONTINUE;
		String result = optRes.get();

		boolean available = StringUtils.containsIgnoreCase(result, server.getAvailableText());
		log.debug("Response from {} contains '{}': {}", server.getAddress(),
			server.getAvailableText(), available);

		return available ? DomainCheckResult.AVAILABLE : DomainCheckResult.TAKEN;

		// TODO: Store expiry date
		// String regexExpiry = "(?s).*Expiry date:\\s*([0-9]{4}/[0-9]{2}/[0-9]{2}).*";
		// Pattern p = Pattern.compile(regexExpiry);
		// Matcher mat = p.matcher(result);
		// if (mat.matches())
		// {
		// String expiry = mat.group(1);
		// log.debug("Expires: {}", expiry);
		// }
	}

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

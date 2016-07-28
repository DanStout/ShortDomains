package ca.danielstout.shortdomains.tld;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory;

public class WhoisServerGetter
{
	private final Map<String, TldServerMapping> map;
	private final PublicSuffixList suffixList = (new PublicSuffixListFactory()).build();

	public WhoisServerGetter()
	{
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			List<TldServerMapping> servers = mapper.readValue(new File("data/servers.json"),
				new TypeReference<List<TldServerMapping>>()
				{
				});
			map = servers.stream().collect(Collectors.toMap(x -> x.getTld(), y -> y));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Optional<TldServerMapping> getWhoisForDomain(String domain)
	{
		String tld = suffixList.getPublicSuffix(domain);
		return Optional.ofNullable(map.get(tld));
	}
}

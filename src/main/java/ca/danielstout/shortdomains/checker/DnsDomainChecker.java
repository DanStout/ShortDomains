package ca.danielstout.shortdomains.checker;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

public class DnsDomainChecker implements DomainCheckStrategy
{
	private static final Logger log = LoggerFactory.getLogger(DnsDomainChecker.class);

	private enum LookupResult
	{
		SUCCESSFUL(0),
		UNRECOVERABLE(1),
		TRY_AGAIN(2),
		HOST_NOT_FOUND(3),
		TYPE_NOT_FOUND(4);

		private final int value;

		private static final Map<Integer, LookupResult> map = Arrays
			.stream(LookupResult.values())
			.collect(Collectors.toMap(x -> x.getValue(), x -> x));

		private LookupResult(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}

		public static LookupResult fromValue(int value)
		{
			return map.get(value);
		}
	}

	@Override
	public DomainCheckResult checkDomain(String domain)
	{
		Lookup l;
		try
		{
			l = new Lookup(domain, DClass.IN);
		}
		catch (TextParseException e)
		{
			log.info("Failed to parse domain '{}'", domain);
			return DomainCheckResult.CANNOT_CONTINUE;
		}
		Record[] result = l.run();

		LookupResult code = LookupResult.fromValue(l.getResult());
		if (code != LookupResult.SUCCESSFUL)
		{
			log.info("Result was unsuccessful; returned code {}", code);
			return DomainCheckResult.UNSURE;
		}

		log.debug("Full data: {}", Arrays.asList(result));
		log.debug("Result successful: {}", result[0].rdataToString());

		return DomainCheckResult.TAKEN;
	}
}

package ca.danielstout.shortdomains.checker;

public interface DomainCheckStrategy
{
	/**
	 * Check whether a domain is available
	 * @param domain - Domain to check
	 * @return true if this domain
	 */
	DomainCheckResult checkDomain(String domain);
}

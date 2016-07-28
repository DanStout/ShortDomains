package ca.danielstout.shortdomains.tld;

import java.util.List;

public class TldServerMapping
{
	private String tld;
	private List<WhoisServer> servers;

	public String getTld()
	{
		return tld;
	}

	public void setTld(String tld)
	{
		this.tld = tld;
	}

	public List<WhoisServer> getServers()
	{
		return servers;
	}

	public void setServers(List<WhoisServer> servers)
	{
		this.servers = servers;
	}

	@Override
	public String toString()
	{
		return "DomainServer [tld=" + tld + ", servers=" + servers + "]";
	}

}

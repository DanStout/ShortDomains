package ca.danielstout.shortdomains.checker.whois;

import java.time.LocalDateTime;

import ca.danielstout.shortdomains.utils.StringUtils;

public class WhoisServer
{
	private long id;
	private String address;
	private String availableText;
	private LocalDateTime lastQueried;
	private String expiryRegex;
	private String expiryFormat;
	private long tldId;

	public String getExpiryFormat()
	{
		return expiryFormat;
	}

	public void setExpiryFormat(String expiryFormat)
	{
		this.expiryFormat = expiryFormat;
	}

	public String getExpiryRegex()
	{
		return expiryRegex;
	}

	public void setExpiryRegex(String expiryRegex)
	{
		this.expiryRegex = expiryRegex;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public LocalDateTime getLastQueried()
	{
		return lastQueried;
	}

	public void setLastQueried(LocalDateTime lastQueried)
	{
		this.lastQueried = lastQueried;
	}

	public long getTldId()
	{
		return tldId;
	}

	public void setTldId(long tldId)
	{
		this.tldId = tldId;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getAddress()
	{
		return address;
	}

	public String getAvailableText()
	{
		return availableText;
	}

	public void setAvailableText(String availableText)
	{
		this.availableText = availableText;
	}

	@Override
	public String toString()
	{
		return StringUtils.makeToString(this);
	}
}

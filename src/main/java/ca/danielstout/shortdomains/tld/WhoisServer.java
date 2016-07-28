package ca.danielstout.shortdomains.tld;

public class WhoisServer
{
	private String address;
	/** May be null */
	private String availableText;

	public String getAddress()
	{
		return address;
	}

	public void setServer(String server)
	{
		this.address = server;
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
		return "ServerWithText [server=" + address + ", availableText=" + availableText + "]";
	}

}

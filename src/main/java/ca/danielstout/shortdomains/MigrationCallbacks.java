package ca.danielstout.shortdomains;

import java.sql.Connection;

import javax.inject.Inject;

import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.callback.BaseFlywayCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.danielstout.shortdomains.admin.User;
import ca.danielstout.shortdomains.admin.UserService;
import ca.danielstout.shortdomains.checker.whois.WhoisServer;
import ca.danielstout.shortdomains.checker.whois.WhoisService;

public class MigrationCallbacks extends BaseFlywayCallback
{
	private static final Logger log = LoggerFactory.getLogger(MigrationCallbacks.class);
	private final UserService userServ;
	private final WhoisService whoisServ;

	@Inject
	public MigrationCallbacks(UserService userService, WhoisService whoisService)
	{
		userServ = userService;
		whoisServ = whoisService;
	}

	@Override
	public void afterEachMigrate(Connection connection, MigrationInfo info)
	{
		String version = info.getVersion().getVersion();
		log.debug("Callback after migration #{}", version);
		switch (version)
		{
		case "2":
			callback2();
			break;
		case "3":
			callback3();
			break;
		}
	}

	private void callback2()
	{
		User u = new User();
		u.setEmail("admin@admin");
		u.setUsername("admin");
		u.setPassword("adminadminadmin");
		userServ.addUser(u);
	}

	private void callback3()
	{
		whoisServ.updateServers();

		WhoisServer serv = whoisServ.getMappingForDomain("blah.ca").get().getServers().get(0);
		String regex = "(?s).*Expiry date:\\s*([0-9]{4}/[0-9]{2}/[0-9]{2}).*";
		serv.setExpiryRegex(regex);
		whoisServ.updateWhoisServer(serv);
	}

}

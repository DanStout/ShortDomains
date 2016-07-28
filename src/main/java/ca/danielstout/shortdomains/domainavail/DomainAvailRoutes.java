package ca.danielstout.shortdomains.domainavail;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.danielstout.shortdomains.FormManager;
import ca.danielstout.shortdomains.checker.DomainChecker;
import ro.pippo.core.route.RouteGroup;

public class DomainAvailRoutes extends RouteGroup
{

	private static final Logger log = LoggerFactory.getLogger(DomainAvailRoutes.class);

	@Inject
	public DomainAvailRoutes(DomainChecker checker)
	{
		super("/domain");

		GET("/", ctx ->
		{
			ctx.render("domain");
		});

		POST("/check", ctx ->
		{
			String domain = ctx.getParameter("domain").toString().trim();
			boolean avail = checker.isDomainAvailable(domain);
			log.debug("'{}' is available: {}", domain, avail);

			FormManager mgr = new FormManager(ctx);
			mgr.flashObject("available", avail);
			mgr.flashObject("domain", domain);
			ctx.redirect("/domain");
		});

	}

}

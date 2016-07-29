package ca.danielstout.shortdomains;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.zaxxer.hikari.HikariDataSource;

import ca.danielstout.shortdomains.admin.UserRoutes;
import ca.danielstout.shortdomains.domainavail.DomainAvailRoutes;
import ca.danielstout.shortdomains.person.PersonController;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.controller.ControllerFactory;
import ro.pippo.core.PippoSettings;
import ro.pippo.core.route.CSRFHandler;
import ro.pippo.guice.GuiceControllerFactory;

public class App extends ControllerApplication
{
	private static final Logger log = LoggerFactory.getLogger(App.class);
	private final Injector injector;

	public App()
	{
		DataSource src = setupDb();
		Sql2o s = getSql2o(src);

		injector = setupDependencyInjection(s);
		migrate(src);

		registerTemplateEngine(CustomPebbleTemplateEngine.class);

		// DomainChecker checker = injector.getInstance(DomainChecker.class);
		// boolean a1 = checker.isDomainAvailable("www.dkjgjh.ca");
		// boolean a2 = checker.isDomainAvailable("danielstout.ca");
		// log.debug("{}, {}", a1, a2);

		// WhoisListConverter conv = new WhoisListConverter();
		// WhoisService serv = new WhoisService(s);
		// Optional<TldServerMapping> map = serv.getMappingForDomain("danielstout.ca");
		// log.debug("{}", map);
		// serv.storeMappings(conv.getServers(false));
	}

	@Override
	protected void onInit()
	{
		ALL("/.*", new CSRFHandler());
		ALL("/.*", ctx ->
		{
			FormManager mgr = new FormManager(ctx);
			mgr.moveFlashToLocal();
			ctx.next();
		});

		GET("/", ctx -> ctx.render("home"));
		GET("/people", PersonController.class, "index").named("person_list");
		POST("/people", PersonController.class, "addPerson").named("person_add");
		DELETE("/person/{id: [0-9]+}", PersonController.class, "deletePerson")
			.named("person_delete");

		addRouteGroup(injector.getInstance(UserRoutes.class));
		addRouteGroup(injector.getInstance(DomainAvailRoutes.class));
	}

	private Injector setupDependencyInjection(Sql2o sql2o)
	{
		Module mod = new GuiceModule(sql2o);
		Injector inj = Guice.createInjector(mod);
		ControllerFactory fact = new GuiceControllerFactory(inj);
		setControllerFactory(fact);
		return inj;
	}

	private DataSource setupDb()
	{
		PippoSettings sets = getPippoSettings();
		String url = sets.getRequiredString("db.url");
		String user = sets.getRequiredString("db.user");
		String pass = sets.getRequiredString("db.pass");

		JdbcDataSource db = new JdbcDataSource();
		db.setUrl(url);
		db.setUser(user);
		db.setPassword(pass);
		HikariDataSource src = new HikariDataSource();
		src.setDataSource(db);

		return src;
	}

	private void migrate(DataSource src)
	{
		Flyway fly = new Flyway();
		MigrationCallbacks call = injector.getInstance(MigrationCallbacks.class);
		fly.setCallbacks(call);
		fly.setDataSource(src);
		int applied = fly.migrate();
		log.debug("Applied {} migrations", applied);
	}

	private Sql2o getSql2o(DataSource src)
	{
		@SuppressWarnings("rawtypes")
		Map<Class, Converter> converters = new HashMap<>();

		Converter<LocalDateTime> c = new Converter<LocalDateTime>()
		{
			@Override
			public LocalDateTime convert(Object val) throws ConverterException
			{
				if (val == null) return null;
				return Timestamp.valueOf(val.toString()).toLocalDateTime();
			}

			@Override
			public Object toDatabaseParam(LocalDateTime val)
			{
				return val.toString();
			}
		};
		converters.put(LocalDateTime.class, c);

		Quirks q = new NoQuirks(converters);
		return new Sql2o(src, q);
	}
}

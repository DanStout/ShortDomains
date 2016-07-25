package ca.danielstout.pippolearn;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.zaxxer.hikari.HikariDataSource;

import ca.danielstout.pippolearn.person.PersonController;
import ca.danielstout.pippolearn.user.UserRoutes;
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
		migrate(src);
		Sql2o s = new Sql2o(src);
		injector = setupDependencyInjection(s);
		registerTemplateEngine(CustomPebbleTemplateEngine.class);
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

		UserRoutes routes = injector.getInstance(UserRoutes.class);
		addRouteGroup(routes);
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
		fly.setDataSource(src);
		int applied = fly.migrate();
		log.debug("Applied {} migrations", applied);
	}
}

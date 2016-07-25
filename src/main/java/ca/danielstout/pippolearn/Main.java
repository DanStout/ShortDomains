package ca.danielstout.pippolearn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.pippo.core.Application;
import ro.pippo.core.Pippo;
import ro.pippo.core.RuntimeMode;
import ro.pippo.core.WebServerSettings;

public class Main
{
	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args)
	{
		Application app = new App();

		RuntimeMode mode = RuntimeMode.getCurrent();
		String name = app.getApplicationName();
		String vers = app.getApplicationVersion();

		log.info("Starting server in {} with app {} on version {}", mode, name, vers);
		Pippo pip = new Pippo(app);
		pip.start();

		WebServerSettings sets = pip.getServer().getSettings();
		log.info("Started at {}:{}", sets.getHost(), sets.getPort());
	}
}

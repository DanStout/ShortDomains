package ca.danielstout.shortdomains;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sql2o.Sql2o;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;

import ca.danielstout.shortdomains.admin.UserService;
import ca.danielstout.shortdomains.person.PersonService;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory;

public class GuiceModule extends AbstractModule implements Module
{
	private final Sql2o sql2o;

	public GuiceModule(Sql2o sql2o)
	{
		this.sql2o = sql2o;
	}

	@Override
	protected void configure()
	{
		bind(PersonService.class).in(Singleton.class);
		bind(UserService.class).in(Singleton.class);
		bind(Sql2o.class).toInstance(this.sql2o);
		bind(PublicSuffixList.class).toInstance((new PublicSuffixListFactory()).build());

		ValidatorFactory validFact = Validation.buildDefaultValidatorFactory();
		Validator validator = validFact.getValidator();
		bind(Validator.class).toInstance(validator);
	}

}

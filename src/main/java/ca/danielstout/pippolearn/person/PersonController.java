package ca.danielstout.pippolearn.person;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.pippo.controller.Controller;
import ro.pippo.core.Param;
import ro.pippo.core.route.RouteContext;

public class PersonController extends Controller
{
	private static final Logger log = LoggerFactory.getLogger(PersonController.class);
	private final PersonService serv;

	@Inject
	public PersonController(PersonService personServ)
	{
		serv = personServ;
	}

	public void index()
	{
		List<Person> people = serv.getPeople();
		log.debug("Found {} people", people.size());
		getRouteContext().setLocal("people", people);
		getResponse().render("people");
	}

	public void addPerson()
	{
		RouteContext ctx = getRouteContext();
		String name = ctx.getParameter("name").toString();
		if (name == null || name.isEmpty())
		{
			ctx.flashError("Name may not be empty");
		}
		else
		{
			Person p = new Person();
			p.setName(name);
			serv.addPerson(p);
			ctx.flashSuccess("Added {}", p.getName());
		}
		ctx.redirect("/people");
	}

	public void deletePerson(@Param("id") long id)
	{
		serv.deletePerson(id);
		getResponse().redirect("/people");
	}
}

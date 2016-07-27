package ca.danielstout.pippolearn.user;

import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.danielstout.pippolearn.FormManager;
import ro.pippo.core.route.RouteGroup;

public class UserRoutes extends RouteGroup
{
	private static final String SES_USER = "user";
	private static final Logger log = LoggerFactory.getLogger(UserRoutes.class);

	@Inject
	public UserRoutes(UserService serv, Validator validator)
	{
		super("/user");

		GET("/login", ctx -> ctx.render("login"));

		POST("/login", ctx ->
		{
			String email = ctx.getParameter("email").toString().trim();
			String password = ctx.getParameter("password").toString().trim();
			Optional<User> user = serv.getUser(email, password);
			log.debug("User for email {}: {}", email, user);
			if (user.isPresent())
			{
				ctx.setSession(SES_USER, user.get());
				ctx.redirect("/");
			}
			else
			{
				ctx.flashError("Invalid email or password");
				ctx.redirect(ctx.getRequestUri());
			}

		});

		POST("/logout", ctx ->
		{
			ctx.removeSession(SES_USER);
			ctx.redirect("/");
		});

		GET("/register", ctx ->
		{
			log.debug("{}", ctx.getResponse().getLocals());
			ctx.render("register");
		});

		POST("/register", ctx ->
		{
			User user = ctx.createEntityFromParameters(User.class);
			log.debug("Attempting to register: {}", user);
			boolean success = false;

			boolean usernameTaken = serv.isUsernameTaken(user.getUsername());
			boolean emailTaken = serv.isEmailTaken(user.getEmail());

			Set<ConstraintViolation<User>> violations = validator.validate(user);

			if (violations.isEmpty() && !usernameTaken && !emailTaken)
			{
				success = serv.addUser(user);
				if (success)
				{
					ctx.setSession(SES_USER, user);
					ctx.redirect("/");
					return;
				}
			}

			FormManager mgr = new FormManager(ctx);
			user.setPassword("");
			mgr.flashObject(user);

			for (ConstraintViolation<User> viol : violations)
			{
				String path = viol.getPropertyPath().toString();
				mgr.addError(path, viol.getMessage());
			}

			if (usernameTaken) mgr.addError("username", "Username is already taken");
			if (emailTaken) mgr.addError("email", "This email is already registered");
			ctx.flashError("Failed to register");
			ctx.redirect(ctx.getRequestUri());

		});
	}
}

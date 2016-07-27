package ca.danielstout.pippolearn.user;

import java.util.Optional;

import javax.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

public class UserService
{
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private final Sql2o db;

	@Inject
	public UserService(Sql2o sql2o)
	{
		db = sql2o;
	}

	/**
	 * Saves a user in the DB; the user's password will be replaced with a hashed version before it
	 * is persisted.
	 * @param user to add
	 * @return true if user was added, else false
	 */
	public boolean addUser(User user)
	{
		String sql = "" +
			"insert into user (email, username, password) " +
			"values (:email, :username, :password)";
		String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		user.setPassword(hashed);
		try (Connection con = db.open())
		{
			Long id = con.createQuery(sql, true)
				.bind(user)
				.executeUpdate()
				.getKey(Long.class);
			user.setId(id);
			return true;
		}
		catch (Sql2oException ex)
		{
			log.debug("Exception while adding user", ex);
			return false;
		}
	}

	public boolean isUsernameTaken(String username)
	{
		String sql = "" +
			"select count(1) from user " +
			"where username = :user " +
			"limit 1";
		try (Connection con = db.open())
		{
			return con.createQuery(sql)
				.addParameter("user", username)
				.executeScalar(Boolean.class);
		}
	}

	public boolean isEmailTaken(String email)
	{
		String sql = "" +
			"select count(1) from user " +
			"where email = :email " +
			"limit 1";
		try (Connection con = db.open())
		{
			return con.createQuery(sql)
				.addParameter("email", email)
				.executeScalar(Boolean.class);
		}
	}

	/**
	 * Gets the user with a given email and password; if there is no matching email or the password
	 * does not match, return an empty {@link Optional}.
	 * @param email email address (case does not matter)
	 * @param plaintextPassword plaintext password
	 * @return Found user or empty optional
	 */
	public Optional<User> getUser(String email, String plaintextPassword)
	{
		String sql = "" +
			"select id, email, username, password from user " +
			"where email = :email";
		try (Connection con = db.open())
		{
			User user = con.createQuery(sql)
				.addParameter("email", email)
				.executeAndFetchFirst(User.class);
			if (user != null)
			{
				if (BCrypt.checkpw(plaintextPassword, user.getPassword()))
				{
					return Optional.of(user);
				}
			}
			return Optional.empty();
		}
	}
}

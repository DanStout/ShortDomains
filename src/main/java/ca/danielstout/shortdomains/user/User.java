package ca.danielstout.shortdomains.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class User
{
	private long id;

	@Email
	@NotNull
	@Size(min = 2, max = 255, message = "Email is required")
	private String email;

	@NotNull
	@Size(min = 2, max = 20, message = "Username must be 2-20 characters long")
	private String username;

	@NotNull
	@Size(min = 8, max = 5000, message = "Password must be at least 8 characters")
	private String password;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	@Override
	public String toString()
	{
		return "User [id=" + id + ", email=" + email + ", username=" + username + "]";
	}
}

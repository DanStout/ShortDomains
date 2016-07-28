package ca.danielstout.shortdomains.tld;

import java.util.List;

import javax.inject.Inject;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class WhoisService
{

	private final Sql2o db;

	@Inject
	public WhoisService(Sql2o sql2o)
	{
		db = sql2o;
	}

	public void storeMappings(List<TldServerMapping> mappings)
	{
		String sql = ""
			+ "merge into tld (suffix) "
			+ "key (suffix) "
			+ "values (:suffix);";



//set @suff = 'ca';
//merge into tld(suffix) key (suffix) values (@suff);
//set @scop = scope_identity();
//set @tld_id = SELECT CASE WHEN @scop is NULL THEN (select id from tld where suffix = @suff) ELSE @scop END;
//
//insert into whois_server(address, available_text, tld_id) values ('fake.whois.cira.ca', 'not registered', @tld_id);
//
//select * from tld join whois_server on tld.id = whois_server.tld_id;

		try (Connection con = db.beginTransaction())
		{
			// con.createQuery(sql, true)
			// con.
		}
	}

}

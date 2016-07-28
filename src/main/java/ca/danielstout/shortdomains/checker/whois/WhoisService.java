package ca.danielstout.shortdomains.checker.whois;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;

public class WhoisService
{
	private final PublicSuffixList suffixList;
	private static final Logger log = LoggerFactory.getLogger(WhoisService.class);
	private final Sql2o db;

	@Inject
	public WhoisService(Sql2o sql2o, PublicSuffixList publicSuffixList)
	{
		suffixList = publicSuffixList;
		db = sql2o;
	}

	public Optional<TldServerMapping> getMappingForDomain(String domain)
	{
		String tld = suffixList.getPublicSuffix(domain);

		try (Connection con = db.beginTransaction())
		{
			String selectTld = "select id from tld where suffix = :suffix";
			Long id = con
				.createQuery(selectTld)
				.addParameter("suffix", tld)
				.executeScalar(Long.class);
			if (id == null) return Optional.empty();

			String selectServers = "select * from whois_server where tld_id = :tld_id";
			List<WhoisServer> servers = con
				.createQuery(selectServers)
				.setAutoDeriveColumnNames(true)
				.addParameter("tld_id", id)
				.executeAndFetch(WhoisServer.class);
			con.commit();

			TldServerMapping mapping = new TldServerMapping();
			mapping.setTld(tld);
			mapping.setServers(servers);
			return Optional.of(mapping);
		}
	}

	public void storeMappings(List<TldServerMapping> mappings)
	{
		String sql = ""
			+ "merge into whois_server(address, available_text, tld_id) "
			+ "key (address, tld_id) "
			+ "values (:address, :availtext, :tld_id)";

		for (TldServerMapping map : mappings)
		{
			try (Connection con = db.beginTransaction())
			{
				long id = storeOrGetTld(con, map.getTld());

				Query query = con.createQuery(sql);

				for (WhoisServer serv : map.getServers())
				{
					log.debug("Adding server: {} {} {}", serv.getAddress(), serv.getAvailableText(),
						id);
					query
						.addParameter("address", serv.getAddress())
						.addParameter("availtext", serv.getAvailableText())
						.addParameter("tld_id", id)
						.addToBatch();
				}

				query.executeBatch();
				con.commit();
			}
		}
	}

	private long storeOrGetTld(Connection con, String tld)
	{
		log.debug("Getting id for tld: {}", tld);

		String mergeSql = "merge into tld(suffix) key (suffix) values (:suff)";
		String selectSql = "select id from tld where suffix = :suff";

		Long id = con.createQuery(mergeSql)
			.addParameter("suff", tld)
			.executeUpdate()
			.getKey(Long.class);
		log.debug("Fetched id: {}", id);

		if (id == null)
		{
			id = con.createQuery(selectSql)
				.addParameter("suff", tld)
				.executeScalar(Long.class);
			log.debug("TLD already stored; selected {}", id);
		}

		return id;
	}

}

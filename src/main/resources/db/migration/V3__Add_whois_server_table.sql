create table tld
(
	id identity primary key,
	suffix varchar(100) not null
);

create table whois_server
(
	id identity primary key,
	address varchar(255) not null,
	available_text varchar(255) not null,
	last_queried timestamp,
	tld_id bigint not null,
	foreign key(tld_id) references tld(id)
);

alter table whois_server add constraint uq_whois_server_tld_addr unique(tld_id, address);

create table checked_domain
(
	id identity primary key,
	domain varchar(512) not null,
	last_queried timestamp not null,
	expiry timestamp,
	is_available boolean not null
);
create table user
(
	id identity primary key,
	email varchar_ignorecase(255) unique,
	username varchar_ignorecase(20) unique,
	password varchar(60)
)
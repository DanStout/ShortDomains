create table person
(
	ID identity primary key,
	NAME varchar(100) not null
);

insert into person(name) values('Billy Bob'), ('Frank Sina'), ('Sally Ann')
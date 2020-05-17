create table Conversation(
	convo_id integer primary key autoincrement,
	name varchar(255) not null,
	stage integer not null,
	sec_key blob not null
);
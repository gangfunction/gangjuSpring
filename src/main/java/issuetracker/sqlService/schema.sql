create table sqlmap(
  key_varchar(100) primary key,
  sql_varchar(100) not null
);
insert into sqlmap(key_, sql_) values('key1','sql1');
insert into sqlmap(key_, sql_) values('key2','sql2');
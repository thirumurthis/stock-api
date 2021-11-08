/*For now the database script is not used */
/*
CREATE TABLE IF NOT EXISTS stock_store (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  symbol VARCHAR(50) NOT NULL,
  average_price DECIMAL(20,10) NOT NULL,
  stock_count INT NOT NULL,
  user_name varchar(255) NOT NULL,
  active TINYINT NOT NULL
);


create table if not exists users(
	username varchar_ignorecase(50) not null primary key,
	password varchar_ignorecase(50) not null,
	active boolean not null default true
);

create table if not exists authorities (
	username varchar_ignorecase(50) not null,
	authority varchar_ignorecase(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index if not exists ix_auth_username on authorities (username,authority);
*/
--# insert into stock_store (symbol,average_price,stock_count,user_id) values ('MSFT',100.00,10,1)
CREATE TABLE IF NOT EXISTS stock_store (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  symbol VARCHAR(250) NOT NULL,
  average_price DECIMAL(20,10) NOT NULL,
  stock_count INT NOT NULL,
  user_id INT NOT NULL,
  active TINYINT NOT NULL
);

create table if not exists users(
	username varchar_ignorecase(50) not null primary key,
	password varchar_ignorecase(50) not null,
	enabled boolean not null
);

create table if not exists authorities (
	username varchar_ignorecase(50) not null,
	authority varchar_ignorecase(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index if not exists ix_auth_username on authorities (username,authority);

--# insert into stock_store (symbol,average_price,stock_count,user_id) values ('MSFT',100.00,10,1)
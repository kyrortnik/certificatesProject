drop table if exists certificates_tags;
drop table if exists tags;
drop table if exists certificates;



CREATE TABLE IF NOT EXISTS certificates (
  id bigint auto_increment primary key,
  name VARCHAR (255) UNIQUE NOT NULL,
  description VARCHAR (255),
  price NUMERIC (20,2),
  duration VARCHAR (255),
  create_date timestamp,
  last_update_date timestamp
);

CREATE TABLE IF NOT EXISTS tags (
  id bigint auto_increment PRIMARY KEY,
  name VARCHAR (50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS certificates_tags (
  id bigint auto_increment PRIMARY KEY,
  certificate_id INT NOT NULL,
  tag_id INT NOT NULL,
  CONSTRAINT fk_certificates FOREIGN KEY(certificate_id) REFERENCES certificates(id),
  CONSTRAINT fk_tags FOREIGN KEY(tag_id) REFERENCES tags(id)
);
DROP TABLE if EXISTS certificates_tags;
DROP TABLE if EXISTS tags;
DROP TABLE if EXISTS certificates;

CREATE TABLE IF NOT EXISTS certificates (
  id bigint auto_increment PRIMARY KEY,
  name VARCHAR (255) UNIQUE NOT NULL,
  description VARCHAR (255),
  price NUMERIC (20,2),
  duration VARCHAR (255),
  create_date TIMESTAMP WITHOUT TIME ZONE,
  last_update_date TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS tags (
  id bigint auto_increment PRIMARY KEY,
  name VARCHAR (50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS certificates_tags (
  certificate_id bigint NOT NULL,
  tag_id bigint NOT NULL,
  CONSTRAINT fk_certificates FOREIGN KEY(certificate_id) REFERENCES certificates(id),
  CONSTRAINT fk_tags FOREIGN KEY(tag_id) REFERENCES tags(id)
);


CREATE ALIAS IF NOT EXISTS create_new_tags FOR "com.epam.esm.utilities.H2.Function.createNewTags";

CREATE ALIAS IF NOT EXISTS get_tags_ids FOR "com.epam.esm.utilities.H2.Function.getTagIdsForNames";

CREATE ALIAS IF NOT EXISTS create_cert_tag_relation FOR "com.epam.esm.utilities.H2.Function.createCertificateTagRelation";

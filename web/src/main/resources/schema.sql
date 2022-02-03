DROP TABLE if EXISTS certificates_tags;
DROP TABLE if EXISTS tags;
DROP TABLE if EXISTS certificates;

CREATE TABLE IF NOT EXISTS certificates (
  id bigint auto_increment PRIMARY KEY,
  name VARCHAR (255) UNIQUE NOT NULL,
  description VARCHAR (255),
  price NUMERIC (20,2),
  duration VARCHAR (255),
  create_date TIMESTAMP,
  last_update_date TIMESTAMP
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


--CREATE ALIAS query FOR "com.epam.esm.utilities.H2.Function.query";

CREATE ALIAS IF NOT EXISTS create_new_tags FOR "com.epam.esm.utilities.H2.Function.createNewTags";

CREATE ALIAS IF NOT EXISTS get_tags_ids FOR "com.epam.esm.utilities.H2.Function.getTagIdsForNames";

CREATE ALIAS IF NOT EXISTS create_cert_tag_relation FOR "com.epam.esm.utilities.H2.Function.createCertificateTagRelation";

--CREATE OR REPLACE FUNCTION create_new_tags(tagNames varchar[]) RETURNS void AS '
--DECLARE
-- tagName varchar;
-- nameCount integer;
-- BEGIN
-- FOREACH tagName IN ARRAY $1
--    LOOP
--    SELECT COUNT(name) INTO nameCount FROM tags WHERE name = tagName;
--        IF nameCount <= 0
--        THEN INSERT INTO tags VALUES (DEFAULT,tagName);
-- 	    END IF;
--    END LOOP;
-- END;'
-- LANGUAGE plpgsql;
--
--
--CREATE OR REPLACE FUNCTION create_cert_tag_relation(tag_ids integer[], cert_id integer)  RETURNS void AS '
--DECLARE
--tagName integer;
--BEGIN
--FOREACH tagName IN ARRAY $1
--    LOOP
--    INSERT INTO certificates_tags VALUES ($2,tagName);
--	END LOOP;
--END;
--'
--LANGUAGE plpgsql;
--
--
--CREATE OR REPLACE FUNCTION get_tags_ids(tag_names varchar[]) RETURNS TABLE (ids int) AS '
--DECLARE
--tagName varchar;
--BEGIN
--FOREACH tagName IN ARRAY $1
--    LOOP
--    SELECT tags.id INTO ids FROM tags WHERE name = tagName;
--    RETURN next;
--    END LOOP;
--END;
--'
--LANGUAGE plpgsql;
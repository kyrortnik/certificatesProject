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


CREATE OR REPLACE FUNCTION
createNewTags(tagNames varchar[]) RETURNS void AS
$$
DECLARE
tagName varchar;
nameCount integer;
        BEGIN
		FOREACH tagName IN ARRAY $1
		LOOP
  		SELECT COUNT(name) INTO nameCount FROM tags WHERE name = tagName;
		IF nameCount <= 0
		 THEN INSERT INTO tags VALUES (DEFAULT,tagName);
			END IF;
			END LOOP;
        END;
$$
 LANGUAGE plpgsql;


 CREATE OR REPLACE FUNCTION
 createCertTagRelation(tagIds integer[], certId integer) RETURNS void AS
 $$
 DECLARE
 tagName integer;
         BEGIN
 		FOREACH tagName IN ARRAY $1
 		LOOP
   		INSERT INTO certificates_tags VALUES ($2,tagName);
 			END LOOP;
         END;
 $$
  LANGUAGE plpgsql;


--  CREATE OR REPLACE FUNCTION
--  getTagsIds(tagNames varchar[])  RETURNS TABLE (id int) AS
--  $$
--  DECLARE
--  tagName varchar;
--          BEGIN
--  		FOREACH tagName IN ARRAY $1
--  		LOOP
--    		RETURN QUERY SELECT tags.id FROM tags WHERE name = tagName;
--  			END LOOP;
--          END;
--  $$
--   LANGUAGE plpgsql;
--

      CREATE OR REPLACE FUNCTION
     getTagsIds(tagNames varchar[])  RETURNS TABLE (ids int) AS
     $$
     DECLARE
       tagName varchar;
             BEGIN
     		FOREACH tagName IN ARRAY $1
     		LOOP
       	  SELECT tags.id INTO ids FROM tags WHERE name = tagName;
   		  RETURN next;
     			END LOOP;
             END;
     $$
      LANGUAGE plpgsql;



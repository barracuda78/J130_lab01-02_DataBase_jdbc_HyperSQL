QUERY00=CREATE TABLE Authors (auth_id INTEGER PRIMARY KEY, auth_name VARCHAR(64) NOT NULL, auth_note VARCHAR(255));
QUERY01=INSERT INTO Authors (auth_id, auth_name) VALUES (1, 'Arnold Grey');
QUERY02=INSERT INTO Authors (auth_id, auth_name, auth_note) VALUES (2, 'Tom Hawkins', 'new author');
QUERY03=INSERT INTO Authors (auth_id, auth_name) VALUES (3, 'Jim Beam');
QUERY04=INSERT INTO Authors (auth_id, auth_name) VALUES (4, 'Test Author');
QUERY0TEST=SELECT * FROM Authors;
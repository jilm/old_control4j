CREATE TABLE IF NOT EXISTS signal
(
  id CHAR(10) NOT NULL,               # the name of the signal is used as id
  tstamp DATETIME NOT NULL,           # signal timestamp
  validity TINYINT UNSIGNED NOT NULL, # validity 0: valid, > 0: invalid 
  value DOUBLE NOT NULL,              # value
  unit CHAR(5)                        # signal unit
);

# INSERT INTO signal (id, tstamp, validity, value) VALUES ("temp", '2013-04-26 20:05:31', 0, 2.3456e-2);

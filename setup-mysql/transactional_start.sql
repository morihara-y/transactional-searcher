create database transactional;

use transactional;

CREATE TABLE related_dao_code (
  transactional_method_id nchar(36) NOT NULL,
  seq int,
  package_name varchar(100) NOT NULL,
  class_name varchar(50) NOT NULL,
  method_name varchar(50) NOT NULL,
  method_param varchar(200) NOT NULL,
  method_type varchar(200) NOT NULL,
  update_method_cnt int,
  PRIMARY KEY(transactional_method_id,seq)
);

CREATE TABLE transactional_method (
  transactional_method_id nchar(36) NOT NULL PRIMARY KEY,
  package_name varchar(100) NOT NULL,
  class_name varchar(50) NOT NULL,
  method_name varchar(50) NOT NULL,
  method_param varchar(200) NOT NULL,
  method_type varchar(200) NOT NULL,
  develop_status varchar(50) NOT NULL,
  error_message varchar(200)
);

show tables from transactional;

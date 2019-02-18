#!/bin/sh

mysql -h 127.0.0.1 --port 43306 -uroot -pmysql < transactional_start.sql

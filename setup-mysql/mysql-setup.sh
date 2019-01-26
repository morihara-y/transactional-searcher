#!/bin/sh

docker container run --rm --name mysql -e MYSQL_ROOT_PASSWORD=mysql -d -p 43306:3306 mysql

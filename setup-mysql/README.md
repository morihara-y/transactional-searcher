# How to setup mysql

1. Prepare docker environment
2. Run mysql-setup.sh
3. Run init-tables.sh

## 1. Prepare docker environment

You have to download docker.  
If you don't have that environment, please install it in your local.

## 2. Run mysql-setup.sh

You can start temporary mysql server by kicking below.

```sh
./mysql-setup.sh
```

If you want to stop that, please input this command.

```sh
docker stop mysql
```

Then, the container will be stopped. And inserted data are gone.

## 3. Run init-tables.sh

You have to make new table when you kicked `mysql-setup.sh`.  

```sh
./init-tables.sh
```

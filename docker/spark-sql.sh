#!/bin/sh
/usr/local/spark/bin/spark-sql\
 --silent\
 --conf spark.ui.port=4041\
 --driver-class-path /usr/local/mysql/mysql-connector-java-5.1.34-bin.jar

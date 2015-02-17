#!/bin/bash

: ${HADOOP_PREFIX:=/usr/local/hadoop}

$HADOOP_PREFIX/etc/hadoop/hadoop-env.sh

rm /tmp/*.pid

# installing libraries if any - (resource urls added comma separated to the ACP system variable)
cd $HADOOP_PREFIX/share/hadoop/common ; for cp in ${ACP//,/ }; do  echo == $cp; curl -LO $cp ; done; cd -

IP=$(ip -o -4 addr list eth0 | gawk '{split($4,A,"/");print A[1]}')

sed "s/HOSTNAME/$IP/g" /usr/local/hadoop/etc/hadoop/core-template.xml > /usr/local/hadoop/etc/hadoop/core-site.xml

sed "s/HOSTNAME/$IP/g" /usr/local/hadoop/etc/hadoop/yarn-template.xml > /usr/local/hadoop/etc/hadoop/yarn-site.xml

sed "s/MYSQL_PORT_3306_TCP_ADDR/$MYSQL_PORT_3306_TCP_ADDR/g" /usr/local/spark/conf/hive-template.xml > /usr/local/spark/conf/hive-site.xml

service sshd start

$HADOOP_PREFIX/sbin/start-dfs.sh
$HADOOP_PREFIX/bin/hdfs dfsadmin -safemode wait
$HADOOP_PREFIX/sbin/start-yarn.sh

$SPARK_HOME/sbin/start-thriftserver.sh\
 --master yarn\
 --driver-class-path /usr/local/mysql/mysql-connector-java-5.1.34-bin.jar

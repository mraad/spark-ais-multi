#!/bin/bash

: ${HADOOP_PREFIX:=/usr/local/hadoop}

export SPARK_HOME=/usr/local/spark
$SPARK_HOME/sbin/stop-thriftserver.sh

$HADOOP_PREFIX/etc/hadoop/hadoop-env.sh
$HADOOP_PREFIX/sbin/stop-yarn.sh
$HADOOP_PREFIX/sbin/stop-dfs.sh

service sshd stop

#!/bin/sh
docker run --rm\
 --link spark:hdfs\
 -v $(pwd)/data:/data\
 -v $(pwd)/target:/opt/target\
 dockerfile/java:oracle-java7\
 java -server -Xms1g -Xmx4g -XX:+UseCompressedOops -jar /opt/target/spark-ais-multi-1.0.jar

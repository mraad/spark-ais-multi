#!/bin/sh
IP=$(ip -o -4 addr list eth0 | gawk '{split($4,A,"/");print A[1]}')
cat << EOF > /tmp/tmp.hql
drop table if exists ais;
create external table if not exists ais (
mmsi string,
lon double,
lat double,
heading int,
hour int,
zulu string
) partitioned by (year int, month int, day int)
row format delimited
fields terminated by ','
lines terminated by '\n'
stored as textfile;
EOF
cat << EOF > /tmp/tmp.awk
{
  N=split(\$8,NN,"/")
  YY=NN[3]
  MM=NN[4]
  DD=NN[5]
  if(N == 5) print "alter table ais add if not exists partition (year=" YY ",month=" MM ",day=" DD ") location 'hdfs://" IP ":9000" \$8 "';"
}
EOF
hadoop fs -ls -R /ais | awk -v IP=$IP -f /tmp/tmp.awk >> /tmp/tmp.hql
/usr/local/spark/bin/spark-sql\
 --silent\
 --conf spark.ui.port=4041\
 --driver-class-path /usr/local/mysql/mysql-connector-java-5.1.34-bin.jar\
 -f /tmp/tmp.hql

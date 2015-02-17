FROM sequenceiq/hadoop-docker

MAINTAINER @mraad <mraad@esri.com>

ENV PATH $PATH:$HADOOP_PREFIX/bin

RUN cd $HADOOP_PREFIX && chown -R root:root .

RUN echo -e "\n* soft nofile 65536\n* hard nofile 65536" >> /etc/security/limits.conf

RUN curl -s http://d3kbcqa49mib13.cloudfront.net/spark-1.2.1-bin-hadoop2.4.tgz | tar -xz -C /usr/local/
RUN cd /usr/local && ln -s spark-1.2.1-bin-hadoop2.4 spark && chown -R root:root spark-1.2.1-bin-hadoop2.4 spark

ENV SPARK_HOME /usr/local/spark
ENV YARN_CONF_DIR $HADOOP_PREFIX/etc/hadoop

RUN curl -s http://cdn.mysql.com/Downloads/Connector-J/mysql-connector-java-5.1.34.tar.gz | tar -xz -C /usr/local
RUN cd /usr/local && ln -s mysql-connector-java-5.1.34 mysql && chown -R root:root mysql-connector-java-5.1.34 mysql

ADD log4j.properties /usr/local/hadoop/etc/hadoop/
ADD log4j.properties /usr/local/spark/conf/

ADD core-template.xml /usr/local/hadoop/etc/hadoop/
ADD yarn-template.xml /usr/local/hadoop/etc/hadoop/
ADD hive-template.xml /usr/local/spark/conf/

ADD create-table.sh /etc/
ADD spark-sql.sh /etc/
ADD start-all.sh /etc/
ADD stop-all.sh /etc/

RUN chown root:root /etc/*.sh && chmod +x /etc/*.sh

EXPOSE 4040 4041 7077 8080 9000 10000 50095

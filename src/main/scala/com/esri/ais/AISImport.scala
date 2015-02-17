package com.esri.ais

import org.apache.spark.SparkContext._
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.{Logging, SparkConf, SparkContext}

object AISImport extends Serializable with Logging {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
      .setAppName(this.getClass.getSimpleName)
      .setMaster("local[*]")
      .set("spark.serializer", classOf[KryoSerializer].getName)
      .set("spark.executor.memory", "2g")
      .registerKryoClasses(Array(
      classOf[YYMMDD],
      classOf[Target]
    ))

    val sc = new SparkContext(sparkConf)
    try {
      val hdfsAddr = sys.env("HDFS_PORT_9000_TCP_ADDR")
      sc.textFile("file:///data/ais.csv")
        .flatMap(mapper)
        .saveAsHadoopFile(s"hdfs://$hdfsAddr:9000/ais", classOf[YYMMDD], classOf[Target], classOf[MultipleTargetOutputFormat])
    }
    finally {
      sc.stop()
    }
  }

  val lonlatRE = "^\\((.+), (.+)\\)$".r

  def mapper(line: String) = {
    val tokens = line.split('\t')
    val head = tokens(4)
    val date = tokens(6)
    val mode = tokens(7)
    val mmsi = tokens(9)
    tokens(1) match {
      case lonlatRE(lon, lat) =>
        val dateTime = DateTimeParser.parseDateTime(date)
        val yymmdd = YYMMDD(dateTime.getYear, dateTime.getMonthOfYear, dateTime.getDayOfMonth)
        Some((yymmdd, Target(mmsi, lon.toDouble, lat.toDouble, dateTime.getMillis, head.toInt)))
      case _ => None
    }
  }

}
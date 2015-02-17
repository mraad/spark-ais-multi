package com.esri.ais

import org.apache.hadoop.fs.FSDataOutputStream
import org.apache.hadoop.mapred.{RecordWriter, Reporter}
import org.joda.time.format.DateTimeFormat

/**
  */
class TargetWriter(outputStream: FSDataOutputStream) extends RecordWriter[YYMMDD, Target] {

  private final val formatter = DateTimeFormat.forPattern("HH,yyyyMMddHHmmss")

  formatter.withZoneUTC()

  override def write(key: YYMMDD, target: Target) = {
    val date = formatter.print(target.millis)
    outputStream.writeBytes("%s,%.6f,%.6f,%d,%s%n".format(target.mmsi, target.x, target.y, target.heading, date))
  }

  override def close(reporter: Reporter) = {
    outputStream.close()
  }
}

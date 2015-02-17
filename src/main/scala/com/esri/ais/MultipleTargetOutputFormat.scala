package com.esri.ais

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.mapred.lib.MultipleOutputFormat
import org.apache.hadoop.mapred.{FileOutputFormat, JobConf}
import org.apache.hadoop.util.Progressable

/**
  */
class MultipleTargetOutputFormat extends MultipleOutputFormat[YYMMDD, Target] {
  override def getBaseRecordWriter(fileSystem: FileSystem, jobConf: JobConf, name: String, progressable: Progressable) = {
    val path = FileOutputFormat.getTaskOutputPath(jobConf, name)
    val fs = path.getFileSystem(jobConf)
    val outputStream = fs.create(path, progressable)
    new TargetWriter(outputStream)
  }

  override def generateFileNameForKeyValue(ymd: YYMMDD, target: Target, name: String) = {
    f"${ymd.yy}%4d/${ymd.mm}%02d/${ymd.dd}%02d/ais.csv"
  }
}

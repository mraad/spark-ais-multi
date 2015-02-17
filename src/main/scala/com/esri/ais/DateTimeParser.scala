package com.esri.ais

import org.joda.time.format.DateTimeFormat

/**
  */
object DateTimeParser extends Serializable {
  private final val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  formatter.withZoneUTC()

  def parseDateTime(orig: String) = {
    val text = orig.indexOf('.') match {
      case -1 => orig
      case l => orig.substring(0, l)
    }
    formatter.parseDateTime(text)
  }
}

package com.uu.two.utils

import java.sql.Date
import java.text.SimpleDateFormat

object DateTrans {
  def getStandTime(time:Long):String={
    val date = new Date(time)
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.format(date)
  }
}

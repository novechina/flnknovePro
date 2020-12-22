package com.uu.two

import java.util.Date

object App {
  def main(args: Array[String]): Unit = {

    val date1 = new Date()
    val time: Long = date1.getTime
    Thread.sleep(1000)
    val date = new Date()
    val time1 = date.getTime
    println(time1 - time)
  }

}

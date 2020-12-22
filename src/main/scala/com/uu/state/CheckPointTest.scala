package com.uu.state

import java.util.Date

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object CheckPointTest {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    see.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    import org.apache.flink.api.scala._
    val value = see.socketTextStream("152.136.136.15", 9999)
    //统计一个名字数据两次间隔
    value.map(x=>{
      val arrRecord = x.split(" ")
    })
  }

}

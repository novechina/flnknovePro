package com.uu.two

import java.sql.Date
import java.text.SimpleDateFormat

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
case class StationLog(sid:String,callOut:String,callIn:String,callType:String,callTime:Long,duration:Long)
object One {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.api.scala._
    val value = see.readTextFile("C:\\Users\\IBM\\Desktop\\xx.txt")

    val value1 = value.map(x => {
      val strings = x.split(" ")
      new StationLog(strings(0),strings(1),strings(2),strings(3),strings(4).trim.toLong,strings(5).trim.toLong)
    })

    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    value1.filter(_.callIn != "n").map(new CallMapFunction(format)).print()


    see.execute("one")
  }

  //自定义的函数类
  class CallMapFunction(format: SimpleDateFormat) extends MapFunction[StationLog, String] {
    override def map(t: StationLog): String = {
      var strartTime = t.callTime;
      var endTime = t.callTime + t.duration * 1000
      "主叫号码:" + t.callOut + ",被叫号码:" + t.callIn + ",呼叫起始时 间:" + format.format(new Date(strartTime)) + ",呼叫结束时间:" + format.format(new Date(endTime))


    }
  }

}

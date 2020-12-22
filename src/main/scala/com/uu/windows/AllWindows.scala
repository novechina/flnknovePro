package com.uu.windows

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

object AllWindows {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    val value: DataStream[String] = see.socketTextStream("152.136.136.15", 9999)
    tumbWindow(value)
    see.execute("windows")
  }
  //滚动窗口
  def tumbWindow(df:DataStream[String]): Unit ={
    import org.apache.flink.api.scala._
    df.flatMap(_.split(" "))
      .map((_,1))
      .keyBy(_._1)
      .timeWindow(Time.seconds(5))
      .sum(1)
      .print()
  }

  //滑动窗口
  //每隔3s记录最近5s的数据
//    .timeWindow(Time.seconds(5),Time.seconds(3))

  //三秒没有数据进来，触发窗口
//    .window(EventTimeSessionWindows.withGap(Time.seconds(3)))
}

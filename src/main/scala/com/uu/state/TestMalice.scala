package com.uu.state

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object TestMalice {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    see.setParallelism(1)
    import org.apache.flink.streaming.api.scala._
    see.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)



    see.execute()
  }

}

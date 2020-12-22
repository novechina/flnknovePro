package com.uu.one

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object WordCount {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.streaming.api.scala._

    val value = see.socketTextStream("152.136.136.15", 9999)

    val result = value.flatMap(_.split(" ")).map((_, 1))
      .keyBy(0).sum(1)
    result.print()
    see.execute("count")
  }

}

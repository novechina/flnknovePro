package flink

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object WordCount {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    see.setMaxParallelism(1)
    val value: DataStream[String] = see.socketTextStream("152.136.136.15", 9999)
    intimeCount(value)
    see.execute("flink-wordCount")
  }
  //连续单词计数
  def intimeCount(ds:DataStream[String]): Unit ={
    import org.apache.flink.api.scala._
    ds.flatMap(_.split(","))
      .map((_,1))
      .keyBy(0)
      .sum(1)
      .print()
  }

}

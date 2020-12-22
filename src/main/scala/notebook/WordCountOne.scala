package notebook

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows
import org.apache.flink.streaming.api.windowing.time.Time

object WordCountOne {
  def main(args: Array[String]): Unit = {
    //初始化环境
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    //读取socket数据
    val value = see.socketTextStream("152.136.136.15", 9999)
    //隐士转换导入
    import org.apache.flink.api.scala._
    //设置分区数
    see.setParallelism(1)
    //执行统计计算
    value.map(_.split(","))
      .map((_,1))
      .keyBy(_._1.toString)
      //3s内没有数据，进行聚合
      .window(EventTimeSessionWindows.withGap(Time.seconds(3)))
      .sum(1)
      .print()
    see.execute("worddf")
  }
}

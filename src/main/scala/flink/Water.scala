package flink

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
 * 每隔10s统计出现间隔最长的数据
 * 基站时间传入无序，最多延迟3s
 **/
case class StationLog(sid: String, callOut: String, callIn: String, callType: String, callTime: Long, duration: Long)

object Water {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    see.setParallelism(1)
    import org.apache.flink.streaming.api.scala._
    see.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val value: DataStream[String] = see.socketTextStream("152.136.136.15", 9999)
    //设定的无序时间戳提取器
    val extractor = new BoundedOutOfOrdernessTimestampExtractor[StationLog](Time.seconds(3)) {
      override def extractTimestamp(element: StationLog): Long = {
        //呼叫时间戳为判定标准
        element.callTime
      }
    }
    value.map(x => {
      val logInfo = x.split(",")
      //返回日志对象
      new StationLog(logInfo(0),
        logInfo(1),
        logInfo(2),
        logInfo(3),
        logInfo(4).trim.toLong,
        logInfo(5).trim.toLong)
    })
      .assignTimestampsAndWatermarks(extractor)
      .keyBy(_.sid)
      //窗口大小和滑动大小
      .timeWindow(Time.seconds(10), Time.seconds(5))
    see.execute("testWater")
  }
  //返回持续时间最长的函数
  class MaxTimeReduce extends ReduceFunction[StationLog] {
    override def reduce(value1: StationLog, value2: StationLog): StationLog = {
      if (value1.duration > value2.duration) value1 else value2
    }
  }
  //返回最大时间
  class ReturnMaxTime extends WindowFunction[StationLog, String, String, TimeWindow] {
    override def apply(key: String, window: TimeWindow, input: Iterable[StationLog], out: Collector[String]): Unit = {
      var sb = new StringBuilder
      sb.append("窗口范围是： ").append(window.getStart).append("----").append(window.getEnd)
      sb.append("\n")
      sb.append("通话日志:").append(input.iterator.next())
      out.collect(sb.toString())
    }
  }
}
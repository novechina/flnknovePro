package com.uu.two
import java.sql.Date
import java.text.SimpleDateFormat

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.util.Collector
object Two {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.api.scala._
    val source = see.socketTextStream("152.136.136.15", 9999)


  }

  class MoinerFunc() extends KeyedProcessFunction[String,StationLog,String]{
    //使用一个状态记录时间
    lazy val timeState :ValueState[Long] =getRuntimeContext.getState(new ValueStateDescriptor[Long]("time",classOf[Long]))
    override def processElement(i: StationLog, context: KeyedProcessFunction[String, StationLog, String]#Context, collector: Collector[String]): Unit = {
      //获取时间的值
      val time = timeState.value()
      if (i.callType.equals("ok")){
        //获取当前时间
        val currentTime = context.timerService().currentProcessingTime()
        val time = currentTime + 5000L
        //结束判断时间
        context.timerService().registerProcessingTimeTimer(time)
        //更新状态
        timeState.update(time)
      }
    }
    //时间到了触发函数
    override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, StationLog, String]#OnTimerContext, out: Collector[String]): Unit = {
      //获取当前key
      val key = ctx.getCurrentKey
      out.collect(key)
      timeState.clear()
    }
  }
}

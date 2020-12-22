package com.uu.state

import java.util.Date

import com.uu.two.Humman
import com.uu.two.utils.DateTrans
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.util.Collector

case class Record(id:Int,name:String,getTime:Date)
object StateTest {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.api.scala._
    val value = see.socketTextStream("152.136.136.15", 9999)
    //统计一个名字数据两次间隔
    value.map(x=>{
      val arrRecord = x.split(" ")
      new Record(arrRecord(0).trim.toInt,arrRecord(1),new Date())
    })
      .keyBy(_.name)
      .flatMap(new IntervalFunction)
      .print()

    see.execute("func")
  }
//计算两条数据的时间间隔
  //入参数类型，出参数类型
  class IntervalFunction extends RichFlatMapFunction[Record,(String,Long,String,String)]{
//定义值来保存前一条数据的状态
    private var preDate:ValueState[Record] = _
//预先执行,设置一个状态描述
  override def open(parameters: Configuration): Unit = {
    val pre_state = new ValueStateDescriptor[Record]("pre_state", classOf[Record])
    preDate = getRuntimeContext.getState(pre_state)

  }
  override def flatMap(value: Record, out: Collector[(String, Long,String,String)]): Unit = {
    val humanValue = preDate.value()
    //没有 之前的值，写入，有，更新
    if (humanValue == null){
      preDate.update(value)
    }else{
      //获取时间的差值，返回
      val timeSub = value.getTime.getTime - humanValue.getTime.getTime
      out.collect(value.name,timeSub,"前："+DateTrans.getStandTime(humanValue.getTime.getTime),"后："+DateTrans.getStandTime(value.getTime.getTime))
    }
  }
}

}

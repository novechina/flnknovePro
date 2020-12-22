package flink

import org.apache.flink.api.common.functions.{MapFunction, RichMapFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.plan.util.MapExplodeTableFunc

/**
 * 匹配恶意登录，三次失败拉黑
 * */
case class LogStation(id:Int,state:String,name:String)
object MaliceLogin {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.streaming.api.scala._
    val value = see.socketTextStream("152.136.136.15", 9999)
    //1,jack,ll
    value.map(x=>{
      val message = x.split(",")
      new LogStation(message(0).trim.toInt,message(1),message(2))
    }).map(x=>{
      (x.state,1)
    }).keyBy(0)
      .sum(1)
      .filter(_._2 > 3)
      .map(x=>{
        "账号："+x._1+"违规"
      })
      .print()

    see.execute(
      "jj"
    )
  }

}

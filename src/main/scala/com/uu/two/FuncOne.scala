package com.uu.two

import java.sql
import java.sql.DriverManager

import com.uu.two.utils.DateTrans
import org.apache.flink.api.common.functions.{MapFunction, RichMapFunction}
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.streaming.api.scala.{OutputTag, StreamExecutionEnvironment}
import org.apache.flink.util.Collector
case class Humman(id:Long,name:String,sex:String)
object FuncOne {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    //设置分片数
    see.setParallelism(1)
    import org.apache.flink.api.scala._
    val value = see.socketTextStream("152.136.136.15", 9999)
    //返回样例类对象
    val phoneMap = value.map(x => {
      val arrayPhone = x.split(" ")
      new Humman(arrayPhone(0).trim.toLong, arrayPhone(1), arrayPhone(2))
    })
    //侧输出流首先需要定义一个流的标签
    val notSuccessTag= new OutputTag[Humman]("not_success")
    val suTag = new OutputTag[Humman]("su")
    //完成分流
    val splitRiver = phoneMap
      .process(new CreateSideStream(notSuccessTag,suTag))
    val fuRiver = splitRiver.getSideOutput(notSuccessTag)
    val suTagInfo = splitRiver.getSideOutput(suTag)

      splitRiver.print("main")
      fuRiver.print("fu")
      suTagInfo.print("suTag")

//        .keyBy(_.id.toString)
//      .process(new MoniterFunc())
//      .process(new MoniterFunc())
//        .map(new MyRichFunc)
//        .map(new MyMapFunc)
//      .print()
    see.execute("FuncOne")
  }

  //自定义函数对输入内容进行增强
  class MyMapFunc extends MapFunction[Humman,String]{
    override def map(value: Humman): String = {
      "id:"+value.id+
      "名字:"+value.name+
      "性别:"+value.sex
    }
  }

  //定义mysql的连接富函数,拿到地址
  class MyRichFunc extends RichMapFunction[Humman,String]{
    //配置对象
    var conn:sql.Connection = _
    //预处理函数
    override def open(parameters: Configuration): Unit = {
      conn = DriverManager.getConnection("jdbc:mysql://localhost/sqltest","root","123")
      println("建立同数据库的连接")
    }

    override def map(value: Humman): String = {
      //返回结果定义
      var addr:String = ""
      //获取id值
      val id = value.id
      val statement = conn.prepareStatement("select address from student3 where id = " + id)
      val result = statement.executeQuery()
      if(result.next()){
        addr = result.getString(1)
      }
      statement.close()
        "id:"+value.id+
        "名字:"+value.name+
        "性别:"+value.sex+
        "地址:"+addr
    }

    override def close(): Unit = {
      conn.close()
    }
  }

  //底层处理器函数api
  //参数，key类型，输入类型，输出类型
  class MoniterFunc() extends KeyedProcessFunction[String,Humman,String]{
      //使用一个状态记录时间
      lazy val timeState = getRuntimeContext.getState(new ValueStateDescriptor[Long]("time", classOf[Long]))
    //处理流程
    override def processElement(value: Humman, ctx: KeyedProcessFunction[String, Humman, String]#Context, out: Collector[String]): Unit = {
      //获取具体的时间
      val current: Long = timeState.value()
      //若性别为男,定义触发器，5s触发
      if (value.sex == "man"){
        val processTime: Long = ctx.timerService().currentProcessingTime()
        //5s后触发
        val endTime = processTime + 5000L
        //注册触发器
        ctx.timerService().registerProcessingTimeTimer(endTime)
        timeState.update(endTime)
      }
      if(value.sex == "women"){
        ctx.timerService().deleteProcessingTimeTimer(current)
        timeState.clear()
      }
    }
    //触发函数
    override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, Humman, String]#OnTimerContext, out: Collector[String]): Unit = {
      val str = "触发时间:" + DateTrans.getStandTime(timestamp) + "key值" + ctx.getCurrentKey
      out.collect(str)
      timeState.clear()
    }
  }

  //测流输出
  class CreateSideStream(tag: OutputTag[Humman],tag2:OutputTag[Humman]) extends ProcessFunction[Humman,Humman]{
    override def processElement(value: Humman, ctx: ProcessFunction[Humman, Humman]#Context, out: Collector[Humman]): Unit = {
      if (value.sex.equals("women")){
        out.collect(value)
      }else if (value.sex.equals("man")){
        ctx.output(tag,value)
      }else{
        ctx.output(tag2,value)
      }
    }
  }

}

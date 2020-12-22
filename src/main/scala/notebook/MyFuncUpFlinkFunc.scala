package notebook

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

//样例类实现数据的格式化
case class PersonInfo(id:Long,name:String,sex:String,birthday:Date)
//工具类，转化日期格式
object TransDateInfo{
  private val format = new SimpleDateFormat("yyyy 年 MM 月 dd日 HH:mm:ss")
  def dateToString(date: Date): String ={
    format.format(date)
  }
}
//功能增强函数，指定日期的输入方式
class upFunc extends MapFunction[PersonInfo,String]{
  override def map(value: PersonInfo): String = {
    "名字:"+value.name+"生日："+TransDateInfo.dateToString(value.birthday)
  }
}
object MyFuncUpFlinkFunc {
  def main(args: Array[String]): Unit = {
    //flink入口，接入socket数据源
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    //隐士转换
    import org.apache.flink.streaming.api.scala._
    val value = see.socketTextStream("152.136.136.15", 3333)
    //格式化数据结构
    val infoDs = value.map(line => {
      val info = line.split(",")
      new PersonInfo(info(0).trim.toLong, info(1), info(2), new Date(info(3).trim.toLong))
    })
    //调用函数返回预计的值
    infoDs.map(new upFunc).print()

    see.execute("upFunc")
  }

}

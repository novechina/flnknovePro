package com.uu.one

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object ReadFile {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
//    val text = see.readTextFile("C:\\Users\\IBM\\Desktop\\xx.txt")
    import org.apache.flink.api.scala._
    val value = see.fromElements(("a", 3), ("d", 4), ("c", 2), ("c", 5), ("a", 5))
    val collet = value.split(x => if (x._2 % 2 == 0) Seq("偶数") else Seq("奇数"))
    collet.select("偶数").print()

    see.execute("readFile")
  }

}

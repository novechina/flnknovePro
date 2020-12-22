package com.uu.state

import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object HdfsCheckPoint {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.api.scala._
    //检查点的间隔时间
    see.enableCheckpointing(5000)
    //设置备份文件路径
    see.setStateBackend(new FsStateBackend("file:///D:\\tmp\\logs"))
    //设置读取方式,保证读取一次
    see.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    //设置检查点设置时间,超过了按照超时处理，中断操作
    see.getCheckpointConfig.setCheckpointTimeout(5000)
    //检查点之间的最小时间间隔，防止密级触发checkpoint
    see.getCheckpointConfig.setMinPauseBetweenCheckpoints(600)
    //最大的同时执行checkpoint的个数
    see.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    //是否删除checipoint在程序取消后
    see.getCheckpointConfig.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    //可以继续执行的检查失败次数
    see.getCheckpointConfig.setTolerableCheckpointFailureNumber(1)

    see.setParallelism(1)

    val value = see.socketTextStream("152.136.136.15", 9999)
    value.map(x=>{
      val strings = x.split(" ")
      (strings(1),1)
    }).keyBy(0)
        .sum(1)
        .print()
    see.execute("job")
  }

}

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.{EnvironmentSettings, Types}
import org.apache.flink.table.api.java.StreamTableEnvironment
import org.apache.flink.table.sources.CsvTableSource

object FlinkApi {
  def main(args: Array[String]): Unit = {
    val see = StreamExecutionEnvironment.getExecutionEnvironment
    val settings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build()
//    val environment = StreamTableEnvironment.create(see, settings)
  }
}

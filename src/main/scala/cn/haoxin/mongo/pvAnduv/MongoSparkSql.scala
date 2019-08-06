package cn.haoxin.mongo.pvAnduv

import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.WriteConfig
import org.apache.spark.sql.{DataFrame, SparkSession}
import com.mongodb.spark.config._

/**
  * @author huolibin@haoxin.cn
  * @date Created by sheting on 2019/1/16 18:13
  *
  */
object MongoSparkSql {

  def main(args: Array[String]): Unit = {

    val session = SparkSession.builder()
      .master("local")
      .appName("MongoSparkSql")
      .config("spark.mongodb.input.uri", "mongodb://xiaoniu:123568@192.168.71.10:27017/logs.logs")
      .config("spark.mongodb.output.uri", "mongodb://xiaoniu:123568@192.168.71.10:27017/logs.reslut")
      .getOrCreate()

    val df: DataFrame = MongoSpark.load(session)
    df.createTempView("v_logs")

    val result: DataFrame = session.sql("SELECT count(*) pv,count(distinct openid) uv FROM v_logs")

    result.show()
   //保存到mongo
    MongoSpark.save(result)

//    val writeConfig = WriteConfig(Map("collection" -> "spark", "writeConcern.w" -> "majority"), Some(WriteConfig(session)))
//    val sparkDocuments = session.parallelize((1 to 10).map(i => Document.parse(s"{spark: $i}")))
//
//    MongoSpark.save(sparkDocuments, writeConfig)

    session.stop()

  }

}

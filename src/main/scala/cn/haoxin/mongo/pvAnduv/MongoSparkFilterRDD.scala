package cn.haoxin.mongo.pvAnduv

import com.mongodb.spark.MongoSpark
import com.mongodb.spark.rdd.MongoRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.bson.Document

/**
  * @author huolibin@haoxin.cn
  * @date Created by sheting on 2019/1/16 17:33
  * https://docs.mongodb.com/spark-connector/current/
  * https://docs.mongodb.com/spark-connector/current/scala-api/
  */
object MongoSparkFilterRDD {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("MongoSparkFilterRDD")
      .setMaster("local[*]")
      .set("spark.mongodb.input.uri", "mongodb://xiaoniu:123568@192.168.71.10:27017/logs.logs")
      .set("spark.mongodb.output.uri", "mongodb://xiaoniu:123568@192.168.71.10:27017/result.result")
    //创建sparkcontext
    val sc = new SparkContext(conf)

    val docsRDD: MongoRDD[Document] = MongoSpark.load(sc)

    val filterRDD: RDD[Document] = docsRDD.filter(doc => {
      val age = doc.get("age1")
      if (age == null) {
        false
      } else {
        val ageDouble = age.asInstanceOf[Double]
        ageDouble >= 20
      }
    })
    val cacheRDD = filterRDD.cache()

    val pv = cacheRDD.count()
    val uv = cacheRDD.map(doc => {
      doc.getString("openid")
    }).distinct().count()

    println("pv: " + pv + " uv: " + uv)

    sc.stop()

  }
}

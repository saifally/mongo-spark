package com.mongodb.spark_examples;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;


public final class WriteToMongoDBWriteConfig {

  public static void main(final String[] args) throws InterruptedException {

    SparkSession spark = SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      .config("spark.mongodb.input.uri", "mongodb://localhost/test.myCollection")
      .config("spark.mongodb.output.uri", "mongodb://localhost/test.myCollection")
      .getOrCreate();

    JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

    // Create a custom WriteConfig
    Map<String, String> writeOverrides = new HashMap<String, String>();
    writeOverrides.put("collection", "spark");
    writeOverrides.put("writeConcern.w", "majority");
    WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);

    // Create a RDD of 10 documents
    @SuppressWarnings("serial")
	JavaRDD<Document> sparkDocuments = jsc.parallelize(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).map
         (new Function<Integer, Document>() {
       public Document call(final Integer i) throws Exception {
               return Document.parse("{spark: " + i + "}");
             }
      });

    /*Start Example: Save data from RDD to MongoDB*****************/
    MongoSpark.save(sparkDocuments, writeConfig);
    /*End Example**************************************************/

    jsc.close();

  }

}
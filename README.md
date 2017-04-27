

# Spark Thrift Loader
[![Build Status](https://travis-ci.org/airbnb/airbnb-spark-thrift.svg)](https://travis-ci.org/airbnb/airbnb-spark-thrift)
A library for loadling Thrift data into [Spark SQL](http://spark.apache.org/docs/latest/sql-programming-guide.html).

## Features

It supports conversions from Thrift records to Spark SQL, making Thrift a first-class citizen in Spark.
It automatically derives Spark SQL schema from Thrift struct and convert Thrift object to Spark Row in runtime.
Any nested-structs are all support except Map key field needs to be primitive.

It is especially useful when running spark streaming job to consume thrift events from different streaming sources.


## Supported types for Thrift -> Spark SQL conversion

This library supports reading following types. It uses the following mapping from convert Thrift types to Spark SQL types:

| Thrift Type | Spark SQL type |
| --- | --- |
| bool | BooleanType |
| i16 | ShortType |
| i32 | IntegerType |
| i64 | LongType |
| double | DoubleType |
| binary | StringType |
| string | StringType |
| enum | String |
| list | ArrayType |
| set | ArrayType |
| map | MapType |
| struct | StructType |


## Examples


### Convert Thrift Schema to StructType in Spark

```scala
import com.airbnb.spark.thrift.ThriftSchemaConverter

// this will return a StructType for the thrift class
val thriftStructType = ThriftSchemaConverter.convert(ThriftExampleClass.getClass)


```

### Convert Thrift Object to Row in Spark

```scala
import com.airbnb.spark.thrift.ThriftSchemaConverter
import com.airbnb.spark.thrift.ThriftParser

// this will return a StructType for the thrift class
val thriftStructType = ThriftSchemaConverter.convert(ThriftExampleClass.getClass)
val row =  ThriftParser.convertObject(
                thriftObject,
                thriftStructType)
```

### Use cases: consume Kafka Streaming, where each event is a thrift object
```scala
import com.airbnb.spark.thrift.ThriftSchemaConverter
import com.airbnb.spark.thrift.ThriftParser


 directKafkaStream.foreachRDD(rdd => {
    val schema = ThriftSchemaConverter.convert(ThriftExampleClass.getClass)

     val deserializedEvents = rdd
       .map(_.message)
       .filter(_ != null)
       .flatMap(eventBytes => {
           try Some(MessageSerializer.getInstance().fromBytes(eventBytes))
             .asInstanceOf[Option[Message[_]]]
           catch {
               case e: Exception => {
                   LOG.warn(s"Failed to deserialize  thrift event ${e.toString}")
                   None
               }
           }
       }).map(_.getEvent.asInstanceOf[TBaseType])

       val rows: RDD[Row] = ThriftParser(
           ThriftExampleClass.getClass,
           deserializedEvents,
           schema)

       val df = sqlContext.createDataFrame(rows, schema)

       // Process the dataframe on this micrao batch
    })
 }
```

## How to get started
Clone the project and mvn package to get the artifact.


## How to contribute
Please send the PR here and cc @liyintang or @jingweilu1974 for reviewing

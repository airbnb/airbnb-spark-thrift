

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

| Thrift Type | Spark SQL type | Notes |
| --- | --- | --- |
| bool | BooleanType |
| i16 | ShortType |
| i32 | IntegerType |
| i64 | LongType |
| double | DoubleType |
| byte | ByteType |
| ByteBuffer | BinaryType | 
| string | StringType |
| enum | String | This is the String representation of an enum |
| list | ArrayType |
| set | ArrayType |
| map | MapType / ArrayType | A map with non-primitive keys is converted to an Array[Struct(key, val)] |
| struct / union| StructType | A recursively defined struct/union is converted to bytes |


## Examples


### Convert Thrift Schema to StructType in Spark
```scala
import com.airbnb.spark.thrift.Thrift2SparkConverters

// this will return a StructType for the thrift class
val thriftStructType = Thrift2SparkConverters.convertThriftClassToStructType(classOf[ThriftExampleClass])
```

### Convert Thrift Object to Row in Spark
```scala
import com.airbnb.spark.thrift.Thrift2SparkConverters

// this will return a StructType for the thrift class
val row = Thrift2SparkConverters.convertThriftToRow(thriftObject)
```

### Convert Spark Row to Thrift Object
```scala
import com.airbnb.spark.thrift.Spark2ThriftConverters

Spark2ThriftConverters.convertRowToThrift(classOf[ThriftExampleClass], sparkRowOfThriftExampleClass)
```

### Use cases: consume Kafka Streaming, where each event is a thrift object
```scala
import com.airbnb.spark.thrift.Thrift2SparkConverters


 directKafkaStream.foreachRDD(rdd => {
    val schema = Thrift2SparkConverters.convertThriftClassToStructType(classOf[ThriftExampleClass])

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

       val rows: RDD[Row] = Thrift2SparkConverters.convertThriftToRow(deserializedEvents)

       val df = sqlContext.createDataFrame(rows, schema)

       // Process the dataframe on this micrao batch
    })
 }
```

## How to get started
  1. Ensure you have `sbt` and `thrift` (thrift version 0.9.3) installed
  2. Clone the project
  3. run `sbt package` to get the artifact. If you wish to run tests, then run `sbt test`


## How to contribute
Please send the PR here and cc @liyintang or @jingweilu1974 for reviewing

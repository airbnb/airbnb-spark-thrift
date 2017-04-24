#  Data Source for Apache Spark

A library for loadling Thrift data from [Spark SQL](http://spark.apache.org/docs/latest/sql-programming-guide.html).

## Features

It supports conversions between Spark SQL and Thrift records, making Thrift a first-class citizen in Spark.
It derives Spark SQL schema from Thrift struct, and convert Thrift object to Spark Row in the runtime.
Any nested structs, List/Set of non-primitive structs, or Map of non-primitive struct to non-primitive struct are all supported.

It is especially useful when running spark streaming job to consume thrift events from different srtreaming soruces.


## Supported types for Thrift -> Spark SQL conversion

This library supports reading all Avro types. It uses the following mapping from Avro types to Spark SQL types:

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

package com.airbnb.spark.thrift

import com.google.common.base.Objects
import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.mapreduce.{Job, TaskAttemptContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.thrift.{TBase, TFieldIdEnum}

class ThriftRelation (
    val inputRDD: RDD[TBase[_ <: TBase[_, _], _ <: TFieldIdEnum]],
    val thriftClass: Class[TBase[_ <: TBase[_, _], _ <: TFieldIdEnum]],
    val maybeDataSchema: Option[StructType],
    override val paths: Array[String] = Array.empty[String])(@transient val sqlContext: SQLContext)
  extends HadoopFsRelation {

  /** Constraints to be imposed on schema to be stored. */
  private def checkConstraints(schema: StructType): Unit = {
    if (schema.fieldNames.length != schema.fieldNames.distinct.length) {
      val duplicateColumns = schema.fieldNames.groupBy(identity).collect {
        case (x, ys) if ys.length > 1 => "\"" + x + "\""
      }.mkString(", ")
      throw new IllegalArgumentException(s"Duplicate column(s) : $duplicateColumns found, " +
        s"cannot save to Thrift format")
    }
  }

  // buildScan() should return an [[RDD]] of [[InternalRow]]
  override val needConversion: Boolean = false

  override lazy val dataSchema = {
     ThriftSchemaConverter.convert(thriftClass)
  }

  override def buildScan(requiredColumns: Array[String],
                         filters: Array[Filter],
                         inputPaths: Array[FileStatus]): RDD[Row] = {
    ThriftParser(
      thriftClass,
      inputRDD,
      StructType(requiredColumns.map(dataSchema(_)))).asInstanceOf[RDD[Row]]
  }

  override def equals(other: Any): Boolean = other match {
    case that: ThriftRelation =>
      (inputRDD eq that.inputRDD) && paths.toSet == that.paths.toSet &&
        dataSchema == that.dataSchema &&
        schema == that.schema
    case _ => false
  }

  override def hashCode(): Int = {
    Objects.hashCode(
      inputRDD,
      paths.toSet,
      dataSchema,
      schema,
      partitionColumns)
  }

  override def prepareJobForWrite(job: Job): OutputWriterFactory = {
    new OutputWriterFactory {
      override def newInstance(
          path: String,
          dataSchema: StructType,
          context: TaskAttemptContext): OutputWriter = {
        throw new UnsupportedOperationException("Does not support writer yet !")
      }
    }
  }
}

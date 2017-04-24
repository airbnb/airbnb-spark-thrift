/**
  *
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
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

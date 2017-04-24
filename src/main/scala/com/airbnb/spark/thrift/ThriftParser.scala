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

import java.util

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String
import org.apache.thrift.meta_data.FieldMetaData
import org.apache.thrift.meta_data.FieldValueMetaData
import org.apache.thrift.meta_data.MapMetaData
import org.apache.thrift.meta_data._
import org.apache.thrift.{TBase, TFieldIdEnum}
import org.apache.thrift.protocol.TType
import org.apache.commons.logging.{Log, LogFactory}
import org.apache.log4j.{Level, Logger}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.{DataFrame, Row}


object ThriftParser {
  private val LOG: Log = LogFactory.getLog(this.getClass)
  type TBaseType = TBase[_ <: TBase[_, _], _ <: TFieldIdEnum]

  def apply(tBaseClass: Class[TBaseType] ,
            rdd: RDD[TBaseType],
            schema: StructType): RDD[Row] = {

    rdd.mapPartitions {
      iter => {
        iter.flatMap {
          tBase => {
            try {
              Some(convertObject(tBase, schema))
            }
            catch {
              case e: Exception => {
                LOG.info(s"Failed to parse jitney events ${e.toString}")
              }

              None
            }
          }
        }
      }
    }
  }

  def convertObject[T <: TBase[_, _], F <: TFieldIdEnum](
      tBaseInstance: TBase[T, F],
      schema: StructType): Row = {

    tBaseInstance match {
      case null => null
      case _ => {
        val fieldMeta: util.Map[_ <: TFieldIdEnum, FieldMetaData] = FieldMetaData.getStructMetaDataMap(tBaseInstance.getClass)

        val rowSeq = fieldMeta.map(kv => {
          val tFieldIdEnum = kv._1
          val metaData = kv._2.valueMetaData

          val fieldName = tFieldIdEnum.getFieldName
          val valueType = metaData.`type`
          val structType = schema(fieldName)
          val dataType = structType.dataType
          val index = schema.fieldIndex(fieldName)
          val fieldEnum = tBaseInstance.fieldForId(tFieldIdEnum.getThriftFieldId)
          val value = tBaseInstance.getFieldValue(fieldEnum)

          val obj = convertField(value, metaData, dataType)
          (index, obj)
        }).toSeq.sortBy(_._1).map(_._2)

        Row.fromSeq(rowSeq)
      }
    }
  }

  def convertField(fieldValue: Any, fieldMetaData: FieldValueMetaData, fieldSQLType: DataType): Any = {
    if (fieldValue == null) {
      return null
    }
    //println("== value : " + fieldValue + " valueType " + fieldMetaData.`type` + " dataType " + fieldSQLType)
    val fieldThriftType = fieldMetaData.`type`
    (fieldThriftType, fieldSQLType) match {
      case (TType.BOOL, BooleanType) =>
        fieldValue

      case (TType.DOUBLE, DoubleType) =>
        fieldValue
      case (TType.I16, ShortType | IntegerType | LongType) =>
        fieldValue
      case (TType.I32, IntegerType | LongType) =>
        fieldValue
      case (TType.I64, LongType) =>
        fieldValue

      case (TType.STRUCT, st: StructType) =>
        // It is surprising that TType.STRUCT doesn't have StructMetaData
        //val structMetaData = fieldMetaData.asInstanceOf[StructMetaData]
        //val c = structMetaData.getClass
        val structValue = fieldValue.asInstanceOf[TBaseType]
        convertObject(structValue, st)

      case (TType.MAP, MapType(kt, vt, _)) =>
        val mapMetaData = fieldMetaData.asInstanceOf[MapMetaData]
        val keyMetaData = mapMetaData.keyMetaData
        val valueMetaData = mapMetaData.valueMetaData
        val mapFieldValue = fieldValue.asInstanceOf[util.Map[Any, Any]]

        convertMap(mapFieldValue, keyMetaData, valueMetaData, kt, vt)

      case (TType.LIST,  ArrayType(st, _)) =>
        val listMetaData = fieldMetaData.asInstanceOf[ListMetaData]
        val eleMetaData = listMetaData.elemMetaData
        val list = fieldValue.asInstanceOf[util.List[Any]]

        convertArray(list, eleMetaData, st)

      case (TType.SET,  ArrayType(st, _)) =>
        val setMetaData = fieldMetaData.asInstanceOf[SetMetaData]
        val eleMetaData = setMetaData.elemMetaData
        val set = fieldValue.asInstanceOf[util.Set[Any]]
        convertArray(set, eleMetaData, st)

      case (_, StringType) =>
        if(fieldValue.isInstanceOf[Array[Byte]]) {
          UTF8String.fromBytes(fieldValue.asInstanceOf[Array[Byte]]).toString
        } else {
          fieldValue.toString
        }

      case (TType.BYTE | TType.STOP | TType.VOID, _) =>
        throw new UnsupportedOperationException("Does not support stop and void type !")
    }
  }

  /**
    * Parse an object as a Map, preserving all fields
    */
  private def convertMap(mapData: util.Map[Any, Any],
                         keyMetaData: FieldValueMetaData,
                         valueMetaData: FieldValueMetaData,
                         keyDataType: DataType,
                         valueDataType: DataType) = {

    mapData.map{ case (key, value) => {
      (convertField(key, keyMetaData, keyDataType), convertField(mapData.get(key), valueMetaData, valueDataType))
    }}
  }

  private def convertArray(collection: util.Collection[Any],
                           eleMetaData: FieldValueMetaData,
                           eleDataType: DataType) = {
    collection.map{ it => {
      convertField(it, eleMetaData, eleDataType)
    }}.toSeq
  }
}

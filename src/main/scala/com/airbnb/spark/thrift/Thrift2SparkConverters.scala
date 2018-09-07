package com.airbnb.spark.thrift

import java.nio.ByteBuffer

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructField, _}
import org.apache.thrift.meta_data.{FieldMetaData, FieldValueMetaData, ListMetaData, MapMetaData, _}
import org.apache.thrift.protocol.{TBinaryProtocol, TType}
import org.apache.thrift.{TBase, TFieldIdEnum, TFieldRequirementType, TSerializer}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

/**
  * Contains methods to allow converting a Java Thrift Objects to/from Spark's [[Row]] Objects along with [[StructType]]
  * Code inspired from https://github.com/airbnb/airbnb-spark-thrift
  */
// scalastyle:off cyclomatic.complexity
object Thrift2SparkConverters  {
  private val keyName = "key"
  private val valName = "value"
  private val thriftSerializer = new TSerializer(new TBinaryProtocol.Factory())

  private def isPrimitive(meta: FieldValueMetaData): Boolean = !(meta.isContainer || meta.isStruct)

  private def convertJavaElmSeqToRowElmSeq(seq: Seq[Any], innerElmMeta: FieldValueMetaData): Seq[Any] =
    seq.map(Option(_)).map(_.map(convertJavaElmToRowElm(_, innerElmMeta)).orNull)

  /**
    * Converts a [[TBaseType]] to a Spark SQL [[Row]]
    */

  def convertThriftToRow[F <: TFieldIdEnum : ClassTag](instance: TBase[_ <: TBase[_, _], F]): Row = {
    val fieldMeta = FieldMetaData.getStructMetaDataMap(instance.getClass).asScala

    val elms: Seq[Any] = fieldMeta.map({ case (tFieldIdEnum: TFieldIdEnum, metaData: FieldMetaData) => {
      val field: F = instance.fieldForId(tFieldIdEnum.getThriftFieldId.toInt)
      if (instance.isSet(field)) convertJavaElmToRowElm(instance.getFieldValue(field), metaData.valueMetaData)
      else null // scalastyle:ignore
    }}).toSeq

    Row.fromSeq(elms)
  }

  /**
    * Converts a class of [[TBaseType]] to a Spark SQL [[StructType]]
    */
  def convertThriftClassToStructType(tbaseClass: Class[_ <: TBaseType]): StructType = {
    val fieldMeta = FieldMetaData.getStructMetaDataMap(tbaseClass).asScala

    val fields: Seq[StructField] = fieldMeta.map({ case (tFieldIdEnum: TFieldIdEnum, metaData: FieldMetaData) =>
      StructField(
        tFieldIdEnum.getFieldName,
        convertThriftFieldToDataType(metaData.valueMetaData),
        nullable = (metaData.requirementType != TFieldRequirementType.REQUIRED)
      )
    }).toSeq

    StructType(fields)
  }

  /**
    * Converts a Java element to a [[Row]] element
    */
  private def convertJavaElmToRowElm(elm: Any, elmMeta: FieldValueMetaData): Any = {
    if (elmMeta.isBinary) elm match {
      case elmByteArray: Array[Byte] => elmByteArray
      case elmByteBuffer: ByteBuffer => elmByteBuffer.array()
    } else elmMeta.`type` match {
      // Recursive Cases
      case TType.LIST => {
        val seq = elm.asInstanceOf[java.util.List[Any]].asScala
        val innerElmMeta = elmMeta.asInstanceOf[ListMetaData].elemMetaData
        convertJavaElmSeqToRowElmSeq(seq, innerElmMeta)
      }
      case TType.SET => {
        val seq = elm.asInstanceOf[java.util.Set[Any]].asScala.toSeq
        val innerElmMeta = elmMeta.asInstanceOf[SetMetaData].elemMetaData
        convertJavaElmSeqToRowElmSeq(seq, innerElmMeta)
      }
      case TType.MAP => {
        val map = elm.asInstanceOf[java.util.Map[Any, Any]].asScala
        val mapMeta = elmMeta.asInstanceOf[MapMetaData]

        val keys: Seq[Any] = convertJavaElmSeqToRowElmSeq(map.keys.toSeq, mapMeta.keyMetaData)
        val vals: Seq[Any] = convertJavaElmSeqToRowElmSeq(map.values.toSeq, mapMeta.valueMetaData)
        val keyVals = keys.zip(vals)

        // If the key is not primitive, we convert the map to a list of key/value struct
        if (isPrimitive(mapMeta.keyMetaData)) Map(keyVals: _*) else keyVals.map(tuple => Row(tuple._1, tuple._2))

      }
      case TType.STRUCT => elmMeta match {
        case _: StructMetaData => convertThriftToRow(elm.asInstanceOf[TBaseType])
        // If we've recursed on a struct, thrift returns a TType.Struct with non StructMetaData. We serialize to bytes
        case _ => thriftSerializer.serialize(elm.asInstanceOf[TBaseType])
      }
      // Base Cases
      case TType.ENUM => elm.toString
      case TType.BOOL | TType.BYTE | TType.I16 | TType.I32 |TType.I64 | TType.DOUBLE | TType.STRING => elm
      case illegalType @ _ => throw new IllegalArgumentException(s"Illegal Thrift type: $illegalType")
    }
  }

  /**
    * Converts a Thrift field to a Spark SQL [[DataType]].
    */
  private def convertThriftFieldToDataType(meta: FieldValueMetaData): DataType = {
    if (meta.isBinary) BinaryType else meta.`type` match {
      // Recursive Cases
      case TType.LIST => {
        val listMetaData = meta.asInstanceOf[ListMetaData]
        ArrayType(convertThriftFieldToDataType(listMetaData.elemMetaData))
      }
      case TType.SET => {
        val setMetaData = meta.asInstanceOf[SetMetaData]
        ArrayType(convertThriftFieldToDataType(setMetaData.elemMetaData))
      }
      case TType.MAP => {
        val mapMetaData = meta.asInstanceOf[MapMetaData]
        val keyDataType = convertThriftFieldToDataType(mapMetaData.keyMetaData)
        val valueDataType = convertThriftFieldToDataType(mapMetaData.valueMetaData)
        // If the key is not primitive, we convert the map to a list of key/value struct
        if (isPrimitive(mapMetaData.keyMetaData)) MapType(keyDataType, valueDataType)
        else ArrayType(
          StructType(Seq(StructField(keyName, keyDataType), StructField(valName, valueDataType))),
          containsNull = false
        )
      }
      case TType.STRUCT => meta match {
        case structMetaData: StructMetaData => convertThriftClassToStructType(structMetaData.structClass)
        // If we've recursed on a struct, thrift does not return StructMetaData. We use StringType here for JSON
        case _ => BinaryType
      }
      // Base Cases
      case TType.BOOL => BooleanType
      case TType.BYTE => ByteType
      case TType.DOUBLE => DoubleType
      case TType.I16 => ShortType
      case TType.I32 => IntegerType
      case TType.I64 => LongType
      case TType.STRING | TType.ENUM => StringType
      case illegalType@_ => throw new IllegalArgumentException(s"Illegal Thrift type: $illegalType")
    }
  }

}
// scalastyle:on cyclomatic.complexity

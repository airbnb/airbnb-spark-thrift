package com.airbnb.spark.thrift

import java.nio.ByteBuffer

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.thrift.meta_data.{FieldMetaData, FieldValueMetaData, ListMetaData, MapMetaData, _}
import org.apache.thrift.protocol.{TBinaryProtocol, TType}
import org.apache.thrift.{TBase, TDeserializer, TFieldIdEnum}

import scala.collection.JavaConverters._

/**
  * Contains methods to allow converting a Java Thrift Objects to/from Spark's [[Row]] Objects along with [[StructType]]
  * Code inspired from https://github.com/airbnb/airbnb-spark-thrift
  */
// scalastyle:off cyclomatic.complexity
object Spark2ThriftConverters {
  private val thriftDeSerializer = new TDeserializer(new TBinaryProtocol.Factory())

  /**
    * Converts a Spark SQL [[Row]] to a [[TBaseType]]
    */
  def convertRowToThrift[T <: TBaseType](tbaseClass: Class[T], row: Row): T =
    convertRowToThriftGeneric(tbaseClass.asInstanceOf[TBaseClassWrapperWithFieldExt], row).asInstanceOf[T]

  /**
    * Converts a Spark SQL [[Row]] to a [[TBaseType]]
    */
  private def convertRowToThriftGeneric[F <: TFieldIdEnum](tbaseClass: Class[_ <: TBase[_ <: TBase[_, _], F]],
                                                           row: Row,
                                                           typeDefClses: Map[String, Class[_ <: TBaseType]] = Map.empty
                                                          ): TBaseType = {
    val fieldMeta = FieldMetaData.getStructMetaDataMap(tbaseClass).asScala

    val instance = tbaseClass.newInstance()
    fieldMeta.zipWithIndex.foreach({ case ((tFieldIdEnum: TFieldIdEnum, metaData: FieldMetaData), i: Int) =>
      if (!row.isNullAt(i)) {
        val field: F = instance.fieldForId(tFieldIdEnum.getThriftFieldId.toInt)
        val typeDefName = metaData.valueMetaData.getTypedefName
        val datum = convertRowElmToJavaElm(row(i), metaData.valueMetaData, typeDefClses + (typeDefName  -> tbaseClass))
        instance.setFieldValue(field, datum)
      }
    })
    instance
  }

  private def convertRowElmSeqToJavaElmSeq(seq: Seq[Any], innerElmMeta: FieldValueMetaData,
                                           typeDefClses: Map[String, Class[_ <: TBaseType]]): Seq[Any] =
    seq.map(Option(_)).map(_.map(convertRowElmToJavaElm(_, innerElmMeta, typeDefClses)).orNull)

  /**
    * Converts a [[Row]] element to a Java element
    */
  private def convertRowElmToJavaElm(elm: Any, meta: FieldValueMetaData,
                                     typeDefClses: Map[String, Class[_ <: TBaseType]]): Any = {
    if (meta.isBinary) ByteBuffer.wrap(elm.asInstanceOf[Array[Byte]]) else meta.`type` match {
      // Recursive Cases
      case TType.STRUCT => meta match {
        case structMetaData: StructMetaData => {
          val structSafeClass = structMetaData.structClass.asInstanceOf[TBaseClassWrapperWithFieldExt]
          convertRowToThriftGeneric(structSafeClass, elm.asInstanceOf[Row], typeDefClses)
        }
        // This case implies recursion
        case _ => {
          val recursiveInstance = typeDefClses(meta.getTypedefName).newInstance()
          thriftDeSerializer.deserialize(recursiveInstance, elm.asInstanceOf[Array[Byte]])
          recursiveInstance
        }
      }
      case TType.MAP => {
        val mapMeta = meta.asInstanceOf[MapMetaData]
        val keys = elm match {
          case map: Map[_, _] => map.keys
          case mapRows: Seq[_] => mapRows.map({ case Row(k: Any, _) => k})
        }
        val vals = elm match {
          case map: Map[_, _] => map.values
          case mapRows: Seq[_] => mapRows.map({ case Row(_, v: Any) => v})
        }
        val keyVals = convertRowElmSeqToJavaElmSeq(keys.toSeq, mapMeta.keyMetaData, typeDefClses)
          .zip(convertRowElmSeqToJavaElmSeq(vals.toSeq, mapMeta.valueMetaData, typeDefClses))
        Map(keyVals: _*).asJava
      }
      case TType.LIST => {
        val listMeta = meta.asInstanceOf[ListMetaData]
        convertRowElmSeqToJavaElmSeq(elm.asInstanceOf[Seq[Any]], listMeta.elemMetaData, typeDefClses).toList.asJava
      }
      case TType.SET => {
        val setMeta = meta.asInstanceOf[SetMetaData]
        convertRowElmSeqToJavaElmSeq(elm.asInstanceOf[Seq[Any]], setMeta.elemMetaData, typeDefClses).toSet.asJava
      }
      // Base Cases
      case TType.ENUM => {
        val enumMeta = meta.asInstanceOf[EnumMetaData]
        val enumSafeClass = enumMeta.enumClass.asInstanceOf[Class[E] forSome {type E <: Enum[E]}]
        Enum.valueOf(enumSafeClass, elm.asInstanceOf[String])
      }
      case TType.BOOL | TType.BYTE | TType.I16 | TType.I32 |TType.I64 | TType.DOUBLE | TType.STRING => elm
      case illegalType @ _ => throw new IllegalArgumentException(s"Illegal Thrift type: $illegalType")
    }
  }

}
// scalastyle:on cyclomatic.complexity

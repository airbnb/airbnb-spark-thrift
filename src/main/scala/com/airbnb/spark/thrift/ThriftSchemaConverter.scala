package com.airbnb.spark.thrift

import java.lang.reflect.ParameterizedType
import java.util

import org.apache.spark.sql.types._
import org.apache.thrift.meta_data.FieldMetaData
import org.apache.thrift.meta_data.FieldValueMetaData
import org.apache.thrift.meta_data.ListMetaData
import org.apache.thrift.meta_data.MapMetaData
import org.apache.thrift.meta_data._
import org.apache.thrift.protocol.TType
import org.apache.thrift.{TFieldRequirementType, TFieldIdEnum, TBase}

import scala.collection.JavaConversions._

object ThriftSchemaConverter  {

  /**
    * Converts Thrift class to a Spark SQL [[StructType]].
    */
  def convert(tbaseClass: Class[_ <: TBase[_ <: TBase[_, _], _ <: TFieldIdEnum]]): StructType = {
    val fieldMeta: util.Map[_ <: TFieldIdEnum, FieldMetaData] = FieldMetaData.getStructMetaDataMap(tbaseClass)

    val fields = fieldMeta.map(kv => {
      val tFieldIdEnum = kv._1
      val metaData = kv._2

      val fieldName = tFieldIdEnum.getFieldName
      val valueMetaData = metaData.valueMetaData
      val isRequired = (metaData.requirementType == TFieldRequirementType.REQUIRED)

      //println("== fieldName : " + fieldName + " valueType " + valueMetaData.`type`)
      val field = tbaseClass.getDeclaredField(fieldName)

      valueMetaData.`type` match {

        case TType.STRUCT |
             TType.BOOL   |
             TType.I16    |
             TType.I32    |
             TType.I64    |
             TType.BYTE   |
             TType.ENUM   |
             TType.STRING |
             TType.DOUBLE =>
          Some(
            StructField(
              fieldName,
              convertField(Seq(field.getType()),
                           valueMetaData),
              nullable = !isRequired))

        case TType.SET | TType.MAP | TType.LIST =>
          val genericType = field.getGenericType.asInstanceOf[ParameterizedType]
          //val genericTypeClass = genericType.getActualTypeArguments()

          val classes = genericType.
            getActualTypeArguments().
            toSeq.
            map(_.asInstanceOf[Class[_]])

          Some(
            StructField(
              fieldName,
              convertField(classes,
                           valueMetaData),
              nullable = !isRequired))
        case _ => None
      }
    }).toSeq
      .flatten

    StructType(fields)
  }


  /**
    * Converts a Thrift field to a Spark SQL [[DataType]].
    */
  def convertField(fieldClasses: Seq[Class[_]], metaData: FieldValueMetaData): DataType = {
    val ttype = metaData.`type`
    ttype match {
      case TType.BOOL => BooleanType
      case TType.BYTE => BinaryType
      case TType.DOUBLE => DoubleType
      case TType.I16 => ShortType
      case TType.I32 => IntegerType
      case TType.I64 => LongType
      case TType.STRING |
           TType.ENUM => StringType

      case TType.STRUCT =>
        val structClass = fieldClasses(0).asInstanceOf[Class[TBase[_ <: TBase[_, _], _ <: TFieldIdEnum]]]
        convert(structClass)

      case TType.LIST =>
        val listMetaData = metaData.asInstanceOf[ListMetaData]
        val eleMetaData = listMetaData.elemMetaData
        ArrayType(convertField(fieldClasses, eleMetaData), containsNull = false)

      case TType.SET =>
        val setMetaData = metaData.asInstanceOf[SetMetaData]
        val eleMetaData = setMetaData.elemMetaData
        ArrayType(convertField(fieldClasses, eleMetaData), containsNull = false)

      case TType.MAP =>
        val mapMetaData = metaData.asInstanceOf[MapMetaData]
        MapType(
          convertField(Seq(fieldClasses(0)), mapMetaData.keyMetaData),
          convertField(Seq(fieldClasses(1)), mapMetaData.valueMetaData),
          valueContainsNull = false)

      case _ => throw new IllegalArgumentException(s"Illegal Thrift type: $ttype")
    }
  }
}
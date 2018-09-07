package com.airbnb.spark.thrift

import com.airbnb.spark.thrift.generated.dummy._
import com.airbnb.spark.thrift.{Spark2ThriftConverters, Thrift2SparkConverters}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.StructType
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Prop.forAll
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

import scala.collection.JavaConverters._

class Thrift2Spark2ThriftConvertersSpec extends FunSuite with Checkers {
  implicit val arbBasicDummy: Arbitrary[BasicDummy] = Arbitrary(
    for {
      reqStr <- Arbitrary.arbitrary[String]
      str <- Arbitrary.arbitrary[String]
      i16 <- Arbitrary.arbitrary[Short]
      i32 <- Arbitrary.arbitrary[Int]
      i64 <- Arbitrary.arbitrary[Long]
      dbl <- Arbitrary.arbitrary[Double]
      bl <- Arbitrary.arbitrary[Boolean]
      bin <- Arbitrary.arbitrary[Array[Byte]]
    } yield new BasicDummy()
      .setReqStr(reqStr)
      .setStr(str)
      .setInt16(i16)
      .setInt32(i32)
      .setInt64(i64)
      .setDbl(dbl)
      .setBl(bl)
      .setBin(bin)
  )

  implicit val arbUnionDummy: Arbitrary[UnionDummy] = Arbitrary(Gen.lzy(Gen.oneOf(
    Arbitrary.arbitrary[Double].map(UnionDummy.dbl(_)),
    Arbitrary.arbitrary[String].map(UnionDummy.str(_))
  )))

  implicit val arbEnumDummy: Arbitrary[EnumDummy] = Arbitrary(
    for {
      enumVal <- Gen.choose[Int](0, EnumDummy.values().length - 1)
    } yield EnumDummy.findByValue(enumVal)
  )

  // Recursive Generator
  val baseCaseUnionRecursive: Gen[UnionRecursiveDummy] = Arbitrary.arbitrary[Boolean].map(UnionRecursiveDummy.bl(_))
  val unionRecursiveGen: Gen[UnionRecursiveDummy] = Gen.lzy(Gen.oneOf(baseCaseUnionRecursive, recurCaseUnionRecursive))
  val recurCaseUnionRecursive: Gen[UnionRecursiveDummy] = unionRecursiveGen.map(UnionRecursiveDummy.ur(_))
  implicit val arbUnionRecursive: Arbitrary[UnionRecursiveDummy] = Arbitrary(unionRecursiveGen)

  implicit val arbComplexDummy: Arbitrary[ComplexDummy] = Arbitrary(
    for {
      bdList <- Arbitrary.arbitrary[List[BasicDummy]]
      bdSet <- Arbitrary.arbitrary[Set[BasicDummy]]
      strToBdMap <- Arbitrary.arbitrary[Map[String, BasicDummy]]
      bdToStrMap <- Arbitrary.arbitrary[Map[BasicDummy, String]]
      enum <- Arbitrary.arbitrary[EnumDummy]
      union <- Arbitrary.arbitrary[UnionDummy]
      unionRecursive <- Arbitrary.arbitrary[UnionRecursiveDummy]
    } yield new ComplexDummy()
      .setBdList(bdList.asJava)
      .setBdSet(bdSet.asJava)
      .setStrToBdMap(strToBdMap.asJava)
      .setBdToStrMap(bdToStrMap.asJava)
      .setEnumDummy(enum)
      .setUnionDummy(union)
      .setUnionRecursiveDummy(unionRecursive)
  )

  test("Encode <> Decode Invariant") {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local")
      .appName("spark test")
      .getOrCreate()

    val prop = forAll(Gen.nonEmptyListOf(arbComplexDummy.arbitrary)) {
      inputList: List[ComplexDummy] => {
        val sparkSchema: StructType = Thrift2SparkConverters.convertThriftClassToStructType(classOf[ComplexDummy])
        val rowSeq: Seq[Row] = inputList.map(Thrift2SparkConverters.convertThriftToRow(_))
        val rowRDD: RDD[Row] = spark.sparkContext.parallelize(rowSeq)
        val df = spark.createDataFrame(rowRDD, sparkSchema)
        val dfRows = df.collect()
        val decodedInputList = dfRows
          .map(Spark2ThriftConverters.convertRowToThrift(classOf[ComplexDummy], _))
          .toList
        inputList == decodedInputList
      }
    }

    check(prop)
  }
}

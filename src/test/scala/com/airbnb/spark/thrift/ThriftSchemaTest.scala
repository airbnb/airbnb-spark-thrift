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

import java.lang.Long
import java.nio.ByteBuffer
import java.util

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class ThriftSchemaTest extends FunSuite {

  test("test convert thrift schema to spark sql schema") {
    val s1 = new StructSimple()

    val expectStruct = StructType(Seq(
      StructField("id16", ShortType, false),
      StructField("id32", IntegerType, false),
      StructField("id64", LongType, false),
      StructField("bin1", StringType, false), // For binary field, the TType is String
      StructField("b1", BooleanType, false),
      StructField("d1", DoubleType, false),
      StructField("str1", StringType, false),
      StructField("l1", ArrayType(LongType, false), false),
      StructField("m1", MapType(StringType, BooleanType, false), false),
      StructField("s1", ArrayType(DoubleType, false), false),
      StructField("f1", StructType(Seq(
        StructField("id16", ShortType, false),
        StructField("id32", IntegerType, false),
        StructField("id64", LongType, false))), false),
      StructField("fooList",
        ArrayType(
          StructType(Seq(
          StructField("id16", ShortType, false),
          StructField("id32", IntegerType, false),
          StructField("id64", LongType, false))),
          false),
        false),
      StructField("fooMap",
        MapType(
          StringType,
          StructType(Seq(
            StructField("id16", ShortType, false),
            StructField("id32", IntegerType, false),
            StructField("id64", LongType, false))),
          false
        ),
        false),
      StructField("option_str", StringType, true),
      StructField("e",StringType,false)
    ))

    val actualStruct = ThriftSchemaConverter.convert(s1.getClass)

    assertResult(expectStruct)(actualStruct)
  }

  test("test convert thrift instance to spark row") {
    val f1 = new Foo(1, 2, 3)
    val f2 = new Foo(2, 3, 4)
    val f3 = new Foo(3, 4, 5)

    val keys = Array(
      UTF8String.fromString("f1"),
      UTF8String.fromString("f2"),
      UTF8String.fromString("f3")).toSeq

    val m1: util.Map[java.lang.String, java.lang.Boolean] = new util.HashMap[java.lang.String, java.lang.Boolean]
    m1.put("f1", true)
    m1.put("f2", true)
    m1.put("f3", false)

    val s1: util.Set[java.lang.Double] = new util.HashSet[java.lang.Double]()
    s1.add(3.14)
    s1.add(3.14)

    val fooMap = new util.HashMap[String, Foo]()
    fooMap.put("f1", f1)
    fooMap.put("f2", f2)
    fooMap.put("f3", f3)

    val ss = new StructSimple()
    ss.setId16(16)
      .setId32(32)
      .setId64(64L)
      .setB1(true)
      .setD1(3.14)
      .setStr1("str1")
      .setBin1(ByteBuffer.wrap("test_binary".getBytes()))
      .setL1(java.util.Arrays.asList(Long.valueOf(1),Long.valueOf(2),Long.valueOf(3)))
      .setE(TestEnum.RETWEET)
      .setM1(m1)
      .setS1(s1)
      .setF1(f1)
      .setFooList(java.util.Arrays.asList(f1, f2, f3))
      .setFooMap(fooMap)

    val row = ThriftParser.convertObject(
      ss,
      ThriftSchemaConverter.convert(ss.getClass))
    implicit val Ord = implicitly[Ordering[String]]

    val rawRow = Row(
      16,
      32,
      64L,
      "test_binary", //"test_binary",
      true,
      3.14,
      "str1",
      Seq(1l, 2l, 3l),
      Map(
        "f3" -> false,
        "f2" -> true,
        "f1" -> true
      ),
      s1.toList,
      Row(f1.id16, f1.id32, f1.id64),
      Seq(
        Row(f1.id16, f1.id32, f1.id64),
        Row(f2.id16, f2.id32, f2.id64),
        Row(f3.id16, f3.id32, f3.id64)
      ),
      Map(
        "f3" -> Row(f3.id16, f3.id32, f3.id64),
        "f2" -> Row(f2.id16, f2.id32, f2.id64),
        "f1" -> Row(f1.id16, f1.id32, f1.id64)
      ),
      null,
      "RETWEET"
    )

    assertResult(row)(rawRow)
  }

}

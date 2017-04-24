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

import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter}

package object thrift {

  /**
    * Adds a method, `thrift`, to DataFrameWriter that allows you to write thrift files using
    * the DataFileWriter
    */
  implicit class ThriftDataFrameWriter(writer: DataFrameWriter) {
    def thrift: String => Unit = writer.format("com.airbnb.spark.thrift").save
  }

  /**
    * Adds a method, `thrift`, to DataFrameReader that allows you to read thrift files using
    * the DataFileReade
    */
  implicit class ThriftDataFrameReader(reader: DataFrameReader) {
    def thrift: String => DataFrame = reader.format("com.airbnb.spark.thrift").load
  }

}

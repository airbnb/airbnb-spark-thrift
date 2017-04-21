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

package com.airbnb.spark

import org.apache.thrift.{TBase, TFieldIdEnum}

package object thrift {
  // Basic Type of Java thrift objects
  type TBaseType = TBase[_ <: TBase[_, _], _ <: TFieldIdEnum]

  // Class Wrapper for Basic Type of Java thrift objects with Field existential
  type TBaseClassWrapperWithFieldExt = Class[_ <: TBase[_ <: TBase[_, _], F]] forSome {type F <: TFieldIdEnum}
}

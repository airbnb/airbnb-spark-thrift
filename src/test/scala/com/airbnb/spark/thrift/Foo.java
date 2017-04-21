/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.airbnb.spark.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2017-4-21")
public class Foo implements org.apache.thrift.TBase<Foo, Foo._Fields>, java.io.Serializable, Cloneable, Comparable<Foo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Foo");

  private static final org.apache.thrift.protocol.TField ID16_FIELD_DESC = new org.apache.thrift.protocol.TField("id16", org.apache.thrift.protocol.TType.I16, (short)1);
  private static final org.apache.thrift.protocol.TField ID32_FIELD_DESC = new org.apache.thrift.protocol.TField("id32", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField ID64_FIELD_DESC = new org.apache.thrift.protocol.TField("id64", org.apache.thrift.protocol.TType.I64, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new FooStandardSchemeFactory());
    schemes.put(TupleScheme.class, new FooTupleSchemeFactory());
  }

  public short id16; // required
  public int id32; // required
  public long id64; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID16((short)1, "id16"),
    ID32((short)2, "id32"),
    ID64((short)3, "id64");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID16
          return ID16;
        case 2: // ID32
          return ID32;
        case 3: // ID64
          return ID64;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ID16_ISSET_ID = 0;
  private static final int __ID32_ISSET_ID = 1;
  private static final int __ID64_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID16, new org.apache.thrift.meta_data.FieldMetaData("id16", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I16)));
    tmpMap.put(_Fields.ID32, new org.apache.thrift.meta_data.FieldMetaData("id32", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ID64, new org.apache.thrift.meta_data.FieldMetaData("id64", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Foo.class, metaDataMap);
  }

  public Foo() {
  }

  public Foo(
    short id16,
    int id32,
    long id64)
  {
    this();
    this.id16 = id16;
    setId16IsSet(true);
    this.id32 = id32;
    setId32IsSet(true);
    this.id64 = id64;
    setId64IsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Foo(Foo other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id16 = other.id16;
    this.id32 = other.id32;
    this.id64 = other.id64;
  }

  public Foo deepCopy() {
    return new Foo(this);
  }

  @Override
  public void clear() {
    setId16IsSet(false);
    this.id16 = 0;
    setId32IsSet(false);
    this.id32 = 0;
    setId64IsSet(false);
    this.id64 = 0;
  }

  public short getId16() {
    return this.id16;
  }

  public Foo setId16(short id16) {
    this.id16 = id16;
    setId16IsSet(true);
    return this;
  }

  public void unsetId16() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID16_ISSET_ID);
  }

  /** Returns true if field id16 is set (has been assigned a value) and false otherwise */
  public boolean isSetId16() {
    return EncodingUtils.testBit(__isset_bitfield, __ID16_ISSET_ID);
  }

  public void setId16IsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID16_ISSET_ID, value);
  }

  public int getId32() {
    return this.id32;
  }

  public Foo setId32(int id32) {
    this.id32 = id32;
    setId32IsSet(true);
    return this;
  }

  public void unsetId32() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID32_ISSET_ID);
  }

  /** Returns true if field id32 is set (has been assigned a value) and false otherwise */
  public boolean isSetId32() {
    return EncodingUtils.testBit(__isset_bitfield, __ID32_ISSET_ID);
  }

  public void setId32IsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID32_ISSET_ID, value);
  }

  public long getId64() {
    return this.id64;
  }

  public Foo setId64(long id64) {
    this.id64 = id64;
    setId64IsSet(true);
    return this;
  }

  public void unsetId64() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID64_ISSET_ID);
  }

  /** Returns true if field id64 is set (has been assigned a value) and false otherwise */
  public boolean isSetId64() {
    return EncodingUtils.testBit(__isset_bitfield, __ID64_ISSET_ID);
  }

  public void setId64IsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID64_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID16:
      if (value == null) {
        unsetId16();
      } else {
        setId16((Short)value);
      }
      break;

    case ID32:
      if (value == null) {
        unsetId32();
      } else {
        setId32((Integer)value);
      }
      break;

    case ID64:
      if (value == null) {
        unsetId64();
      } else {
        setId64((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID16:
      return Short.valueOf(getId16());

    case ID32:
      return Integer.valueOf(getId32());

    case ID64:
      return Long.valueOf(getId64());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID16:
      return isSetId16();
    case ID32:
      return isSetId32();
    case ID64:
      return isSetId64();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Foo)
      return this.equals((Foo)that);
    return false;
  }

  public boolean equals(Foo that) {
    if (that == null)
      return false;

    boolean this_present_id16 = true;
    boolean that_present_id16 = true;
    if (this_present_id16 || that_present_id16) {
      if (!(this_present_id16 && that_present_id16))
        return false;
      if (this.id16 != that.id16)
        return false;
    }

    boolean this_present_id32 = true;
    boolean that_present_id32 = true;
    if (this_present_id32 || that_present_id32) {
      if (!(this_present_id32 && that_present_id32))
        return false;
      if (this.id32 != that.id32)
        return false;
    }

    boolean this_present_id64 = true;
    boolean that_present_id64 = true;
    if (this_present_id64 || that_present_id64) {
      if (!(this_present_id64 && that_present_id64))
        return false;
      if (this.id64 != that.id64)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_id16 = true;
    list.add(present_id16);
    if (present_id16)
      list.add(id16);

    boolean present_id32 = true;
    list.add(present_id32);
    if (present_id32)
      list.add(id32);

    boolean present_id64 = true;
    list.add(present_id64);
    if (present_id64)
      list.add(id64);

    return list.hashCode();
  }

  @Override
  public int compareTo(Foo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetId16()).compareTo(other.isSetId16());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId16()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id16, other.id16);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetId32()).compareTo(other.isSetId32());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId32()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id32, other.id32);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetId64()).compareTo(other.isSetId64());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId64()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id64, other.id64);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Foo(");
    boolean first = true;

    sb.append("id16:");
    sb.append(this.id16);
    first = false;
    if (!first) sb.append(", ");
    sb.append("id32:");
    sb.append(this.id32);
    first = false;
    if (!first) sb.append(", ");
    sb.append("id64:");
    sb.append(this.id64);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'id16' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'id32' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'id64' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FooStandardSchemeFactory implements SchemeFactory {
    public FooStandardScheme getScheme() {
      return new FooStandardScheme();
    }
  }

  private static class FooStandardScheme extends StandardScheme<Foo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Foo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID16
            if (schemeField.type == org.apache.thrift.protocol.TType.I16) {
              struct.id16 = iprot.readI16();
              struct.setId16IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ID32
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.id32 = iprot.readI32();
              struct.setId32IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ID64
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.id64 = iprot.readI64();
              struct.setId64IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetId16()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'id16' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetId32()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'id32' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetId64()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'id64' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Foo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID16_FIELD_DESC);
      oprot.writeI16(struct.id16);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(ID32_FIELD_DESC);
      oprot.writeI32(struct.id32);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(ID64_FIELD_DESC);
      oprot.writeI64(struct.id64);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FooTupleSchemeFactory implements SchemeFactory {
    public FooTupleScheme getScheme() {
      return new FooTupleScheme();
    }
  }

  private static class FooTupleScheme extends TupleScheme<Foo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Foo struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI16(struct.id16);
      oprot.writeI32(struct.id32);
      oprot.writeI64(struct.id64);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Foo struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.id16 = iprot.readI16();
      struct.setId16IsSet(true);
      struct.id32 = iprot.readI32();
      struct.setId32IsSet(true);
      struct.id64 = iprot.readI64();
      struct.setId64IsSet(true);
    }
  }

}

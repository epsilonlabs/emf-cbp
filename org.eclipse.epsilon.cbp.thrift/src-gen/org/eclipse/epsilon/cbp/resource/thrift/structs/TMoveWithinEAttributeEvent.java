/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.eclipse.epsilon.cbp.resource.thrift.structs;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMoveWithinEAttributeEvent implements org.apache.thrift.TBase<TMoveWithinEAttributeEvent, TMoveWithinEAttributeEvent._Fields>, java.io.Serializable, Cloneable, Comparable<TMoveWithinEAttributeEvent> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TMoveWithinEAttributeEvent");

  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField TARGET_FIELD_DESC = new org.apache.thrift.protocol.TField("target", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField FROM_POSITION_FIELD_DESC = new org.apache.thrift.protocol.TField("fromPosition", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField TO_POSITION_FIELD_DESC = new org.apache.thrift.protocol.TField("toPosition", org.apache.thrift.protocol.TType.I32, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TMoveWithinEAttributeEventStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TMoveWithinEAttributeEventTupleSchemeFactory());
  }

  public String name; // required
  public long target; // required
  public int fromPosition; // required
  public int toPosition; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NAME((short)1, "name"),
    TARGET((short)2, "target"),
    FROM_POSITION((short)3, "fromPosition"),
    TO_POSITION((short)4, "toPosition");

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
        case 1: // NAME
          return NAME;
        case 2: // TARGET
          return TARGET;
        case 3: // FROM_POSITION
          return FROM_POSITION;
        case 4: // TO_POSITION
          return TO_POSITION;
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
  private static final int __TARGET_ISSET_ID = 0;
  private static final int __FROMPOSITION_ISSET_ID = 1;
  private static final int __TOPOSITION_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TARGET, new org.apache.thrift.meta_data.FieldMetaData("target", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.FROM_POSITION, new org.apache.thrift.meta_data.FieldMetaData("fromPosition", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TO_POSITION, new org.apache.thrift.meta_data.FieldMetaData("toPosition", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TMoveWithinEAttributeEvent.class, metaDataMap);
  }

  public TMoveWithinEAttributeEvent() {
  }

  public TMoveWithinEAttributeEvent(
    String name,
    long target,
    int fromPosition,
    int toPosition)
  {
    this();
    this.name = name;
    this.target = target;
    setTargetIsSet(true);
    this.fromPosition = fromPosition;
    setFromPositionIsSet(true);
    this.toPosition = toPosition;
    setToPositionIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TMoveWithinEAttributeEvent(TMoveWithinEAttributeEvent other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetName()) {
      this.name = other.name;
    }
    this.target = other.target;
    this.fromPosition = other.fromPosition;
    this.toPosition = other.toPosition;
  }

  public TMoveWithinEAttributeEvent deepCopy() {
    return new TMoveWithinEAttributeEvent(this);
  }

  @Override
  public void clear() {
    this.name = null;
    setTargetIsSet(false);
    this.target = 0;
    setFromPositionIsSet(false);
    this.fromPosition = 0;
    setToPositionIsSet(false);
    this.toPosition = 0;
  }

  public String getName() {
    return this.name;
  }

  public TMoveWithinEAttributeEvent setName(String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public long getTarget() {
    return this.target;
  }

  public TMoveWithinEAttributeEvent setTarget(long target) {
    this.target = target;
    setTargetIsSet(true);
    return this;
  }

  public void unsetTarget() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TARGET_ISSET_ID);
  }

  /** Returns true if field target is set (has been assigned a value) and false otherwise */
  public boolean isSetTarget() {
    return EncodingUtils.testBit(__isset_bitfield, __TARGET_ISSET_ID);
  }

  public void setTargetIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TARGET_ISSET_ID, value);
  }

  public int getFromPosition() {
    return this.fromPosition;
  }

  public TMoveWithinEAttributeEvent setFromPosition(int fromPosition) {
    this.fromPosition = fromPosition;
    setFromPositionIsSet(true);
    return this;
  }

  public void unsetFromPosition() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __FROMPOSITION_ISSET_ID);
  }

  /** Returns true if field fromPosition is set (has been assigned a value) and false otherwise */
  public boolean isSetFromPosition() {
    return EncodingUtils.testBit(__isset_bitfield, __FROMPOSITION_ISSET_ID);
  }

  public void setFromPositionIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __FROMPOSITION_ISSET_ID, value);
  }

  public int getToPosition() {
    return this.toPosition;
  }

  public TMoveWithinEAttributeEvent setToPosition(int toPosition) {
    this.toPosition = toPosition;
    setToPositionIsSet(true);
    return this;
  }

  public void unsetToPosition() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TOPOSITION_ISSET_ID);
  }

  /** Returns true if field toPosition is set (has been assigned a value) and false otherwise */
  public boolean isSetToPosition() {
    return EncodingUtils.testBit(__isset_bitfield, __TOPOSITION_ISSET_ID);
  }

  public void setToPositionIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TOPOSITION_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case TARGET:
      if (value == null) {
        unsetTarget();
      } else {
        setTarget((Long)value);
      }
      break;

    case FROM_POSITION:
      if (value == null) {
        unsetFromPosition();
      } else {
        setFromPosition((Integer)value);
      }
      break;

    case TO_POSITION:
      if (value == null) {
        unsetToPosition();
      } else {
        setToPosition((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NAME:
      return getName();

    case TARGET:
      return Long.valueOf(getTarget());

    case FROM_POSITION:
      return Integer.valueOf(getFromPosition());

    case TO_POSITION:
      return Integer.valueOf(getToPosition());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NAME:
      return isSetName();
    case TARGET:
      return isSetTarget();
    case FROM_POSITION:
      return isSetFromPosition();
    case TO_POSITION:
      return isSetToPosition();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TMoveWithinEAttributeEvent)
      return this.equals((TMoveWithinEAttributeEvent)that);
    return false;
  }

  public boolean equals(TMoveWithinEAttributeEvent that) {
    if (that == null)
      return false;

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_target = true;
    boolean that_present_target = true;
    if (this_present_target || that_present_target) {
      if (!(this_present_target && that_present_target))
        return false;
      if (this.target != that.target)
        return false;
    }

    boolean this_present_fromPosition = true;
    boolean that_present_fromPosition = true;
    if (this_present_fromPosition || that_present_fromPosition) {
      if (!(this_present_fromPosition && that_present_fromPosition))
        return false;
      if (this.fromPosition != that.fromPosition)
        return false;
    }

    boolean this_present_toPosition = true;
    boolean that_present_toPosition = true;
    if (this_present_toPosition || that_present_toPosition) {
      if (!(this_present_toPosition && that_present_toPosition))
        return false;
      if (this.toPosition != that.toPosition)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TMoveWithinEAttributeEvent other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetName()).compareTo(other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, other.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTarget()).compareTo(other.isSetTarget());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTarget()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.target, other.target);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFromPosition()).compareTo(other.isSetFromPosition());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFromPosition()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.fromPosition, other.fromPosition);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetToPosition()).compareTo(other.isSetToPosition());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetToPosition()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.toPosition, other.toPosition);
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
    StringBuilder sb = new StringBuilder("TMoveWithinEAttributeEvent(");
    boolean first = true;

    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("target:");
    sb.append(this.target);
    first = false;
    if (!first) sb.append(", ");
    sb.append("fromPosition:");
    sb.append(this.fromPosition);
    first = false;
    if (!first) sb.append(", ");
    sb.append("toPosition:");
    sb.append(this.toPosition);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (name == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'name' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'target' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'fromPosition' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'toPosition' because it's a primitive and you chose the non-beans generator.
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

  private static class TMoveWithinEAttributeEventStandardSchemeFactory implements SchemeFactory {
    public TMoveWithinEAttributeEventStandardScheme getScheme() {
      return new TMoveWithinEAttributeEventStandardScheme();
    }
  }

  private static class TMoveWithinEAttributeEventStandardScheme extends StandardScheme<TMoveWithinEAttributeEvent> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TMoveWithinEAttributeEvent struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TARGET
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.target = iprot.readI64();
              struct.setTargetIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // FROM_POSITION
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.fromPosition = iprot.readI32();
              struct.setFromPositionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TO_POSITION
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.toPosition = iprot.readI32();
              struct.setToPositionIsSet(true);
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
      if (!struct.isSetTarget()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'target' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetFromPosition()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'fromPosition' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetToPosition()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'toPosition' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TMoveWithinEAttributeEvent struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TARGET_FIELD_DESC);
      oprot.writeI64(struct.target);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(FROM_POSITION_FIELD_DESC);
      oprot.writeI32(struct.fromPosition);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(TO_POSITION_FIELD_DESC);
      oprot.writeI32(struct.toPosition);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TMoveWithinEAttributeEventTupleSchemeFactory implements SchemeFactory {
    public TMoveWithinEAttributeEventTupleScheme getScheme() {
      return new TMoveWithinEAttributeEventTupleScheme();
    }
  }

  private static class TMoveWithinEAttributeEventTupleScheme extends TupleScheme<TMoveWithinEAttributeEvent> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TMoveWithinEAttributeEvent struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.name);
      oprot.writeI64(struct.target);
      oprot.writeI32(struct.fromPosition);
      oprot.writeI32(struct.toPosition);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TMoveWithinEAttributeEvent struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.name = iprot.readString();
      struct.setNameIsSet(true);
      struct.target = iprot.readI64();
      struct.setTargetIsSet(true);
      struct.fromPosition = iprot.readI32();
      struct.setFromPositionIsSet(true);
      struct.toPosition = iprot.readI32();
      struct.setToPositionIsSet(true);
    }
  }

}


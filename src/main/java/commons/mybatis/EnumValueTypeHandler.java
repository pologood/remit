package commons.mybatis;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

//notice! only for string and int
public class EnumValueTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

  private Class<E> type;

  private Method method;

  private final E[] enums;

  private final Object[] values;

  public EnumValueTypeHandler(Class<E> type) {
    if (type == null) {
      throw new IllegalArgumentException("Type argument cannot be null");
    }
    this.type = type;
    this.enums = type.getEnumConstants();
    if (this.enums == null) {
      throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum value type.");
    }

    int i = 0;
    try {
      this.method = type.getMethod("getValue");
      this.values = String.class == method.getReturnType() ? new String[enums.length] : new Integer[enums.length];

      for (E e : this.enums) {
        this.values[i++] = this.method.invoke(e);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum value type.");
    }
  }

  public E getEnum(Object value) {
    for (int i = 0; i < values.length; ++i) {
      if (Objects.equals(values[i], value)) return enums[i];
    }
    return null;
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
    try {
      if (String.class == this.method.getReturnType()) ps.setString(i, (String) method.invoke(parameter));
      else ps.setInt(i, (int) this.method.invoke(parameter));
    } catch (Exception e) {
      throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum value type.");
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Object o = rs.getObject(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      try {
        return getEnum(o);
      } catch (Exception ex) {
        throw new IllegalArgumentException(
            String.format("Cannot convert %s to %s by ordinal value.", o, type.getSimpleName()), ex);
      }
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Object o = rs.getObject(columnIndex);
    if (rs.wasNull()) {
      return null;
    } else {
      try {
        return getEnum(o);
      } catch (Exception ex) {
        throw new IllegalArgumentException(
            String.format("Cannot convert %s to %s by ordinal value.", o, type.getSimpleName()), ex);
      }
    }
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Object o = cs.getObject(columnIndex);
    if (cs.wasNull()) {
      return null;
    } else {
      try {
        return getEnum(o);
      } catch (Exception ex) {
        throw new IllegalArgumentException(
            String.format("Cannot convert %s to %s by ordinal value.", o, type.getSimpleName()), ex);
      }
    }
  }

}

package commons.mybatis;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import commons.utils.EnumConverter;

public class EnumValueTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

  private final Method method;

  private final Map<String, E> enumMap = new HashMap<>();

  public EnumValueTypeHandler(Class<E> type) {
    if (Objects.isNull(type) || !type.isEnum()) throw new IllegalArgumentException(type + " is not a enum type");
    try {
      this.method = type.getMethod(EnumConverter.METHOD_NAME);
      for (E e : type.getEnumConstants())
        enumMap.put(String.valueOf(this.method.invoke(e)), e);
    } catch (Exception e) {
      throw new IllegalArgumentException(type.getSimpleName() + " does not have get value method");
    }
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
    try {
      ps.setString(i, String.valueOf(method.invoke(parameter)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Object o = rs.getObject(columnName);
    return rs.wasNull() ? null : enumMap.get(String.valueOf(o));
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Object o = rs.getObject(columnIndex);
    return rs.wasNull() ? null : enumMap.get(String.valueOf(o));
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Object o = cs.getObject(columnIndex);
    return cs.wasNull() ? null : enumMap.get(String.valueOf(o));
  }

}

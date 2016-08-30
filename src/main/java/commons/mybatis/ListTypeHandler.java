/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sogou.pay.remit.common.JsonHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月14日;
//-------------------------------------------------------
public class ListTypeHandler<T> extends BaseTypeHandler<List<T>> {

  public final TypeReference<List<T>> LIST_TYPE = new TypeReference<List<T>>() {};

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType)
      throws SQLException {
    try {
      ps.setString(i, JsonHelper.toJson(parameter));
    } catch (Exception e) {
      throw new IllegalArgumentException("ListTypeHandler only supports list not empty");
    }
  }

  @Override
  public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String s = rs.getString(columnName);
    if (rs.wasNull()) return null;
    else try {
      return JsonHelper.fromJson(s, LIST_TYPE);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new IllegalArgumentException(String.format("%s:%s is not a list type", columnName, s), ex);
    }
  }

  @Override
  public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String s = rs.getString(columnIndex);
    if (rs.wasNull()) return null;
    else try {
      return JsonHelper.fromJson(s, LIST_TYPE);
    } catch (Exception ex) {
      throw new IllegalArgumentException(String.format("%s is not a list type", s), ex);
    }
  }

  @Override
  public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String s = cs.getString(columnIndex);
    if (cs.wasNull()) return null;
    else try {
      return JsonHelper.fromJson(s, LIST_TYPE);
    } catch (Exception ex) {
      throw new IllegalArgumentException(String.format("%s is not a list type", s), ex);
    }
  }

}

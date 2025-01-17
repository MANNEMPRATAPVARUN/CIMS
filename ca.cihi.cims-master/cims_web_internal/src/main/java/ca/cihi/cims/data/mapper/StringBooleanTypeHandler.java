package ca.cihi.cims.data.mapper;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;


public class StringBooleanTypeHandler extends BaseTypeHandler<Boolean>  {

	public void setNonNullParameter(PreparedStatement ps, int i,Boolean parameter, JdbcType jdbcType) throws SQLException {
		  ps.setString(i, parameter ? "Y" : "N");

	}

	public Boolean getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		 return valueOf(rs.getString(columnName));
	}

	public Boolean getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return  valueOf(cs.getString(columnIndex));
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int position)
			throws SQLException {
		return valueOf(rs.getString(position));
	}

	public Boolean valueOf(String value) {
		if (value.equals("Y")) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}

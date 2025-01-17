package ca.cihi.cims.data.ibatis;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * @author szhang
 */
public class BooleanTypeHandlerCallback implements TypeHandlerCallback {

    private static final String TRUE = "1";
    private static final String FALSE = "0";

    /**
     * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com.ibatis.sqlmap.client.extensions.ResultGetter)
     */
    @Override
    public Object getResult(final ResultGetter getter)
    throws SQLException {
        String flag = getter.getString();
        return TRUE.equals(flag);
    }

    /**
     * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(com.ibatis.sqlmap.client.extensions.ParameterSetter,
     *      java.lang.Object)
     */
    @Override
    public void setParameter(final ParameterSetter setter, final Object parameter)
    throws SQLException {
        if (Boolean.TRUE.equals(parameter)) {
            setter.setString(TRUE);
        } else {
            setter.setString(FALSE);
        }
    }

    /**
     * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#valueOf(java.lang.String)
     */
    @Override
    public Object valueOf(final String stringValue) {
        return stringValue;
    }
}

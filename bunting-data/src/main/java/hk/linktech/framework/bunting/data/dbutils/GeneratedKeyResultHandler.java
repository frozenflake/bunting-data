package hk.linktech.framework.bunting.data.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

public class GeneratedKeyResultHandler implements ResultSetHandler<Object> {
	
	Class type;
	
	public GeneratedKeyResultHandler( Class type ) {
		this.type = type;
	}

	@Override
	public Object handle(ResultSet rs) throws SQLException {
		return rs == null || rs.next() == false ? null : rs.getObject(1);
	}

}

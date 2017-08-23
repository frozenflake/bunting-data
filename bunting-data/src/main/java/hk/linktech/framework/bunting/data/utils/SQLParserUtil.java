package hk.linktech.framework.bunting.data.utils;

import hk.linktech.framework.bunting.data.SQLType;

public class SQLParserUtil {
	public static SQLType parseType( String sql ) {
		assert( sql != null );
		String _sql = sql.trim().toLowerCase();
		
		if( _sql.indexOf("select") == 0 ) {
			return SQLType.SELECT;
		} else if(_sql.indexOf("update") == 0){
			return SQLType.INSERT;
		} else if(_sql.indexOf("insert") == 0){
			return SQLType.INSERT;
		} else if( _sql.indexOf("delete") == 0 ) {
			return SQLType.DELETE;
		} else {
			return SQLType.UNDIFINED;
		}
	}
}

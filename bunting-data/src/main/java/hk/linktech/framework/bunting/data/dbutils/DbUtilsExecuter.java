package hk.linktech.framework.bunting.data.dbutils;

import hk.linktech.framework.bunting.data.SQLExecuter;
import hk.linktech.framework.bunting.data.utils.SQLParserUtil;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DbUtilsExecuter implements SQLExecuter, ApplicationContextAware {
	
	ApplicationContext applicationContext;
	DataSource dataSource;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public <T> T execute( Method method, DataSource dataSource, String sql,
			Object[] args) throws Exception {
		if( null == dataSource ) {
			throw new Exception("DataSource is null.");
		}
		
		QueryRunner query = new QueryRunner(dataSource);
		Object result = null;
		switch( SQLParserUtil.parseType(sql) ) {
		case INSERT:
			Class<?> rt = method.getReturnType();
			//if( rt.equals(int.class) || rt.equals(Integer.class) || rt.equals(long.class) || rt.equals(Long.class) )
			if( rt.isPrimitive() )
			{
				result = query.insert( sql, new GeneratedKeyResultHandler(Object.class), args );
			} else {
				query.update(sql, args);
			}
			break;
		case SELECT:
			ResultSetHandler rsh = ResultSetHandlerFactory.getHandlerByMethod(method);
			result = query.query(sql, rsh, args);
			break;
		case UPDATE:
		case DELETE:
		case UNDIFINED:
			result = query.update(sql, args);
			break;
		default:
			break;
		}
		return (T) result;
	}
}

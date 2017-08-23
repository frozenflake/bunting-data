package hk.linktech.framework.bunting.data;

import java.lang.reflect.Method;

import javax.sql.DataSource;

public interface SQLExecuter {
	public <T> T execute( Method method, DataSource dataSource, String sql, Object[] args ) throws Exception;
}

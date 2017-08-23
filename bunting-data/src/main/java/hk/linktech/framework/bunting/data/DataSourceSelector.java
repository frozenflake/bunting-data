package hk.linktech.framework.bunting.data;

import java.lang.reflect.Method;

import javax.sql.DataSource;

public interface DataSourceSelector {
	public DataSource select( Class<?> mappedInterface, Method method, Object[] arguments );
}

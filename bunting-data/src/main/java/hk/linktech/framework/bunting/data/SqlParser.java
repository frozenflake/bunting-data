package hk.linktech.framework.bunting.data;

import java.lang.reflect.Method;

public interface SqlParser {
	public ExecuteContext parse( Class<?> interfaceClass, Method method, Object[] args ) throws Throwable;
}

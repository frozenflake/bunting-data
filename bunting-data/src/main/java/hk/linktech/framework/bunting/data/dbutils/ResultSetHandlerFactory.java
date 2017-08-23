package hk.linktech.framework.bunting.data.dbutils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.BeanMapHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public class ResultSetHandlerFactory {
	public static ResultSetHandler<?> getHandlerByMethod( Method method ) {
		Class<?> returnedClass = method.getReturnType();
		if( returnedClass.isPrimitive() || returnedClass.getName().startsWith("java.lang")) {
			return new ScalarHandler<Object>();
		} else if( returnedClass.isAssignableFrom( List.class ) ) {
			Type type = method.getGenericReturnType();
			if( type instanceof ParameterizedType ) {
				Class<?> genericClass = null;
				Type t = ((ParameterizedType)type).getActualTypeArguments()[0];
				if( t instanceof ParameterizedType ) {
					Class<?> a = (Class<?>)((ParameterizedType) t).getRawType();
					if( a.isAssignableFrom( Map.class )) {
						//TODO: 判断参数类型
						return new MapListHandler();
					}
				} else {
					genericClass = (Class<?>)t;
					if( genericClass.getName().startsWith("java.lang") ) {
						return new ColumnListHandler();
					} else if( genericClass.isArray() ) {
						return new ArrayListHandler();
					}
					
					return new BeanListHandler(genericClass);
				}
			}
		} else if( returnedClass.isAssignableFrom( Map.class ) ){
			Type type = method.getGenericReturnType();
			//TODO: Map<key,bean> 处理
			if( type instanceof ParameterizedType ) {
				Type kt = ((ParameterizedType)type).getActualTypeArguments()[0];
				Type vt = ((ParameterizedType)type).getActualTypeArguments()[1];
				
				Class<?> vc = (Class<?>)vt;
				if( !vc.getName().startsWith("java.lang") && !vc.isPrimitive() ) {
					return new BeanMapHandler(vc);
				}
			}
			
			return new MapHandler();
		} else if( returnedClass.isArray() ){
			return new ArrayHandler();
		} else {
			return new BeanHandler(returnedClass);
		}
		return null;
	}
	
	public List<Object[]> test() {
		return null;
	}
	
	public static void main( String[] args ) throws NoSuchMethodException, SecurityException {
		Type retClass = ResultSetHandlerFactory.class.getMethod("test", null).getGenericReturnType();
		System.out.println(((ParameterizedType)retClass).getActualTypeArguments()[0]);
		ResultSetHandler h = ResultSetHandlerFactory.getHandlerByMethod(ResultSetHandlerFactory.class.getMethod("test", null));
		//System.out.println(Integer.class.isPrimitive());
	}
}

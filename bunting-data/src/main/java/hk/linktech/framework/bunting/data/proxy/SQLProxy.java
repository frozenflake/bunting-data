package hk.linktech.framework.bunting.data.proxy;

import hk.linktech.framework.bunting.data.DataSourceSelector;
import hk.linktech.framework.bunting.data.ExceptionHandler;
import hk.linktech.framework.bunting.data.ExecuteContext;
import hk.linktech.framework.bunting.data.PropertyNameStatementParser;
import hk.linktech.framework.bunting.data.SQLExecuter;
import hk.linktech.framework.bunting.data.SqlParser;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;

public class SQLProxy implements InvocationHandler {
	
	private static final String DEFAULT_PROPERTIES_PATH = "/bunting-data.properties";
	
	Class<?> interfacedClass;
	
	private ApplicationContext applicationContext;
	private SQLExecuter sqlExecuter;
	private SqlParser sqlParser = new PropertyNameStatementParser();
	private DataSourceSelector dataSourceSelector;
	private ExceptionHandler exceptionHandler;
	
	private static final Properties defaultProperties;
	
	static {
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_PROPERTIES_PATH, SQLProxy.class);
			defaultProperties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not load 'bunting-data.properties': " + ex.getMessage());
		}
	}
	
	protected void initDefaults() throws Exception {
		Assert.notNull(defaultProperties);
		
		Class<? extends SQLExecuter> cls1 = (Class<? extends SQLExecuter>) Class.forName(defaultProperties.getProperty("SQLExecuter.class"));
		this.sqlExecuter = cls1.newInstance();
		
		Class<? extends SqlParser> cls2 = (Class<? extends SqlParser>) Class.forName(defaultProperties.getProperty("SqlParser.class"));
		this.sqlParser = cls2.newInstance();
		
		try {
			Class<? extends DataSourceSelector> cls3 = (Class<? extends DataSourceSelector>) Class.forName(defaultProperties.getProperty("DataSourceSelector.class"));
			this.dataSourceSelector = cls3.newInstance();
			
			Class<? extends ExceptionHandler> cls4 = (Class<? extends ExceptionHandler>) Class.forName(defaultProperties.getProperty("ExceptionHandler.class"));
			this.exceptionHandler = cls4.newInstance();
		} catch( Exception e ) {
			
		}
	}
	
	@SuppressWarnings("unused")
	private SQLProxy() {};
	
	public SQLProxy( Class<?> clazz ) throws Exception {
		this.interfacedClass = clazz;
		initDefaults();
		Assert.notNull(this.sqlExecuter);
		Assert.notNull(this.sqlParser);
	}
	
	public SQLProxy( Class<?> clazz, SQLExecuter executer, ApplicationContext applicationContext ) throws Exception {
		initDefaults();
		this.interfacedClass = clazz;
		if( executer != null ) this.sqlExecuter = executer;
		this.applicationContext = applicationContext;
		
		Assert.notNull(this.sqlExecuter);
		Assert.notNull(this.sqlParser);
	}
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if( "equals".equals(method.getName()) ) {
			return false;
		}
		
		logger.debug("AgentProxy[" + proxy.getClass() + "].[" + method.getName() + "] executed.");
		
		Object result = null;
		
		try {
			ExecuteContext ctx = this.sqlParser.parse(this.interfacedClass, method, args);
			
			DataSource ds = this.applicationContext.getBean("dataSource",DataSource.class);
			
			if( logger.isInfoEnabled() ) {
				logger.info("SQL is about to execute: " + ctx.getSql() + "; args:" + JSON.toJSONString(ctx.getArgs()));
			}
			result = sqlExecuter.execute(method, ds, ctx.getSql(), ctx.getArgs());
		} catch( Throwable e ) {
			if( this.exceptionHandler != null ) {
				Throwable ex = this.exceptionHandler.wrapException(e);
				if( ex != null ) {
					throw ex;
				}
			} else {
				Class<?>[] decExceptions = method.getExceptionTypes();
				for( Class<?> et : decExceptions ) {
					if( et.isAssignableFrom( e.getClass() ) ) {
						throw e;
					}
				}
				throw new RuntimeException( e.getMessage() );
			}
		}
		
		return result;
	}
	
}

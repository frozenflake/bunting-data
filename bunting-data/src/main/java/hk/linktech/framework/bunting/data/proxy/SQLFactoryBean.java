package hk.linktech.framework.bunting.data.proxy;

import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SQLFactoryBean implements FactoryBean<Object>,ApplicationContextAware {
	
	private Class<?> originalInterface;
	private ApplicationContext applicationContext;
	
	@SuppressWarnings("unused")
	private SQLFactoryBean() {};

	public SQLFactoryBean( Class<?> intf ) {
		this.originalInterface = intf;
	}
	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{ this.originalInterface }, new SQLProxy(this.originalInterface, null, this.applicationContext));
	}

	@Override
	public Class<?> getObjectType() {
		return this.originalInterface;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}

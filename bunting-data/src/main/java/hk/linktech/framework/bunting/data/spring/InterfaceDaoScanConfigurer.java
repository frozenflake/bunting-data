package hk.linktech.framework.bunting.data.spring;

import static org.springframework.util.Assert.notNull;
import hk.linktech.framework.bunting.data.proxy.SQLFactoryBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class InterfaceDaoScanConfigurer implements
		BeanDefinitionRegistryPostProcessor, InitializingBean,
		ApplicationContextAware, BeanNameAware {

	Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unused")
	private String beanName;
	private String basePackage;
	private String defaultFactoryBean = SQLFactoryBean.class.getName();
	private String factoryBean;

	public void setFactoryBean(String factoryBean) {
		this.factoryBean = factoryBean;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(this.basePackage, "Property 'basePackage' is required");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		logger.info("开始扫描目录");
		InterfaceDaoScanner scanner = new InterfaceDaoScanner(registry);
		try {
			if( this.factoryBean == null || this.factoryBean.equals("") ) {
				scanner.setFactoryBean( Class.forName(this.defaultFactoryBean)  );
			} else {
				scanner.setFactoryBean(Class.forName(this.factoryBean));
			}
		}catch (ClassNotFoundException e) {
			scanner.setFactoryBean(SQLFactoryBean.class);
		}
		scanner.scan(this.basePackage);
		logger.info("扫描目录结束");
	}
}

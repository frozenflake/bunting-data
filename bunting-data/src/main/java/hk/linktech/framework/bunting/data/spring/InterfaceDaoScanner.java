package hk.linktech.framework.bunting.data.spring;

import hk.linktech.framework.bunting.data.annotation.Dao;

import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

public class InterfaceDaoScanner extends ClassPathBeanDefinitionScanner {
	
	private Class<?> factoryBean;

	public void setFactoryBean(Class<?> factoryBean) {
		this.factoryBean = factoryBean;
	}

	public InterfaceDaoScanner(BeanDefinitionRegistry registry) {
		super(registry);
		this.addIncludeFilter(new AnnotationTypeFilter(Dao.class));
	}

	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected boolean isCandidateComponent(
			AnnotatedBeanDefinition beanDefinition) {
		if (beanDefinition.getMetadata().hasAnnotation(Dao.class.getName())) {
			return true;
		}
		return false;
	}

	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No interfaced Dao was found in '"
					+ Arrays.toString(basePackages)
					+ "' package. Please check your configuration.");
		} else {
			processBeanDefinitions(beanDefinitions);
		}

		return beanDefinitions;
	}
	
	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
	    GenericBeanDefinition definition;
	    Assert.notNull(this.factoryBean);
	    for (BeanDefinitionHolder holder : beanDefinitions) {
	      definition = (GenericBeanDefinition) holder.getBeanDefinition();

	      if (logger.isDebugEnabled()) {
	        logger.debug("Creating MapperFactoryBean with name '" + holder.getBeanName() 
	          + "' and '" + definition.getBeanClassName() + "' mapperInterface");
	      }

	      // the mapper interface is the original class of the bean
	      // but, the actual class of the bean is MapperFactoryBean
	      definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // issue #59
	      definition.setBeanClass( this.factoryBean );

	    }
	  }
}

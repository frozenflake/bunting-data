package hk.linktech.framework.test;

import hk.linktech.framework.test.service.TransactionService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-bunting-data.xml"})
public class APP //implements ApplicationContextAware
{
	@Autowired
	private ApplicationContext context;
	@Autowired
	private TransactionService service;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void testRegistry() {
		logger.info("测试");
	}
	
	@Test
	public void testDao() {
		try {
			this.service.testTx();
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
}

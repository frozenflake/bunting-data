package hk.linktech.framework.test.service;

import hk.linktech.framework.test.bean.TradeWorks;
import hk.linktech.framework.test.dao.ITradeWorksDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
	
	@Autowired
	ITradeWorksDao tradeWorksDao;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Transactional
	public void testTx() {
		Document doc = new Document();
		//doc.setId("Hello Doc");
		TradeWorks tw = this.tradeWorksDao.getById(148670593970086048L);
		logger.info("OrderId is " + tw.getOrderid());
		//this.tradeWorksDao.insertTest("TEST_1");
		//this.tradeWorksDao.insertTest("TEST_1");
		//Document doc = new Document();
		//doc.setId("Hello Doc");
		//doc.setDocid("Hello docid");
		//this.tradeWorksDao.insertTest(doc);
	}
	
	public class Document {
		String id;
		String docid;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDocid() {
			return docid;
		}
		public void setDocid(String docid) {
			this.docid = docid;
		}
		
	}
}

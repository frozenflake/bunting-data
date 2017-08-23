package hk.linktech.framework.test.dao;

import hk.linktech.framework.bunting.data.annotation.Dao;
import hk.linktech.framework.bunting.data.annotation.Param;
import hk.linktech.framework.bunting.data.annotation.SQLStatement;
import hk.linktech.framework.bunting.data.annotation.SqlMapper;
import hk.linktech.framework.test.bean.TradeWorks;
import hk.linktech.framework.test.service.TransactionService.Document;

@Dao
public interface ITradeWorksDao {
	
	@SqlMapper( statements={ 
			@SQLStatement(value="SELECT _id id, _orderId orderId  FROM _trade_works WHERE 1 = 1"),
			@SQLStatement(value="AND _id = {id}",check="#id != null"),
			@SQLStatement("LIMIT 0,1") } )
	public TradeWorks getById( @Param(value="id",required=false)Long id );
	
	@SqlMapper("INSERT Document(`id`,`docid`) VALUES ( {id}, {id} )")
	public void insertTest(@Param("id")String id);
	
	@SqlMapper("INSERT Document(`id`,`docid`) VALUES ( {doc.id}, {doc.docid} )")
	public void insertTest(@Param("doc")Document doc);
}
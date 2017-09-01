# bunting-data

This is a spring plugin mapping SQL statement to an java interface's method using annotations.
It's simple to use and easy to integrated with your own spring project.
To use bunting-data, just need to add maven dependency to your pom.xml like this:

<dependency>
    <groupId>hk.linktech.framework.bunting</groupId>
    <artifactId>data</artifactId>
    <version>1.0.0</version>
</dependency>

In your spring config file, just add a source sanner bean named xxxx to it, this bean will automaticly scan your source packages 
and instantiate DAO objects implemented your @Dao annotationed interfaces,and compose a SQL statement with SqlMapper annotations
when invoking and return a instance type of the method returned(by reflect).

<bean id="daoScanConfigurer" class="hk.linktech.framework.bunting.data.spring.InterfaceDaoScanConfigurer">
   <property name="basePackage"><value>somepackage</value></property>
</bean>

Using it like this:

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

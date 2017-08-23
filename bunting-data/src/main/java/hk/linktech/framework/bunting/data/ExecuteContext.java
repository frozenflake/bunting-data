package hk.linktech.framework.bunting.data;

public class ExecuteContext {
	private String sql;
	private Object[] args;
	
	public ExecuteContext( String sql, Object[] args ) {
		this.sql = sql;
		this.args = args;
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
}

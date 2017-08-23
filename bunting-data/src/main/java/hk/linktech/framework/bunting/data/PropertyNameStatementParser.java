package hk.linktech.framework.bunting.data;

import hk.linktech.framework.bunting.data.annotation.Param;
import hk.linktech.framework.bunting.data.annotation.SQLStatement;
import hk.linktech.framework.bunting.data.annotation.SqlMapper;
import hk.linktech.framework.bunting.data.exception.ParseException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class PropertyNameStatementParser implements SqlParser  {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private ExpressionParser parser = new SpelExpressionParser();  
	
	@Override
	public ExecuteContext parse(Class<?> interfaceClass, Method method, Object[] args)
			throws Throwable {
		
		ExecuteContext context = null;
		
		if( method == null ) {
			throw new Exception("Method parameter is null.");
		}
		
		SqlMapper sqlInfo = method.getAnnotation(SqlMapper.class);
		if( sqlInfo == null ) {
			throw new Exception("No Executer annotation found.");
		}
		
		Map<String,Object> paramMap = null;
		try {
			paramMap = this.getPropertyValues(method, args);
		} catch (ParseException e) {
			throw new Exception( e.getMessage() );
		}

		String inputSql = sqlInfo.value();
		if( null == inputSql || "".equals(inputSql.trim()) ) {
			SQLStatement[] statements = sqlInfo.statements();
			inputSql = this.parseConditionSql(statements, paramMap, args);
		}
		
		if( null == inputSql || "".equals(inputSql.trim()) ) {
			throw new Exception("SQL statement length is 0.");
		}
		
		try {
			List<String> sqlParams = this.parsePropertyList(inputSql);
			context = this.parseInputSql(inputSql, paramMap, sqlParams);
		} catch (ParseException e) {
			throw new Exception( e.getMessage() );
		}
		
		return context;
	}
	
	protected boolean checkCondition( String expression, final Map<String,Object> paramMap, final Object[] args ) throws ParseException {
		boolean ok = false;
		if( expression == null || "".equals(expression.trim()) ) {
			return true;
		}
		synchronized( this.parser ) {
			EvaluationContext context = new StandardEvaluationContext();
			Set<Entry<String,Object>> ens = paramMap.entrySet();
			for( Entry<String,Object> en : ens ) {
				context.setVariable(en.getKey(), en.getValue());
			}
			try {
				ok = this.parser.parseExpression(expression).getValue(context,Boolean.class);
				if( logger.isDebugEnabled() ) {
					logger.debug("Spring Expression Language [" + expression + "] executed is [" + ok + "]");
				}
			} catch( org.springframework.expression.ParseException | EvaluationException e ) {
				throw new ParseException( e.getMessage() );
			}
		}
		return ok;
	}
	
	protected final String parseConditionSql( SQLStatement[] statements, Map<String,Object> paramMap, Object[] args ) throws ParseException {
		String _sql = "";
		for( SQLStatement st : statements ) {
			if( checkCondition( st.check(), paramMap, args ) ) { 
				_sql += " " + st.value();
			}
		}
		return _sql;
	}
	
	private final ExecuteContext parseInputSql( String sql, Map<String,Object> paramMap, List<String> sqlParams ) throws ParseException {
		if( sqlParams == null || sqlParams.size() < 1 ) {
			return new ExecuteContext(sql, null);
		}
		List<Object> args = new ArrayList<Object>();
		for( String param : sqlParams ) {
			sql = sql.replaceAll( "\\{" + param + "\\}", "?" );
			if( paramMap.containsKey( param ) ) {
				args.add( paramMap.get(param) );
			} else {
				Object spv = this.specialParameterParse(param, paramMap);
				if( spv != null ) {
					args.add(spv);
				} else {
					//throw new ParseException("Request param with name " + param + " not found in arguments.");
					args.add(null);
				}
			}
		}
		
		return new ExecuteContext( sql, args.toArray() );
	}
	
	protected Object specialParameterParse( String param, Map<String,Object> paramMap ) throws ParseException {
		Object _value = null;
		String[] pair = param.split("\\.");
		if( pair != null && pair.length == 2 && pair[0].length() > 0 && pair[1].length() > 0 ) {
			Object _bean = null;
			if( paramMap.containsKey( pair[0] ) ) {
				_bean = paramMap.get(pair[0]);
			}
			if( _bean != null && !_bean.getClass().isPrimitive() ) {
				try {
					_value = BeanUtils.getProperty(_bean, pair[1]);
				} catch (IllegalAccessException | InvocationTargetException
						| NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*if( _value == null ) {
			throw new ParseException("Request param with name " + param + " not found in arguments.");
		}*/
		
		return _value;
	}
	
	protected List<String> parsePropertyList( String sql ) {
		List<String> list = new ArrayList<String>();
		int sp = 0;
		while( ( sp = sql.indexOf("{", sp) ) != -1 ) {
			int s0 = sp + 1;
			String name = sql.substring( s0, ( sp = sql.indexOf("}", sp) ) );

			list.add(name);
		}
		return list;
	}
	
	private final Map<String,Object> getPropertyValues( Method method, Object[] args ) throws ParseException {
		if( method == null ) {
			throw new ParseException( "Parameter Method is null" );
		}
		
		Map<String,Object> map = new HashMap<String,Object>();

		Annotation[][] annoss = method.getParameterAnnotations();
		if( annoss == null || annoss.length < 1 ) {
			return map;
		}
		
		int i = 0;
		for( Annotation[] annos : annoss ) {
			if( annos != null && annos.length > 0 ) {
				for( Annotation anno : annos ) {
					if( anno instanceof Param ) {
						String vname = ((Param)anno).value();
						
						if( ((Param)anno).required() && args[i] == null ) {
							throw new ParseException( "Required param " + vname + " is null" );
						}
						
						map.put(vname, args[i]);
						break;
					}
				}
			}
			i++;
		}
		
		return map;
	}
}

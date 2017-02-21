package org.hqjpa

import scala.reflect.ClassTag

/**
 * Is used to add function calling support to query builders while avoiding code duplication.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
trait FunctionCallSupport { self : IQueryBuilder => 
	/**
	 * Call given function.
	 * 
	 * @param RESULT Type of function result.
	 * 
	 * @param name Function name.
	 * @param args Function arguments.
	 * @return Function result expression proxy.
	 */
	def function[RESULT : ClassTag](name : String)(args : IExpressionProvider[_]*) : ExpressionProxy[RESULT] = {
		//get result class
		val resultClass = implicitly[ClassTag[RESULT]].runtimeClass.asInstanceOf[Class[RESULT]];
		
		//get function arguments
		val argExprs = args.map { arg => arg.__getExpression() };
		
		//create function call expression
		val expr = criteriaBuilder.function(name, resultClass, argExprs :_*);
		val proxy = new ExpressionProxy(expr, this);
		
		//
		return proxy;
	}
	
	/**
	 * Call given function.
	 * 
	 * @param RESULT Type of function result.
	 * 
	 * @param name Function name.
	 * @param args Function arguments.
	 * @return Function result expression proxy.
	 */
	def apply[RESULT : ClassTag](name : String)(args : IExpressionProvider[_]*) : ExpressionProxy[RESULT] = {
		return function[RESULT](name)(args :_*);
	}
	
	/**
	 * Call given function via function definition. Assuming a proper function 
	 * definition, implementing proper apply() method, the actual use of this method
	 * should be q(fundef)(args...), where q is some instance of query builder.
	 * @param functionDef Function definition to use.
	 * @return An copy of given funtion definition prepared for the call.
	 */
	def function[FUNDEF <: ISQLFunctionDef](functionDef : FUNDEF) : FUNDEF = {
		//clone given function definition, supply host query builder
		val cpy = functionDef.clone().asInstanceOf[FUNDEF];
		cpy.queryBuilder = this;
		
		//
		return cpy;
	}
	
	/**
	 * Call given function via function definition. Assuming a proper function 
	 * definition, implementing proper apply() method, the actual use of this method
	 * should be q(fundef)(args...), where q is some instance of query builder.
	 * @param functionDef Function definition to use.
	 * @return An copy of given funtion definition prepared for the call.
	 */
	def apply[FUNDEF <: ISQLFunctionDef](functionDef : FUNDEF) : FUNDEF = {
		return function[FUNDEF](functionDef);
	}
}
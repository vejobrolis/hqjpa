package org.hqjpa

import scala.reflect.ClassTag
import javax.persistence.criteria.CriteriaBuilder

/**
 * Trait for SQL function definitions.<br/>
 * <br/>
 * This trait defines a stub that gets cloned at query construction and then is
 * passed current IQueryBuilder. This allows the stub to be defined and used
 * as value with DB service.<br/> 
 * <br/>
 * To be actually usefull, the derivation should define ann apply() method accepting
 * expected number of arguments with IExpressionProvider's of expected type and
 * returning an EpressionProxy with expected result type. The function can then
 * be called in query as:<pre>
 * 	val result = q(derivation)(args...);
 * </pre>
 * <br/> 
 * Static members are thread safe, instance members are not. 
 */
trait ISQLFunctionDef extends Cloneable {
	/** Host query builder. Set at query construction time, after cloning. */
	var queryBuilder : IQueryBuilder = _;
		
	/**
	 * Makes a shallow copy of this function definition.
	 * @return A shallow copy of this function definition.
	 */
	override def clone() : ISQLFunctionDef = {
		val cpy = super.clone();
		return cpy.asInstanceOf[ISQLFunctionDef];
	}
	
	/** Shorthand for host query builder (queryBuilder). */
	protected def qb : IQueryBuilder = {
		return queryBuilder;
	}
	
	/** Shorthand for criteria builder of host query builder (queryBuilder.criteriaBuilder). */
	protected def cb : CriteriaBuilder = {
		return queryBuilder.criteriaBuilder;
	}
	
	/**
	 * Helper for building function applicator. 
	 * 
	 * @param RESULT Type of function result.
	 * 
	 * @param name Function name.
	 * @param args Function arguments.
	 * @return Function result expression proxy.
	 */
	protected def call[RESULT : ClassTag](name : String)(args : IExpressionProvider[_]*) : ExpressionProxy[RESULT] = {
		//get result class
		val resultClass = implicitly[ClassTag[RESULT]].runtimeClass.asInstanceOf[Class[RESULT]];
		
		//get function arguments
		val argExprs = args.map { arg => arg.__getExpression() };
		
		//create function call expression
		val expr = queryBuilder.criteriaBuilder.function(name, resultClass, argExprs :_*);
		val proxy = new ExpressionProxy(expr, queryBuilder);
		
		//
		return proxy;
	}
	
	/**
	 * Call given function via function definition. Assuming a proper function 
	 * definition, implementing proper apply() method, the actual use of this method
	 * should be call(fundef)(args...).<br/>
	 * <br/>
	 * This call is for building composite functions.
	 * @param functionDef Function definition to use.
	 * @return An copy of given funtion definition prepared for the call.
	 */
	protected def call[FUNDEF <: ISQLFunctionDef](functionDef : FUNDEF) : FUNDEF = {
		//clone given function definition, supply host query builder
		val cpy = functionDef.clone().asInstanceOf[FUNDEF];
		cpy.queryBuilder = queryBuilder;
		
		//
		return cpy;
	}
	
	/**
	 * Create query literal from given value.
	 * 
	 * @param value Type of literal value.
	 * 
	 * @param value Value to create the literal from.
	 * @return Expression proxy for the literal created.
	 */
	protected def literal[VALUE <: AnyRef](value : VALUE) : ExpressionProxy[VALUE] = {
		val expr = queryBuilder.criteriaBuilder.literal(value);
		val proxy = new ExpressionProxy(expr, queryBuilder);
		return proxy;
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Byte) : ExpressionProxy[java.lang.Byte] = {
		return literal(value : java.lang.Byte);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Short) : ExpressionProxy[java.lang.Short] = {
		return literal(value : java.lang.Short);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Int) : ExpressionProxy[java.lang.Integer] = {
		return literal(value : java.lang.Integer);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Long) : ExpressionProxy[java.lang.Long] = {
		return literal(value : java.lang.Long);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Float) : ExpressionProxy[java.lang.Float] = {
		return literal(value : java.lang.Float);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Double) : ExpressionProxy[java.lang.Double] = {
		return literal(value : java.lang.Double);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Boolean) : ExpressionProxy[java.lang.Boolean] = {
		return literal(value : java.lang.Boolean);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	protected def literal(value : Char) : ExpressionProxy[java.lang.Character] = {
		return literal(value : java.lang.Character);
	}
}
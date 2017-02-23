package org.hqjpa

import scala.collection.JavaConverters._
import javax.persistence.criteria.AbstractQuery
import javax.persistence.criteria.JoinType


/**
 * Is used to add support for common artifacts of SELECT queries to SelectQueryBuilder and 
 * SelectSuqueryBuilder while avoiding code duplication.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
trait SelectQuerySupport { self : IQueryBuilder =>
	/** Host JPA query. */
	def jpaQuery : AbstractQuery[_];
		
	/** 
	 * Create a COUNT expression on queryBuilder root.
	 * 
	 * @param OWNER Type of parent entity.
	 * @param ENTITY Type of entity being joined.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @return A proxy for the expression.
	 */
	def count[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF](entityProxy : EP[OWNER, ENTITY, SELF]) : ExpressionProxy[java.lang.Long] = {
		//get query root from parent entity proxy of fail if one is not available
		val root = entityProxy.__root.getOrElse {
				val msg = 
					"Unable to COUNT on entity that is not a query root. This probably means that" +
					"you are skipping an intermediate entity in a join or using meta-data entity directly. " +
					"Perform your joins sequentially without skipping intermediate entities, use from() to " +
					"derive countable query roots.";
				throw new AssertionError(msg);
			};
		
		//produce count expression
		val expr = criteriaBuilder.count(root);
		val exprProxy = new ExpressionProxy(expr, this);
		
		//
		return exprProxy;
	}
	
	/** 
	 * Create a COUNT(DISTINCT ...) expression on queryBuilder root.
	 * 
	 * @param OWNER Type of parent entity.
	 * @param ENTITY Type of entity being joined.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @return A proxy for the expression.
	 */
	def countDistinct[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF](entityProxy : EP[OWNER, ENTITY, SELF]) : ExpressionProxy[java.lang.Long] = {
		//get query root from parent entity proxy of fail if one is not available
		val root = entityProxy.__root.getOrElse {
				val msg = 
					"Unable to COUNT on entity that is not a query root. This probably means that" +
					"you are skipping an intermediate entity in a join or using meta-data entity directly. " +
					"Perform your joins sequentially without skipping intermediate entities, use from() to " +
					"derive countable query roots.";
				throw new AssertionError(msg);
			};
		
		//produce count expression
		val expr = criteriaBuilder.countDistinct(root);
		val exprProxy = new ExpressionProxy(expr, this);
		
		//
		return exprProxy;
	}
	
	/** 
	 * Create query root in JOIN clause.
	 * 
	 * @param OWNER Type of parent entity.
	 * @param ENTITY Type of entity being joined.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @param joinType Type of JOIN clause.
	 * @return A new entity proxy over query join root.
	 */
	def join[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF](entityProxy : EP[OWNER, ENTITY, SELF], joinType : JoinType) : SELF = {
		//get parent entity proxy or fail if one is not available
		val parent = entityProxy.__parentEntityProxy.getOrElse {
				val msg = 
					"Unable to join on entity proxy without a parent. " +
					"This probably means that you are trying to join directly on meta-data entity. " +
					"Create a query root with from() or join() and use that.";
				throw new AssertionError(msg);
			};
			
		//get query root from parent entity proxy of fail if one is not available
		val root = parent.__root.getOrElse {
				val msg = 
					"Unable to join on entity with a parent that is not a query root. " +
					"This probably means that you are skipping an intermediate entity in a join. " +
					"Perform your joins sequentially without skipping intermediate entities. ";
				throw new AssertionError(msg);
			};
			
		//produce a join
		val join =
			if( entityProxy.__singularAttribute.isDefined ) {
				root.join(entityProxy.__singularAttribute.get, joinType);
			}
			else if( entityProxy.__setAttribute.isDefined ) {
				root.join(entityProxy.__setAttribute.get, joinType);
			}
			else {
				val msg = 
					"No supported attribute type is defined. This probably means that attribute type was " + 
					"added to generator but related support was not added to SelectQueryBuilder.";
				throw new AssertionError(msg);
			}
		
		//create join entity proxy
		val joinEntityProxy = entityProxy.clone().asInstanceOf[EP[OWNER, ENTITY, SELF]];
		joinEntityProxy.__root = Some(join);
		joinEntityProxy.__queryBuilder = this;
		
		//
		return joinEntityProxy.asInstanceOf[SELF];
	}
	
	/** 
	 * Create query root in LEFT (OUTER) JOIN clause.
	 * 
	 * @param OWNER Type of parent entity.
	 * @param ENTITY Type of entity being joined.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @return A new entity proxy over query join root.
	 */
	def leftJoin[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF](entityProxy : EP[OWNER, ENTITY, SELF]) : SELF = {
		return join(entityProxy, JoinType.LEFT);
	}
	
	/** 
	 * Create query root in RIGHT (OUTER) JOIN clause.
	 * 
	 * @param OWNER Type of parent entity.
	 * @param ENTITY Type of entity being joined.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @return A new entity proxy over query join root.
	 */
	def rightJoin[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF](entityProxy : EP[OWNER, ENTITY, SELF]) : SELF = {
		return join(entityProxy, JoinType.RIGHT);
	}
	
	/** 
	 * Create query root in INNER JOIN clause.
	 * 
	 * @param OWNER Type of parent entity.
	 * @param ENTITY Type of entity being joined.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @return A new entity proxy over query join root.
	 */
	def innerJoin[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF](entityProxy : EP[OWNER, ENTITY, SELF]) : SELF = {
		return join(entityProxy, JoinType.INNER);
	}
	
	/**
	 * Create a "true" predicate for use in WHERE clauses in a form that passes
	 * Hibernate query parser.
	 * @return A "true" predicate. 
	 */
	def noop : PredicateProxy = {
		val pred = criteriaBuilder.equal(criteriaBuilder.literal(1), criteriaBuilder.literal(1));
		val proxy = new PredicateProxy(pred, this);
		
		//
		return proxy;
	}
		
	/**
	 * Start a first WHEN/THEN statement of a case expression.
	 * 
	 * @param RESULT Type of the result of the statement/expression.
	 * 
	 * @param cond Condition of the statement.
	 * @param result Result of the statement. 
	 * @return A new instance of a case expression proxy, with first WHEN/THEN statement set to given one.
	 */
	def when[RESULT](cond : PredicateProxy, result : ExpressionProxy[RESULT]) : CaseExprProxy[RESULT] = {
		//create CASE expression and proxy
		val expr = criteriaBuilder.selectCase[RESULT]();
		val proxy = new CaseExprProxy(expr, this);
		
		//set first WHEN/THEN statement
		proxy.when(cond, result);
		
		//
		return proxy;
	}
	
	/**
	 * Start a first WHEN/THEN statement of a case expression.
	 * 
	 * @param RESULT Type of the result of the statement/expression.
	 * 
	 * @param cond Condition of the statement.
	 * @param result Result of the statement. 
	 * @return A new instance of a case expression proxy, with first WHEN/THEN statement set to given one.
	 */
	def when[RESULT](cond : PredicateProxy, result : RESULT) : CaseExprProxy[RESULT] = {
		//create CASE expression and proxy
		val expr = criteriaBuilder.selectCase[RESULT]();
		val proxy = new CaseExprProxy(expr, this);
		
		//set first WHEN/THEN statement
		proxy.when(cond, result);
		
		//
		return proxy;
	}
	
	/**
	 * Set WHERE clause of query. Will overwrite any previously set WHERE clause.
	 * @param predicateProxy Predicate proxy containing predicate for the WHERE clause.
	 * @returns Self. For call chaining.
	 */
	def where(predicateProxy : PredicateProxy) : Unit = {
		//set the clause
		jpaQuery.where(Vector(predicateProxy.predicate) :_*);
	}
	
	/**
	 * Set HAVING clause of query. Will overwrite any previously set HAVING clause.
	 * @param exprProxy Expression to use in HAVING clause.
	 * @returns Self. For call chaining.
	 */
	def having(exprProxy : ExpressionProxy[java.lang.Boolean]) : Unit = {
		//set the clause
		jpaQuery.having(exprProxy.expr);
	}
	
	/**
	 * Set HAVING clause of query. Will overwrite any previously set HAVING clause.
	 * @param predProxy Predicate to use in HAVING clause.
	 * @returns Self. For call chaining.
	 */
	def having(predProxy : PredicateProxy) : Unit = {
		//set the clause
		jpaQuery.having(Vector(predProxy.predicate) :_*);
	}
	
	/**
	 * Create a COALESCE(a, b) expression.
	 * @param a First argument of COALESCE expression.
	 * @param b Second argument of COALESCE expression.
	 */
	def coalesce[VALUE](a : ExpressionProxy[VALUE], b : VALUE) : ExpressionProxy[VALUE] = {
		val ce = criteriaBuilder.coalesce(a.expr, b);
		val proxy = new ExpressionProxy(ce, this);
		
		//
		return proxy;
	}
	
	/**
	 * Set GROUP BY clause of query. Will replace any previously set GROUP BY clause.
	 * @param groups Groups to use.
	 * @returns Self. For call chaining.
	 */
	def groupBy(groups : IExpressionProvider[_]*) : Unit = {
		val groupsList = groups.map { group => group.__getExpression() }.asJava;
		jpaQuery.groupBy(groupsList);
	}	
}
package org.hqjpa

import scala.collection.JavaConverters._
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Subquery

/**
 * Builder for nested SELECT queries. JPA only allows returning single expression
 * from nested SELECT query, which can be either a single entity, a single attribute
 * of an entity or a computed expression.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param host Host SELECT query builder.
 * @param jpaQuery Underlying JPA (sub)query.
 */
class SelectSubqueryBuilder(
	val host : IQueryBuilder,
	val jpaQuery : Subquery[Any]
)
extends
	IQueryBuilder with
	ScalaNumericLiteralsSupport with
	SubquerySupport with
	SelectQuerySupport
{
	/** Related JPA criteria builder. */
	override def criteriaBuilder : CriteriaBuilder = {
		host.criteriaBuilder;
	}
	
	/** 
	 * Create query root in FROM clause.
	 * 
	 * @param ENTITY Type of entity being selected from.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @return A new entity proxy over query root.
	 */
	def from[EP[_, ENTITY, SELF] <: EntityProxy[_, ENTITY, SELF], ENTITY, SELF](entityProxy : EP[_, ENTITY, SELF]) : SELF = {
		//create query root
		val root = jpaQuery.from(entityProxy.__entityClass);
		
		//create root entity proxy
		val rootEntityProxy = entityProxy.clone().asInstanceOf[EP[_, ENTITY, SELF]];
		rootEntityProxy.__root = Some(root);
		rootEntityProxy.__queryBuilder = this;
		
		//
		return rootEntityProxy.asInstanceOf[SELF];
	}
	
	/**
	 * Create query literal from given value.
	 * 
	 * @param value Type of literal value.
	 * 
	 * @param value Value to create the literal from.
	 * @return Expression proxy for the literal created.
	 */
	override def literal[VALUE <: AnyRef](value : VALUE) : ExpressionProxy[VALUE] = {
		val expr = criteriaBuilder.literal(value);
		val proxy = new ExpressionProxy(expr, this);
		
		return proxy;
	}	
	
	/**
	 * Set SELECT clause of query, marking SELECT as NON DISTINCT. Will overwrite any previously 
	 * set SELECT clause.
	 * 
	 * @param RESULT Underlying type of artifact selected.
	 * 
	 * @param what Object being selected.
	 * @return Object being selected.
	 */
	def select[RESULT](what : IExpressionProvider[RESULT]) : IExpressionProvider[RESULT] = {
		//set SELECT clause, mark query as non-distinct
		jpaQuery.asInstanceOf[Subquery[RESULT]].select(what.__getExpression());
		jpaQuery.distinct(false);
		
		//
		return what;
	}
	
	/**
	 * Set SELECT clause of query, marking SELECT as DISTINCT. Will overwrite any previously set
	 * SELECT clause.
	 * 
	 * @param RESULT Underlying type of artifact selected.
	 * 
	 * @param what Object being selected.
	 * @return Object being selected.
	 */
	def selectDistinct[RESULT](what : IExpressionProvider[RESULT]) : IExpressionProvider[RESULT] = {
		//set SELECT clause, mark query as distinct
		jpaQuery.asInstanceOf[Subquery[RESULT]].select(what.__getExpression());
		jpaQuery.distinct(true);
		
		//
		return what;
	}
}
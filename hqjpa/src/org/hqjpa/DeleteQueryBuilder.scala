package org.hqjpa

import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaDelete
import javax.persistence.criteria.CriteriaBuilder

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object DeleteQueryBuilder {
	/**
	 * Allows controlling delete query.<br>
	 * <br/>
	 * Static members are thread safe, instance members are not.
	 * 
	 * @param ENTITY Type of target entity.
	 */
	class ResultChoice[ENTITY](val queryBuilder : DeleteQueryBuilder[ENTITY]) {
		/**
		 * Runs the query.
		 */
		def run() : Unit = {
			val em = queryBuilder.entityManager;
			val query = em.createQuery(queryBuilder.jpaQuery);
			query.executeUpdate();
		}
	}
}

/**
 * Builder for DELETE queries.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param ENTITY Type of target entity.
 * 
 * @param entityManager Host entity manager.
 * @param jpaQuery Related JPA update query. 
 */
class DeleteQueryBuilder[ENTITY](
		val entityManager : EntityManager, 
		val jpaQuery : CriteriaDelete[ENTITY]
	) 
	extends 
		IQueryBuilder with
		ScalaNumericLiteralsSupport with
		FunctionCallSupport with
		SubquerySupport
{
	import DeleteQueryBuilder._
	
	/** Underlying criteria builder. */
	override val criteriaBuilder : CriteriaBuilder = entityManager.getCriteriaBuilder();
	
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
	 * Set WHERE clause of query. Will overwrite any previously set WHERE clause.
	 * @param predicateProxy Predicate proxy containing predicate for the WHERE clause.
	 */
	def where(predicateProxy : PredicateProxy) : Unit = {
		jpaQuery.where(Vector(predicateProxy.predicate) :_*);
  	}
}
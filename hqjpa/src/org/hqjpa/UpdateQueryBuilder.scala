package org.hqjpa

import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaUpdate
import javax.persistence.criteria.Path
import javax.persistence.criteria.Expression
import javax.persistence.criteria.CriteriaBuilder

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object UpdateQueryBuilder {
	/**
	 * Allows controlling update query.<br>
	 * <br/>
	 * Static members are thread safe, instance members are not.
	 * 
	 * @param ENTITY Type of target entity.
	 */
	class ResultChoice[ENTITY](val queryBuilder : UpdateQueryBuilder[ENTITY]) {
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
 * Builder for UPDATE queries.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param ENTITY Type of target entity.
 * 
 * @param entityManager Host entity manager.
 * @param jpaQuery Related JPA update query. 
 */
class UpdateQueryBuilder[ENTITY](
		val entityManager : EntityManager, 
		val jpaQuery : CriteriaUpdate[ENTITY]
	) 
	extends 
		IQueryBuilder with
		ScalaNumericLiteralsSupport with
		SubquerySupport
{
  	import UpdateQueryBuilder._
  	
  	/** Underlying criteria builder. */
	override val criteriaBuilder : CriteriaBuilder = entityManager.getCriteriaBuilder();
  	
  	/**
  	 * ADD given name=value pairs to SET clause. Use -> operator on attributes to produce the pairs.
  	 * @param nameValuePair A list of name value pairs to add.
  	 * @returns Self. For call chaining. 
  	 */
  	def set(nameValuePairs : ((Path[Any], Any))*) : UpdateQueryBuilder[ENTITY] = {
  		//add name value pairs to query
  		nameValuePairs.foreach { pair =>
  			pair._2 match {
  				case expr : Expression[_] => jpaQuery.set(pair._1, expr.asInstanceOf[Expression[Any]]);
  				case value => jpaQuery.set(pair._1, value);
  			}  			
  		}
  		
  		//
  		return this;
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
	 * @returns Self. For call chaining.
	 */
	def where(predicateProxy : PredicateProxy) : UpdateQueryBuilder[ENTITY] = {
		//set the clause
		jpaQuery.where(Vector(predicateProxy.predicate) :_*);
		
		//
		return this;
  	}
}
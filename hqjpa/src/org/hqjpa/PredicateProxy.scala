package org.hqjpa

import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Selection
import javax.persistence.criteria.Expression


/**
 * Proxy for a predicate to be used in WHERE or HAVING clauses. Primitive predicates are 
 * created by comparison operators in implicit extensions of SingularAttributeProxy.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * @param predicate Predicate being proxied.
 * @param queryBuilder Host query builder.
 */
class PredicateProxy(
		val predicate : Predicate,
		val queryBuilder : IQueryBuilder		
	) 
	extends 
	ISelectionProvider[java.lang.Boolean] with
	IExpressionProvider[java.lang.Boolean]
{
	
	/**
	 * Create the predicate negating current predicate.
	 * @return Predicate negating current predicate.
	 */
	def unary_!() : PredicateProxy = {
		val notPredicate = predicate.not();
		val proxy = new PredicateProxy(notPredicate, queryBuilder);
		
		//
		return proxy;
	}
	
	/**
	 * Create "and" join of this predicate and other predicate.
	 * @param rigthArg Proxy of predicate to "and" join.
	 * @return Predicate proxy of "and" join.
	 */
	def &&(rightArg : PredicateProxy) : PredicateProxy = {
		val andPredicate = queryBuilder.criteriaBuilder.and(Vector(predicate, rightArg.predicate) :_*);
		val proxy = new PredicateProxy(andPredicate, queryBuilder);
		
		//
		return proxy;
	}
	
	/**
	 * Create "and" join of this predicate and other predicate.
	 * @param rigthArg Proxy of predicate to "and" join.
	 * @return Predicate proxy of "and" join.
	 */
	def &&(rightArg : java.lang.Boolean) : PredicateProxy = {
		val cb = queryBuilder.criteriaBuilder;
		val andPredicate =cb.and(predicate, cb.literal(rightArg));
		val proxy = new PredicateProxy(andPredicate, queryBuilder);
		
		//
		return proxy;
	}
	
	/**
	 * Create "||" join of this predicate and other predicate.
	 * @param rigthArg Proxy of predicate to "or" join.
	 * @return Predicate proxy of "or" join.
	 */
	def ||(rightArg : PredicateProxy) : PredicateProxy = {
		val orPredicate = queryBuilder.criteriaBuilder.or(Vector(predicate, rightArg.predicate) :_*);
		val proxy = new PredicateProxy(orPredicate, queryBuilder);
		
		//
		return proxy;
	}
	
	/**
	 * Create "||" join of this predicate and other predicate.
	 * @param rigthArg Value to "or" join.
	 * @return Predicate proxy of "or" join.
	 */
	def ||(rightArg : java.lang.Boolean) : PredicateProxy = {
		val cb = queryBuilder.criteriaBuilder;
		val orPredicate = cb.or(predicate, cb.literal(rightArg));
		val proxy = new PredicateProxy(orPredicate, queryBuilder);
		
		//
		return proxy;
	}
	
	override def __getSelection() : Selection[java.lang.Boolean] = {
		return predicate;
	}
	
	override def __getExpression() : Expression[java.lang.Boolean] = {
		return predicate;
	}
}
package org.hqjpa

import javax.persistence.criteria.Expression


/**
 * Allows extracting expressions from various JPA class proxies in a uniform way. 
 * Used in groupBy() of SelectQueryBuilder.
 * @param T Underlying type of JPA expression being proxied.
 */
trait IExpressionProvider[T] {
	/**
	 * Get expression compatible part of the proxied object.
	 * @return Expression compatible part of the proxied object.
	 */
	def __getExpression() : Expression[T];
}

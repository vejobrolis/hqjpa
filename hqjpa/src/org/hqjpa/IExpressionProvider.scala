package org.hqjpa

import javax.persistence.criteria.Expression


/**
 * Allows extracting expressions from various JPA class proxies in a uniform way. 
 * Used in groupBy() of SelectQueryBuilder.
 * @param T Underlying type of JPA expression being proxied.
 */
trait IExpressionProvider[T] {
	/**
	 * Get selection compatible part of the proxied object for SELECT statement.
	 * @return Selection compatible part of the proxies object for SELECT statement.
	 */
	def __getExpression() : Expression[T];
}

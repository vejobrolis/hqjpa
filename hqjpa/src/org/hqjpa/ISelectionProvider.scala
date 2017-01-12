package org.hqjpa

import javax.persistence.criteria.Selection

/**
 * Allows extracting selections from various JPA class proxies in a uniform way.
 * @param T Underlying type of JPA expression being proxied.
 */
trait ISelectionProvider[T] {
	/**
	 * Get selection compatible part of the proxied object for SELECT statement.
	 * @return Selection compatible part of the proxies object for SELECT statement.
	 */
	def __getSelection() : Selection[T];
}
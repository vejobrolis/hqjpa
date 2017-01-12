package org.hqjpa

import javax.persistence.criteria.CriteriaBuilder

/**
 * Encapsulates commonly used attributes of query builders.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
trait IQueryBuilder {
	/** Underlying JPA criteria builder. */
	def criteriaBuilder : CriteriaBuilder;
}
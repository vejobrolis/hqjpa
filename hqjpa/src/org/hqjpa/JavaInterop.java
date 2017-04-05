package org.hqjpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * Resolver methods for Scala/Java interoperability issues.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
public class JavaInterop {
	/**
	 * Resolver for CriteriaBuilder.coalesce()
	 * @param cb Criteria builder to use.
	 * @param a Expression A.
	 * @param b Expression B.
	 * @return Resulting expression.
	 */
	public static <T> Expression<T> coalesceExpr(CriteriaBuilder cb, Expression<T> a, Expression<T> b) {
		return cb.coalesce(a, b);
	}
}

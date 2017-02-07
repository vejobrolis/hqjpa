package org.hqjpa

import javax.persistence.criteria.Subquery
import javax.persistence.criteria.Selection
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Order

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance memebers are not.
 */
object SubqueryProxy {
	/**
	 * Implicit type converter to add condition extensions (in ComparableConditionExtensions) to 
	 * proxies of expressions of comparable types.
	 * 
	 * @param VALUE Type of underlying expression.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toComparableExtensions[VALUE <: Comparable[VALUE]](
		src : SubqueryProxy[VALUE] 
	) : ComparableExtensions[VALUE] = 
	{		
		val extensions = new ComparableExtensions(src.subquery, src.queryBuilder);
		
		//
		return extensions;
	}	
	
	/**
	 * Implicit type converter to add string condition extensions (in StringConditionExtensions) to 
	 * proxies for expressions of string types.
	 * 
	 * @param VALUE Type of underlying expression.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toStringExtensions(src : SubqueryProxy[String]) : StringExtensions = {		
		val extensions = new StringExtensions(src.subquery, src.queryBuilder);
	
		//
		return extensions;
	}
	
	/**
	 * Implicit type converter to add operators in GenericExtensions to 
	 * proxies for expressions.
	 * 
	 * @param VALUE Type of underlying expression of expression being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toGenericExtensions[VALUE](src : SubqueryProxy[VALUE]) : GenericExtensions[VALUE] = {		
		val extensions = new GenericExtensions(src.subquery, src.queryBuilder);
		
		//
		return extensions;
	}	
	
	/**
	 * Implicit type converter to add operators in NumberExtensions to 
	 * proxies for expressions.
	 * 
	 * @param VALUE Type of underlying expression of expression being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toNumberExtensions[VALUE <: Number](src : SubqueryProxy[VALUE]) : NumberExtensions[VALUE] = {		
		val extensions = new NumberExtensions(src.subquery, src.queryBuilder);
		
		//
		return extensions;
	}
	
	/**
	 * Implicit type converter to add operators in FloatExtensions to 
	 * proxies for expressions.
	 * 
	 * @param VALUE Type of underlying expression of expression being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toFloatExtensions(src : SubqueryProxy[java.lang.Float]) : FloatExtensions = {		
		val extensions = new FloatExtensions(src.subquery, src.queryBuilder);
		
		//
		return extensions;
	}
	
	/**
	 * Implicit type converter to add operators in IntegerAggregateExtensions to 
	 * proxies for expressions.
	 * 
	 * @param VALUE Type of underlying expression of expression being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toIntegerExtensions(src : SubqueryProxy[java.lang.Integer]) : IntegerExtensions = {		
		val extensions = new IntegerExtensions(src.subquery, src.queryBuilder);
		
		//
		return extensions;
	}
		
	/**
	 * Extensions for proxies over comparable expressions.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param VALUE Type of underlying expression expression.
	 *
	 * @param expr Expression being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class ComparableExtensions[VALUE <: Comparable[VALUE]](
		override val subquery : Subquery[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		SubqueryProxy[VALUE](subquery, queryBuilder) with
		OperatorExtensions.ComparableExtensions[VALUE]
	{		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => subquery };
		
		/**
		 * Allows forcing comparable extensions on compatible expression proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def cmp = this;		
	}
	
	/**
	 * Additional extensions for expressions over strings.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param VALUE Type of underlying expression of expression being proxied.
	 *
	 * @param expr Expression being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class StringExtensions(
		override val subquery : Subquery[String],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		SubqueryProxy[String](subquery, queryBuilder) with
		OperatorExtensions.StringExtensions
	{
		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[String]) = { () => subquery };
		
		/**
		 * Allows forcing string extensions on compatible expression proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def str = this;	
	}
	
	/**
	 * Extensions for expressions over all types.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param VALUE Type of underlying expression of expression being proxied.
	 *
	 * @param expr Expression being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class GenericExtensions[VALUE](
		override val subquery : Subquery[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		SubqueryProxy[VALUE](subquery, queryBuilder) with
		OperatorExtensions.GeneralExtensions[VALUE]
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => subquery };		
		
		/**
		 * Allows forcing aggregate extensions on compatible expression proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def agg = this;		
	}
	
	/**
	 * Extensions for expressions over numbers.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 *
	 * @param expr Expression being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class NumberExtensions[VALUE <: Number](
		override val subquery : Subquery[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		SubqueryProxy[VALUE](subquery, queryBuilder) with
		OperatorExtensions.NumberExtensions[VALUE]
	{		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => subquery };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def num = this;		
	}
	
	/**
	 * Extensions for expressions over float numbers.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 *
	 * @param expr Expression being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class FloatExtensions(
		override val subquery : Subquery[java.lang.Float],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		SubqueryProxy[java.lang.Float](subquery, queryBuilder) with
		OperatorExtensions.FloatExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Float]) = { () => subquery };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def flt = this;	
	}
	
	/**
	 * Extensions for expressions over integer numbers.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 *
	 * @param expr Expression being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class IntegerExtensions(
		override val subquery : Subquery[java.lang.Integer],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		SubqueryProxy[java.lang.Integer](subquery, queryBuilder) with
		OperatorExtensions.IntegerExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Integer]) = { () => subquery };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def int = this;		
	}
}

/**
 * Proxy for hibernate subqueries.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param VALUE Type of underlying value selected.
 * 
 * @param subquer JPA subquery being proxied.
 */
class SubqueryProxy[VALUE](
		val subquery : Subquery[VALUE],
		val queryBuilder : IQueryBuilder
	)
	extends 
		ISelectionProvider[VALUE] with
		IExpressionProvider[VALUE]
{
	/**
	 * Ascending ordering for this subquery. 
	 */
	def asc : Order = {
		return queryBuilder.criteriaBuilder.asc(subquery);		
	}
	
	/**
	 * Descending ordering for this subquery.
	 */
	def desc : Order = {
		return queryBuilder.criteriaBuilder.desc(subquery);
	}
	
	/**
	 * Derive ordering from this subquery.
	 * @param asc True to derive ascending ordering, false to derive descending ordering.
	 * @return Ordering derived.
	 */
	def order(asc : Boolean) : Order = {
		return (if( asc ) this.asc else this.desc);
	}
	
	override def __getSelection() : Selection[VALUE] = {
		return subquery.getSelection();
	}	
	
	override def __getExpression() : Expression[VALUE] = {
		return subquery;
	}	
}
package org.hqjpa

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Selection
import javax.persistence.criteria.Order

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object CaseExprProxy {
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
		src : CaseExprProxy[VALUE] 
	) : ComparableExtensions[VALUE] = 
	{		
		val extensions = new ComparableExtensions(src.expr, src.queryBuilder);
		
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
	implicit def toStringExtensions(src : CaseExprProxy[String]) : StringExtensions = {		
		val extensions = new StringExtensions(src.expr, src.queryBuilder);
	
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
	implicit def toGenericExtensions[VALUE](src : CaseExprProxy[VALUE]) : GenericExtensions[VALUE] = {		
		val extensions = new GenericExtensions(src.expr, src.queryBuilder);
		
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
	implicit def toNumberExtensions[VALUE <: Number](src : CaseExprProxy[VALUE]) : NumberExtensions[VALUE] = {		
		val extensions = new NumberExtensions(src.expr, src.queryBuilder);
		
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
	implicit def toFloatExtensions(src : CaseExprProxy[java.lang.Float]) : FloatExtensions = {		
		val extensions = new FloatExtensions(src.expr, src.queryBuilder);
		
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
	implicit def toIntegerExtensions(src : CaseExprProxy[java.lang.Integer]) : IntegerExtensions = {		
		val extensions = new IntegerExtensions(src.expr, src.queryBuilder);
		
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
	 * @param expr CriteriaBuilder.Case being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class ComparableExtensions[VALUE <: Comparable[VALUE]](
		override val expr : CriteriaBuilder.Case[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		CaseExprProxy[VALUE](expr, queryBuilder) with
		OperatorExtensions.ComparableExtensions[VALUE]
	{		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => CriteriaBuilder.Case[VALUE]) = { () => expr };
		
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
	 * @param expr CriteriaBuilder.Case being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class StringExtensions(
		override val expr : CriteriaBuilder.Case[String],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		CaseExprProxy[String](expr, queryBuilder) with
		OperatorExtensions.StringExtensions
	{
		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => CriteriaBuilder.Case[String]) = { () => expr };
		
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
	 * @param expr CriteriaBuilder.Case being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class GenericExtensions[VALUE](
		override val expr : CriteriaBuilder.Case[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		CaseExprProxy[VALUE](expr, queryBuilder) with
		OperatorExtensions.GeneralExtensions[VALUE]
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => CriteriaBuilder.Case[VALUE]) = { () => expr };		
		
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
	 * @param expr CriteriaBuilder.Case being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class NumberExtensions[VALUE <: Number](
		override val expr : CriteriaBuilder.Case[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		CaseExprProxy[VALUE](expr, queryBuilder) with
		OperatorExtensions.NumberExtensions[VALUE]
	{		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => CriteriaBuilder.Case[VALUE]) = { () => expr };
		
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
	 * @param expr CriteriaBuilder.Case being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class FloatExtensions(
		override val expr : CriteriaBuilder.Case[java.lang.Float],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		CaseExprProxy[java.lang.Float](expr, queryBuilder) with
		OperatorExtensions.FloatExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => CriteriaBuilder.Case[java.lang.Float]) = { () => expr };
		
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
	 * @param expr CriteriaBuilder.Case being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class IntegerExtensions(
		override val expr : CriteriaBuilder.Case[java.lang.Integer],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		CaseExprProxy[java.lang.Integer](expr, queryBuilder) with
		OperatorExtensions.IntegerExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => CriteriaBuilder.Case[java.lang.Integer]) = { () => expr };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def int = this;		
	}
}

/**
 * Proxy and builder for CASE WHEN expressions.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param RESULT Type of result of the expression.
 * 
 * @param expr Case expression being proxied.
 * @param queryBuilder Parent query builder.
 */
class CaseExprProxy[RESULT] (
			val expr : CriteriaBuilder.Case[RESULT], 
			val queryBuilder : IQueryBuilder 
		) 
		extends ISelectionProvider[RESULT] {
	
	/**
	 * Get selection compatible part of the proxied object for SELECT statement.
	 * @return Selection compatible part of the proxies object for SELECT statement.
	 */
	override def __getSelection(): Selection[RESULT] = {
		return expr;
	}
	
	/**
	 * Add "WHEN cond THEN result" branch.
	 * @param cond Condition of the branch.
	 * @param result Resulf of the branch.
	 * @return Self.
	 */
	def when(cond : PredicateProxy, result : ExpressionProxy[RESULT]) : CaseExprProxy[RESULT] = {
		expr.when(cond.predicate, result.expr);
		return this;
	}
		
	/**
	 * Add "WHEN cond THEN result" branch.
	 * @param cond Condition of the branch.
	 * @param result Resulf of the branch.
	 * @return Self.
	 */
	def when(cond : PredicateProxy, result : RESULT) : CaseExprProxy[RESULT] = {
		expr.when(cond.predicate, result);
		return this;
	}
	
	/**
	 * Add "OTHERWISE result" branch.
	 * @param result Resulf of the branch.
	 * @return Self.
	 */
	def otherwise(result : ExpressionProxy[RESULT]) : CaseExprProxy[RESULT] = {
		expr.otherwise(result.expr);
		return this;
	}
	
	/**
	 * Add "OTHERWISE result" branch.
	 * @param result Resulf of the branch.
	 * @return Self.
	 */
	def otherwise(result : RESULT) : CaseExprProxy[RESULT] = {
		expr.otherwise(result);
		return this;
	}
	
	/**
	 * Ascending ordering for this expression.
	 */
	def asc : Order = {
		return queryBuilder.criteriaBuilder.asc(expr);		
	}
	
	/**
	 * Descending ordering for this expression.
	 */
	def desc : Order = {
		return queryBuilder.criteriaBuilder.desc(expr);
	}
}
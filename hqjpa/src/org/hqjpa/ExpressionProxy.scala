package org.hqjpa

import scala.collection.JavaConverters.asJavaCollectionConverter

import javax.persistence.criteria.Expression
import javax.persistence.criteria.Order
import javax.persistence.criteria.Selection
import scala.runtime.ScalaWholeNumberProxy

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object ExpressionProxy {
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
		src : ExpressionProxy[VALUE] 
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
	implicit def toStringExtensions(src : ExpressionProxy[String]) : StringExtensions = {		
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
	implicit def toGenericExtensions[VALUE](src : ExpressionProxy[VALUE]) : GenericExtensions[VALUE] = {		
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
	implicit def toNumberExtensions[VALUE <: Number](src : ExpressionProxy[VALUE]) : NumberExtensions[VALUE] = {		
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
	implicit def toFloatExtensions(src : ExpressionProxy[java.lang.Float]) : FloatExtensions = {		
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
	implicit def toIntegerExtensions(src : ExpressionProxy[java.lang.Integer]) : IntegerExtensions = {		
		val extensions = new IntegerExtensions(src.expr, src.queryBuilder);
		
		//
		return extensions;
	}
	
	/**
	 * Implicit type converter to add operators in OperatorExtensions.BooleanExtensions to 
	 * proxies for expressions.
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toBooleanExtensions(src : ExpressionProxy[java.lang.Boolean]) : BooleanExtensions = {		
		val extensions = new BooleanExtensions(src.expr, src.queryBuilder);
		
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
		override val expr : Expression[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		ExpressionProxy[VALUE](expr, queryBuilder) with
		OperatorExtensions.ComparableExtensions[VALUE]
	{		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => expr };
		
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
		override val expr : Expression[String],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		ExpressionProxy[String](expr, queryBuilder) with
		OperatorExtensions.StringExtensions
	{
		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[String]) = { () => expr };
		
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
		override val expr : Expression[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		ExpressionProxy[VALUE](expr, queryBuilder) with
		OperatorExtensions.GeneralExtensions[VALUE]
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => expr };		
		
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
		override val expr : Expression[VALUE],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		ExpressionProxy[VALUE](expr, queryBuilder) with
		OperatorExtensions.NumberExtensions[VALUE]
	{		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => expr };
		
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
		override val expr : Expression[java.lang.Float],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		ExpressionProxy[java.lang.Float](expr, queryBuilder) with
		OperatorExtensions.FloatExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Float]) = { () => expr };
		
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
		override val expr : Expression[java.lang.Integer],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		ExpressionProxy[java.lang.Integer](expr, queryBuilder) with
		OperatorExtensions.IntegerExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Integer]) = { () => expr };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def int = this;		
	}
	
	/**
	 * Extensions for expressions over boolean values.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param expr CriteriaBuilder.Case being proxied.
	 * @param queryBuilder Host query builder.
	 */
	class BooleanExtensions(
		override val expr : Expression[java.lang.Boolean],
		override val queryBuilder : IQueryBuilder
	) 
	extends 
		ExpressionProxy[java.lang.Boolean](expr, queryBuilder) with
		OperatorExtensions.BooleanExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Boolean]) = { () => expr };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def bool = this;		
	}
}

/**
 * Proxy for expressions.<br/> 
 * <br/>
 * Uses in WHERE and HAVING clauses are enabled by implicit cast to one of extension classes
 * defined in companion object. <br/>
 * <br/>
 * Uses in results sets are enabled by implicit cast to VALUE, exposing actual value of the
 * expression.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param VALUE Type of underlying value of expression being proxied.
 *
 * @param expr Expression being proxied.
 * @param queryBuilder Host query builder.
 */
class ExpressionProxy[VALUE](		
		val expr : Expression[VALUE],
		val queryBuilder : IQueryBuilder
	) 
	extends 
	ISelectionProvider[VALUE]  with
	IExpressionProvider[VALUE]
{
	
	override def __getSelection() : Selection[VALUE] = {
		return expr;
	}
	
	override def __getExpression() : Expression[VALUE] = {
		return expr;
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
	
	/**
	 * Derive ordering from this expression.
	 * @param asc True to derive ascending ordering, false to derive descending ordering.
	 * @return Ordering derived.
	 */
	def order(asc : Boolean) : Order = {
		return (if( asc ) this.asc else this.desc);
	}
}
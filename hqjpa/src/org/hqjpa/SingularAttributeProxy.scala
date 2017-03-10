package org.hqjpa

import scala.collection.JavaConverters.asJavaCollectionConverter

import javax.persistence.metamodel.SingularAttribute
import javax.persistence.criteria.Selection
import javax.persistence.criteria.Path
import javax.persistence.criteria.Order
import javax.persistence.criteria.Expression
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression


/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object SingularAttributeProxy {
	/**
	 * Implicit type converter to add extensions in ComparableConditionExtensions to singular 
	 * attribute proxies for attributes of comparable types.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toComparableExtensions[OWNER, VALUE <: Comparable[VALUE]](
				src : SingularAttributeProxy[OWNER, VALUE] 
			) : ComparableExtensions[OWNER, VALUE] = {
		
		val extensions = new ComparableExtensions[OWNER, VALUE](src.path, src.parentEntityProxy);
		
		//
		return extensions;
	}
	
	/**
	 * Implicit type converter to add extensions in StringConditionExtensions to singular 
	 * attribute proxies for attributes of string types.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toStringExtensions[OWNER](
				src : SingularAttributeProxy[OWNER, String] 
			) : StringExtensions[OWNER] = {
		
		val extensions = new StringExtensions[OWNER](src.path, src.parentEntityProxy);
	
		//
		return extensions;
	}
	
	/**
	 * Implicit type converter to add operators in GenericExtensions to singular 
	 * attribute proxies for attributes.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toGenericExtensions[OWNER, VALUE](
				src : SingularAttributeProxy[OWNER, VALUE] 
			) : GenericExtensions[OWNER, VALUE] = {
		
		val extensions = new GenericExtensions[OWNER, VALUE](src.path, src.parentEntityProxy);
		
		//
		return extensions;
	}	
	
	/**
	 * Implicit type converter to add operators in NumberExtensions to singular 
	 * attribute proxies for attributes.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toNumberExtensions[OWNER, VALUE <: Number](
				src : SingularAttributeProxy[OWNER, VALUE] 
			) : NumberExtensions[OWNER, VALUE] = {
		
		val extensions = new NumberExtensions[OWNER, VALUE](src.path, src.parentEntityProxy);
		
		//
		return extensions;
	}	
	
	/**
	 * Implicit type converter to add operators in FloatExtensions to singular 
	 * attribute proxies for attributes.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toFloatExtensions[OWNER](
				src : SingularAttributeProxy[OWNER, java.lang.Float] 
			) : FloatExtensions[OWNER] = {
		
		val extensions = new FloatExtensions[OWNER](src.path, src.parentEntityProxy);
		
		//
		return extensions;
	}	
	
	/**
	 * Implicit type converter to add operators in IntegerExtensions to singular 
	 * attribute proxies for attributes.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toIntegerExtensions[OWNER](
				src : SingularAttributeProxy[OWNER, java.lang.Integer] 
			) : IntegerExtensions[OWNER] = {
		
		val extensions = new IntegerExtensions[OWNER](src.path, src.parentEntityProxy);
		
		//
		return extensions;
	}	
	
	/**
	 * Implicit type converter to add operators in OperatorExtensions.BooleanExtensions to 
	 * proxies for expressions.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * 
	 * @param src Instance being converted.
	 * @return Extending instance.
	 */
	implicit def toBooleanExtensions[OWNER](
				src : SingularAttributeProxy[OWNER, java.lang.Boolean]
			) : BooleanExtensions[OWNER] = {		
		
		val extensions = new BooleanExtensions[OWNER](src.path, src.parentEntityProxy);
		
		//
		return extensions;
	}
	
	
	/**
	 * Extensions for proxies over comparable attributes.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * @param VALUE Type of underlying attribute of attribute being proxied.

	 *
	 * @param path Path of the attribute relative to the actual root in the query.
	 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
	 */
	class ComparableExtensions[OWNER, VALUE <: Comparable[VALUE]](
		override val path : Path[VALUE],
		override val parentEntityProxy : EntityProxy[_, _, _]
	) extends 
		SingularAttributeProxy[OWNER, VALUE](parentEntityProxy) with
		OperatorExtensions.ComparableExtensions[VALUE]
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => path };	
		
		/**
		 * Allows forcing comparable extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def cmp = this;		
	}
	
	/**
	 * Additional extensions for proxies over string attributes.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 *
	 * @param path Path of the attribute relative to the actual root in the query.
	 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
	 */
	class StringExtensions[OWNER](
		override val path : Path[String],
		override val parentEntityProxy : EntityProxy[_, _, _]
	) 
	extends 
		SingularAttributeProxy[OWNER, String](parentEntityProxy) with
		OperatorExtensions.StringExtensions
	{		
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[String]) = { () => path };
		
		/**
		 * Allows forcing string extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def str = this;		
	}
	
	/**
	 * Extensions involving generic functions.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 *
	 * @param path Path of the attribute relative to the actual root in the query.
	 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
	 */
	class GenericExtensions[OWNER, VALUE](
		override val path : Path[VALUE],
		override val parentEntityProxy : EntityProxy[_, _, _]
	) 
	extends 
		SingularAttributeProxy[OWNER, VALUE](parentEntityProxy) with
		OperatorExtensions.GeneralExtensions[VALUE]
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => path };	
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def gen = this;				
	}
	
	/**
	 * Extensions for attributes over numbers.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 * @param VALUE Type of underlying attribute of attribute being proxied.
	 *
	 * @param path Path of the attribute relative to the actual root in the query.
	 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
	 */
	class NumberExtensions[OWNER, VALUE <: Number](
		override val path : Path[VALUE],
		override val parentEntityProxy : EntityProxy[_, _, _]
	) 
	extends 
		SingularAttributeProxy[OWNER, VALUE](parentEntityProxy) with
		OperatorExtensions.NumberExtensions[VALUE]
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[VALUE]) = { () => path };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def num = this;		
	}
	
	/**
	 * Extensions for attributes over float numbers.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 *
	 * @param path Path of the attribute relative to the actual root in the query.
	 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
	 */
	class FloatExtensions[OWNER](
		override val path : Path[java.lang.Float],
		override val parentEntityProxy : EntityProxy[_, _, _]
	) 
	extends 
		SingularAttributeProxy[OWNER, java.lang.Float](parentEntityProxy) with
		OperatorExtensions.FloatExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Float]) = { () => path };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def flt = this;
		
		
	}
	
	/**
	 * Extensions for attributes over integer numbers.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 *
	 * @param path Path of the attribute relative to the actual root in the query.
	 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
	 */
	class IntegerExtensions[OWNER](
		override val path : Path[java.lang.Integer],
		override val parentEntityProxy : EntityProxy[_, _, _]
	) 
	extends 
		SingularAttributeProxy[OWNER, java.lang.Integer](parentEntityProxy) with
		OperatorExtensions.IntegerExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Integer]) = { () => path };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def int = this;			
	}
	
	/**
	 * Extensions for attributes over boolean values.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 * 
	 * @param OWNER Type of owner of the attribute being proxied.
	 *
	 * @param path Path of the attribute relative to the actual root in the query.
	 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
	 */
	class BooleanExtensions[OWNER](
		override val path : Path[java.lang.Boolean],
		override val parentEntityProxy : EntityProxy[_, _, _]
	) 
	extends 
		SingularAttributeProxy[OWNER, java.lang.Boolean](parentEntityProxy) with
		OperatorExtensions.BooleanExtensions
	{
		/** Extractor for left side of the expression. */
		override val __leftSideExpr : (() => Expression[java.lang.Boolean]) = { () => path };
		
		/**
		 * Allows forcing aggregate extensions on compatible attribute proxies in 
		 * scopes having ambiguous implicit conversions.
		 */
		def bool = this;			
	}
}

/**
 * Singular attribute proxy, only for non entity types.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param OWNER Type of owning entity when entity proxy is used to define entity attribute.
 * @param VALUE Type of underlying non-entity.
 * 
 * @param parentEntityProxy Parent entity proxy. Set by the entity proxy providing the attribute.
 */
class SingularAttributeProxy[OWNER, VALUE](
		val parentEntityProxy : EntityProxy[_, _, _]
	) 
	extends 
		ISelectionProvider[VALUE] with
		IExpressionProvider[VALUE]
{
	//initializer
	{
		//validate constructor inputs
		assert(parentEntityProxy != null, "Constructor argument 'parentEntityProxy' is null.");
	}
	
	/**
	 * Host query builder. Taken from parent entity proxy.
	 */
	def queryBuilder : IQueryBuilder = {
		return parentEntityProxy.__queryBuilder;
	}
	
	/** 
	 * Related JPA path. For this to be defined, parent entity proxy must have 
	 * either root or path defined. 
	 */
	def path : Path[VALUE] = {
		//if parent entity proxy has neither 'root' nor 'path', path can not be derived
		if( parentEntityProxy.__root.isEmpty && parentEntityProxy.__path.isEmpty ) {
			val msg = 
				"Parent entity proxy has neither 'root' nor 'path' defined. " +
				"Unable to derive path for subsequent entity proxy. " +
				"This probably means that you are trying to use meta-data entity directly for attribute selection. " +
				"Derive a query root by using from() or join(), and use that.";
			throw new AssertionError(msg);
		}

		//if parent entity proxy is JPA root, use JPA root to get path of this entity proxy
		if( parentEntityProxy.__root.isDefined ) {			
			val path = parentEntityProxy
				.__root.get
				.get(attribute.asInstanceOf[SingularAttribute[Any, Any]]);
			
			return path.asInstanceOf[Path[VALUE]];
		}				
		
		//if parent entity proxy is JPA path, use JPA path to get path of this entity proxy
		if( parentEntityProxy.__path.isDefined ) {
			val path = parentEntityProxy
				.__path.get
				.get(attribute.asInstanceOf[SingularAttribute[Any, Any]]);				
			
			return path.asInstanceOf[Path[VALUE]];
		}
		
		//	
		throw new AssertionError("Should not reach this line.");
	};
	
	/** 
	 * Singular attribute being proxied. Override when creating new instance.
	 */
	def attribute : SingularAttribute[OWNER, VALUE] = {
		throw new NotImplementedError("Must be overriden and implemented when creating new instance.");
	}
	
	/**
	 * Ascending ordering for this attribute.
	 */
	def asc : Order = {
		return queryBuilder.criteriaBuilder.asc(path);		
	}
	
	/**
	 * Descending ordering for this attribute.
	 */
	def desc : Order = {
		return queryBuilder.criteriaBuilder.desc(path);
	}
	
	/**
	 * Derive ordering from this attribute.
	 * @param asc True to derive ascending ordering, false to derive descending ordering.
	 * @return Ordering derived.
	 */
	def order(asc : Boolean) : Order = {
		return (if( asc ) this.asc else this.desc);
	}
	
	/**
	 * Creates a pair from underlying JPA path of the attribute being proxied and 
	 * a given value. Is used in update queries to produce attribute setters.
	 * @param value Value to add to the pair.
	 * @return A pair of (this.path, value).
	 */
	def ->(value : VALUE) : (Path[Any], Any) = {
		return ((path.asInstanceOf[Path[Any]], value.asInstanceOf[Any]));
	}
	
	/**
	 * Creates a pair from underlying JPA path of the attribute being proxied and 
	 * a given value. Is used in update queries to produce attribute setters.
	 * @param value Value to add to the pair.
	 * @return A pair of (this.path, value).
	 */
	def ->(value : ExpressionProxy[VALUE]) : (Path[Any], Any) = {
		return ((path.asInstanceOf[Path[Any]], value.expr.asInstanceOf[Any]));
	}
	
	/**
	 * Creates a pair from underlying JPA path of the attribute being proxied and 
	 * a given value. Is used in update queries to produce attribute setters.
	 * @param value Value to add to the pair.
	 * @return A pair of (this.path, value).
	 */
	def ->(value : PredicateProxy) : (Path[Any], Any) = {
		return ((path.asInstanceOf[Path[Any]], value.predicate.asInstanceOf[Any]));
	}
	
	override def __getSelection() : Selection[VALUE] = {
		return path;
	}	
	
	override def __getExpression() : Expression[VALUE] = {
		return path;
	}	
}
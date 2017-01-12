package org.hqjpa

import javax.persistence.criteria.From
import javax.persistence.criteria.Path
import javax.persistence.criteria.Selection
import javax.persistence.metamodel.SingularAttribute
import javax.persistence.metamodel.SetAttribute
import javax.persistence.criteria.Expression

/**
 * Used to provide roots, paths and selections.<br/>
 * <br/>
 * Static members are thread safe, instance members are not
 * 
 * @param OWNER Type of owning entity when entity proxy is used to define entity attribute.
 * @param ENTITY Type of underlying entity.
 * @param SELF Type of deriving entity proxy class. Required for Scala type inference to be able to see deriving class correctly.
 * 
 * @param __entityClass Class of underlying entity. Set when deriving.
 * @param __parentEntityProxy Parent entity proxy, set when entity proxy is used to define entity attribute.
 */
class EntityProxy[OWNER, ENTITY, SELF](
		val __entityClass : Class[ENTITY],
		val __parentEntityProxy : Option[EntityProxy[_, OWNER, _]]
	) 
	extends 
		ISelectionProvider[ENTITY]	with
		IExpressionProvider[ENTITY] with
	 	Cloneable 
{	
	/** Host query builder. Set by query builder when deriving proxies over roots and joins. */
	private var __mQueryBuilder : IQueryBuilder = _;
	
	/** Host query builder. Getter. Proxies to query builder of parent entity proxy if no local query builder is set. */
	def __queryBuilder : IQueryBuilder = {
		//local query builder not set? proxy to one in parent entity proxy
		if( __mQueryBuilder == null ) {
			//proxy
			if( __parentEntityProxy.isDefined ) {
				return __parentEntityProxy.get.__queryBuilder;
			}
			//parent entity proxy not defined? no query builder
			else {
				return null;
			}
		}
		//return local query builder
		else {
			return __mQueryBuilder;
		}		
	}
	
	/** Host query builder. Setter. */ 
	def __queryBuilder_=(value : IQueryBuilder) : Unit = {
		__mQueryBuilder = value;
	}
	
	/** Related JPA query root. Set if proxy is used to provide query root. */
	var __root : Option[From[_, ENTITY]] = None;
	
	/** 
	 * Related JPA path. For this to be defined, parent entity proxy must be set 
	 * and have either root or path defined. Also, this entity proxy must have
	 * singular attribute defined.
	 */
	def __path : Option[Path[ENTITY]] = {
		//if parent entity proxy is not defined, path can not be derived
		if( __parentEntityProxy.isEmpty ) {
			return None;
		}
		
		//if this is not proxy over singular attribute, path can not be derived
		if( __singularAttribute.isEmpty ) {
			return None;
		}
		
		//if parent entity proxy has neither 'root' nor 'path', path can not be derived
		if( __parentEntityProxy.get.__root.isEmpty && __parentEntityProxy.get.__path.isEmpty ) {
			val msg = 
				"Parent entity proxy has neither 'root' nor 'path' defined. " +
				"Unable to derive path for subsequent entity proxy. " +
				"This probably means that you are trying to use meta-data entity directly for selection. " +
				"Derive a query root by using from() or join(), and use that.";
			throw new AssertionError(msg);
		}

		//if parent entity proxy is JPA root, use JPA root to get path of this entity proxy
		if( __parentEntityProxy.get.__root.isDefined ) {			
			val path = Some(
					__parentEntityProxy.get
						.__root.get
						.get(__singularAttribute.get)
				);
			
			return path;
		}				
		
		//if parent entity proxy is JPA path, use JPA path to get path of this entity proxy
		if( __parentEntityProxy.get.__path.isDefined ) {
			val path = Some(
					__parentEntityProxy.get
						.__path.get
						.get(__singularAttribute.get)
				);
			
			return path;
		}
		
		//	
		throw new AssertionError("Should not reach this line.");
	};
	
	/**
	 * Override when creating new instance, if proxy is used to provide singular attribute.
	 */
	def __singularAttribute : Option[SingularAttribute[OWNER, ENTITY]] = { 
		return None;
	}
	
	/**
	 * Override when creating new instance, if proxy is used to provide set attribute. 
	 */
	def __setAttribute : Option[SetAttribute[OWNER, ENTITY]] = { 
		return None; 
	}
	
	/**
	 * Shallow cloning. Used to make copies of empty proxy for building derived proxies 
	 * over query roots and query paths.
	 * @return A shallow copy of this instance.
	 */
	override def clone() : AnyRef = {
		return super.clone();
	}
	
	/**
	 * Creates a pair from underlying JPA path of the attribute being proxied and 
	 * a given value. Is used in update queries to produce attribute setters.
	 * @param value Value to add to the pair.
	 * @return A pair of (this.path, value).
	 */
	def ->(value : ENTITY) : (Path[Any], Any) = {
		return ((__path.get.asInstanceOf[Path[Any]], value.asInstanceOf[Any]));
	}
	
	/**
	 * Creates a pair from underlying JPA path of the attribute being proxied and 
	 * a given value. Is used in update queries to produce attribute setters.
	 * @param value Value to add to the pair.
	 * @return A pair of (this.path, value).
	 */
	def ->(value : ExpressionProxy[ENTITY]) : (Path[Any], Any) = {
		return ((__path.get.asInstanceOf[Path[Any]], value.expr.asInstanceOf[Any]));
	}
	
	override def __getSelection() : Selection[ENTITY] = {
		if( __root.isDefined ) {
			return __root.get;
		}
		
		if( __path.isDefined ) {
			return __path.get;
		}
		
		val msg = "Neither 'from' nor 'path' are defined. Unable to extract 'Selection'."; 
		throw new Error(msg);
	}
	
	override def __getExpression() : Expression[ENTITY] = {
		if( __root.isDefined ) {
			return __root.get;
		}
		
		if( __path.isDefined ) {
			return __path.get;
		}
		
		val msg = "Neither 'from' nor 'path' are defined. Unable to extract 'Selection'."; 
		throw new Error(msg);
	}
}
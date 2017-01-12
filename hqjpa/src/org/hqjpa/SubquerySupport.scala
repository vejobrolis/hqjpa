package org.hqjpa

import scala.reflect.ClassTag
import javax.persistence.criteria.Subquery
import javax.persistence.criteria.AbstractQuery
import javax.persistence.criteria.Root
import javax.persistence.criteria.CommonAbstractCriteria

/**
 * Is used to add support for sub-queries to SelectQueryBuilder and SelectSuqueryBuilder while
 * avoiding code duplication.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
trait SubquerySupport { self : IQueryBuilder => 
	/** Host JPA query. */
	def jpaQuery : CommonAbstractCriteria;
	
	/**
	 * Define subquery with no correlation to any root of the parent query.
	 *	
	 * @param RESULT Subquery result type.
	 * 
	 * @param builder Subquery builder.
	 * @return Subquery proxy.
	 */
	def subquery[RESULT : ClassTag](builder : (SelectSubqueryBuilder) => IExpressionProvider[RESULT]) : SubqueryProxy[RESULT] = {
		//create JPA subquery over expected result type
		val valueClassTag = implicitly[ClassTag[RESULT]];
		val jpaSubquery = jpaQuery.subquery(valueClassTag.runtimeClass.asInstanceOf[Class[RESULT]]);
		
		//create subquery builder
		val subquery = new SelectSubqueryBuilder(this, jpaSubquery.asInstanceOf[Subquery[Any]]);
		
		//build subquery
		val result = builder(subquery);
		
		//create subquery proxy
		val subqueryProxy = new SubqueryProxy(jpaSubquery, this);
		
		//
		return subqueryProxy;
	}
	
	/**
	 * Define subquery with correlation to 1 root of parent query.
	 * 
	 * @param RESULT Subquery result type.
	 * 
	 * @param root0 Root of parent query to correlate and expose in subquery.
	 * @param builder Subquery builder.
	 * @return Subquery proxy.
	 */
	def subquery
		[
			RESULT : ClassTag,
			EP0[_, ENTITY0, ROOT0] <: EntityProxy[_, ENTITY0, ROOT0], ENTITY0, ROOT0
		]
		(
			root0 : EP0[_, ENTITY0, ROOT0]
		)
		(
			builder : (SelectSubqueryBuilder, ROOT0) => IExpressionProvider[RESULT]
		) 
		: SubqueryProxy[RESULT] = 
	{
		//validate roots
		assert(
			root0.__root.isDefined && root0.__root.get.isInstanceOf[Root[_]], 
			"Argument 'root0' does not represent a query root. Note that joins over singular " +
			"attributes are not query roots and can't be correlated. Just use them directly."
		);
		
		//create JPA subquery over expected result type
		val valueClassTag = implicitly[ClassTag[RESULT]];
		val jpaSubquery = jpaQuery.subquery(valueClassTag.runtimeClass.asInstanceOf[Class[RESULT]]);
		
		//create subquery builder
		val subquery = new SelectSubqueryBuilder(this, jpaSubquery.asInstanceOf[Subquery[Any]]);
		
		//create correlated query roots
		val jpaRoot0 = jpaSubquery.correlate(root0.__root.get.asInstanceOf[Root[ENTITY0]]);
		val root0Corr = root0.clone().asInstanceOf[EP0[_, ENTITY0, ROOT0]];
		root0Corr.__root = Some(jpaRoot0);
		root0Corr.__queryBuilder = subquery;
		
		//build subquery
		val result = builder(subquery, root0Corr.asInstanceOf[ROOT0]);
		
		//create subquery proxy
		val subqueryProxy = new SubqueryProxy(jpaSubquery, this);
		
		//
		return subqueryProxy;
	}
	
	/**
	 * Define subquery with correlation to 2 roots of parent query.
	 * 
	 * @param RESULT Subquery result type.
	 * 
	 * @param root0 Root of parent query to correlate and expose in subquery.
	 * @param root1 Root of parent query to correlate and expose in subquery.
	 * @param builder Subquery builder.
	 * @return Subquery proxy.
	 */
	def subquery
		[
			RESULT : ClassTag,
			EP0[_, ENTITY0, ROOT0] <: EntityProxy[_, ENTITY0, ROOT0], ENTITY0, ROOT0,
			EP1[_, ENTITY1, ROOT1] <: EntityProxy[_, ENTITY1, ROOT1], ENTITY1, ROOT1
		]
		(
			root0 : EP0[_, ENTITY0, ROOT0],
			root1 : EP1[_, ENTITY1, ROOT1]
		)
		(
			builder : (SelectSubqueryBuilder, ROOT0, ROOT1) => IExpressionProvider[RESULT]
		) 
		: SubqueryProxy[RESULT] = 
	{
		//validate roots
		assert(
			root0.__root.isDefined && root0.__root.get.isInstanceOf[Root[_]], 
			"Argument 'root0' does not represent a query root. Note that joins over singular " +
			"attributes are not query roots and can't be correlated. Just use them directly."
		);
		assert(
			root1.__root.isDefined && root1.__root.isInstanceOf[Root[_]], 
			"Argument 'root1' does not represent a query root. Note that joins over singular " +
			"attributes are not query roots and can't be correlated. Just use them directly."
		);
		
		//create JPA subquery over expected result type
		val valueClassTag = implicitly[ClassTag[RESULT]];
		val jpaSubquery = jpaQuery.subquery(valueClassTag.runtimeClass.asInstanceOf[Class[RESULT]]);
		
		//create subquery builder
		val subquery = new SelectSubqueryBuilder(this, jpaSubquery.asInstanceOf[Subquery[Any]]);
		
		//create correlated query roots
		val jpaRoot0 = jpaSubquery.correlate(root0.__root.get.asInstanceOf[Root[ENTITY0]]);
		val root0Corr = root0.clone().asInstanceOf[EP0[_, ENTITY0, ROOT0]];
		root0Corr.__root = Some(jpaRoot0);
		root0Corr.__queryBuilder = subquery;
		
		val jpaRoot1 = jpaSubquery.correlate(root1.__root.get.asInstanceOf[Root[ENTITY1]]);
		val root1Corr = root1.clone().asInstanceOf[EP1[_, ENTITY1, ROOT1]];
		root1Corr.__root = Some(jpaRoot1);
		root1Corr.__queryBuilder = subquery;
		
		//build subquery
		val result = builder(
				subquery, 
				root0Corr.asInstanceOf[ROOT0],
				root1Corr.asInstanceOf[ROOT1]
			);
		
		//create subquery proxy
		val subqueryProxy = new SubqueryProxy(jpaSubquery, this);
		
		//
		return subqueryProxy;
	}
}
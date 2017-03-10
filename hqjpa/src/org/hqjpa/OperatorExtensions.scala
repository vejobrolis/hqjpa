package org.hqjpa

import scala.collection.JavaConverters._
import javax.persistence.criteria.Expression

/**
 * Reusable traits for adding operator extensions to attribute and expression proxies.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object OperatorExtensions {
	/**
	 * Extensions for proxies over comparable types.
	 * 
	 * @param VALUE Type of VALUE being proxied.
	 */
	trait ComparableExtensions[VALUE <: Comparable[VALUE]] {
		/** Extractor for left side of the expression. */
		val __leftSideExpr : () => Expression[VALUE];
		
		/** Query builder to use. */
		def queryBuilder : IQueryBuilder;
		
		/**
		 * Get predicate proxy telling if attribute is IN given value set.
		 * @param values Values to test against.
		 * @return A corresponding predicate proxy.	
		 */
		def in(values : VALUE*) : PredicateProxy = {
			//if value list is empty generate expression "1=2" instead of "x IN ()" which fails in execution
			if( values.size == 0 ) {
				val cb = queryBuilder.criteriaBuilder;
				val pred = cb.equal(cb.literal(1), cb.literal(2));
				val proxy = new PredicateProxy(pred, queryBuilder);
				
				//
				return proxy;
			}
			//value list is non-empty, generate normal IN expression
			else {
				val valuesCollection = values.asJavaCollection;			
				val predicate = __leftSideExpr().in(valuesCollection);
				
				val proxy = new PredicateProxy(predicate, queryBuilder);
				
				//
				return proxy;
			}
		}
		
		/**
		 * Get predicate proxy telling if attribute is IN given value set.
		 * @param values Expresion returning values to test against.
		 * @return A corresponding predicate proxy.	
		 */
		def in(values : IExpressionProvider[VALUE]) : PredicateProxy = {
			val predicate = __leftSideExpr().in(values.__getExpression());			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if value of attribute is BETWEEN two given values.
		 * @param start Starting value.
		 * @param end Ending value.
		 * @return A corresponding predicate proxy.
		 */
		def between(start : VALUE, end : VALUE) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.between(__leftSideExpr(), start, end);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute IS NULL.
		 * @return A corresponding predicate proxy.
		 */
		def isNull : PredicateProxy = {
			val predicate = __leftSideExpr().isNull();			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute IS NOT NULL.
		 * @return A corresponding predicate proxy.
		 */
		def isNotNull : PredicateProxy = {
			val predicate = __leftSideExpr().isNotNull();			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
				
		/**
		 * Get predicate proxy telling if attribute equals given value.
		 * @param value Value to test against.
		 * @return A corresponding predicate proxy.
		 */
		def ===(value : VALUE) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.equal(__leftSideExpr(), value);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if this attribute equals given attribute.
		 * @param other Expression to test against.
		 * @return A corresponding predicate proxy.
		 */
		def ===(other : IExpressionProvider[VALUE]) : PredicateProxy = {			
			val predicate = queryBuilder.criteriaBuilder.equal(
						__leftSideExpr(), other.__getExpression()
					);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute is not equal to given value.
		 * @param value Value to test against.
		 * @return A corresponding predicate proxy.
		 */
		def =!=(value : VALUE) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.notEqual(__leftSideExpr(), value);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if this attribute is not equal to given attribute.
		 * @param other Expression to test against.
		 * @return A corresponding predicate proxy.
		 */
		def =!=[OTHER_OWNER, OTHER_META](other : IExpressionProvider[VALUE]) : PredicateProxy = {			
			val predicate = queryBuilder.criteriaBuilder.notEqual(
						__leftSideExpr(), other.__getExpression()
					);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute is grater than given value.
		 * @param value Value to test against.
		 * @return A corresponding predicate proxy.
		 */
		def >(value : VALUE) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.greaterThan(__leftSideExpr(), value);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		
		/**
		 * Get predicate proxy telling if this attribute is greater than given attribute.
		 * @param other Expression to test against.
		 * @return A corresponding predicate proxy.
		 */
		def >(other : IExpressionProvider[VALUE]) : PredicateProxy = {			
			val predicate = queryBuilder.criteriaBuilder.greaterThan(
						__leftSideExpr(), other.__getExpression()
					);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute is greater or equal than given value.
		 * @param value Value to test against.
		 * @return A corresponding predicate proxy.
		 */
		def >=(value : VALUE) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.greaterThanOrEqualTo(__leftSideExpr(), value);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if this attribute is greater or equal to given attribute.
		 * @param other Expression to test against.
		 * @return A corresponding predicate proxy.
		 */
		def >=(other : IExpressionProvider[VALUE]) : PredicateProxy = {			
			val predicate = queryBuilder.criteriaBuilder.greaterThanOrEqualTo(
						__leftSideExpr(), other.__getExpression()
					);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute is lesser than given value.
		 * @param value Value to test against.
		 * @return A corresponding predicate proxy.
		 */
		def <(value : VALUE) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.lessThan(__leftSideExpr(), value);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if this attribute is lesser than given attribute.
		 * @param other Expression to test against.
		 * @return A corresponding predicate proxy.
		 */
		def <(other : IExpressionProvider[VALUE]) : PredicateProxy = {
			
			val predicate = queryBuilder.criteriaBuilder.lessThanOrEqualTo(
						__leftSideExpr(), other.__getExpression()
					);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute is lesser than or equal to given value.
		 * @param value Value to test against.
		 * @return A corresponding predicate proxy.
		 */
		def <=(value : VALUE) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.lessThanOrEqualTo(__leftSideExpr(), value);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if this attribute is lesser than or equal to given attribute.
		 * @param other Expression to test against.
		 * @return A corresponding predicate proxy.
		 */
		def <=(other : IExpressionProvider[VALUE]) : PredicateProxy = {			
			val predicate = queryBuilder.criteriaBuilder.lessThanOrEqualTo(
						__leftSideExpr(), other.__getExpression()
					);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get MAX(...) expression on attribute or expression.
		 * @return A corresponding expression proxy.
		 */
		def greatest : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.greatest(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get MIN(...) expression on attribute or expression.
		 * @return A corresponding expression proxy.
		 */
		def least : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.least(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
	}
	
	/**
	 * Extensions for proxies over string types.
	 */
	trait StringExtensions {
		/** Extractor for left side of the expression. */
		val __leftSideExpr : () => Expression[String];
		
		/** Query builder to use. */
		def queryBuilder : IQueryBuilder;
		
		/**
		 * Get predicate proxy telling if attribute is like given string expression.
		 * @param value Value to test against.
		 * @return A corresponding predicate proxy.
		 */
		def like(value : String) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.like(__leftSideExpr(), value);			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get predicate proxy telling if attribute is like given string expression.
		 * @param other Expression to test against.
		 * @return A corresponding predicate proxy.
		 */
		def like(other : IExpressionProvider[String]) : PredicateProxy = {
			val predicate = queryBuilder.criteriaBuilder.like(__leftSideExpr(), other.__getExpression());			
			val proxy = new PredicateProxy(predicate, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get expression proxy for string length.
		 * @return A corresponding expression proxy.
		 */
		def length : ExpressionProxy[java.lang.Integer] = {
			val expr = queryBuilder.criteriaBuilder.length(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Concatenate this expression with given string. Cannot use just "+" because
		 * it likely clashes with any2StringAdd implicit and compiler does not find
		 * the right operator when expression is used as argument.
		 * @param other StringString to concatenate with.
		 * @return Resulting expression proxy.
		 */
		def :+(other : String) : ExpressionProxy[String] = {
			val expr = queryBuilder.criteriaBuilder.concat(__leftSideExpr(), other);
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Concatenate this expression with given string expression. Cannot use just "+" because
		 * it likely clashes with any2StringAdd implicit and compiler does not find
		 * the right operator when expression is used as argument.
		 * @param other String epxression to concatenate with.
		 * @return Resulting expression proxy.
		 */
		def :+(other : IExpressionProvider[String]): ExpressionProxy[String] = {
			val expr = queryBuilder.criteriaBuilder.concat(__leftSideExpr(), other.__getExpression());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Concatenate given string with this expression (right associative version
		 * of "+"). 
		 * @param other String expression to concatenate with.
		 * @return Resulting expression proxy.
		 */
		def +:(other : String) : ExpressionProxy[String] = {
			val expr = queryBuilder.criteriaBuilder.concat(other, __leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
	}
	
	/**
	 * Extensions for proxies over general types.
	 * 
	 * @param VALUE Type of VALUE being proxied.
	 */
	trait GeneralExtensions[VALUE] {
		/** Extractor for left side of the expression. */
		val __leftSideExpr : () => Expression[VALUE];
		
		/** Query builder to use. */
		def queryBuilder : IQueryBuilder;
		
		/**
		 * Create a COUNT expression on this attribute or expression.
		 * @return A proxy for the expression.
		 */
		def count : ExpressionProxy[java.lang.Long] = {
			val expr = queryBuilder.criteriaBuilder.count(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Create a COUNT(DISTINCT ...) expression on this attribute or expression.
		 * @return A proxy for the expression.
		 */
		def countDistinct : ExpressionProxy[java.lang.Long] = {
			val expr = queryBuilder.criteriaBuilder.countDistinct(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
	}
	
	/**
	 * Extensions for proxies over general types.
	 * 
	 * @param VALUE Type of VALUE being proxied.
	 */
	trait NumberExtensions[VALUE <: Number] {
		/** Extractor for left side of the expression. */
		val __leftSideExpr : () => Expression[VALUE];
		
		/** Query builder to use. */
		def queryBuilder : IQueryBuilder;
		
		/**
		 * Create a SUM expression on this attribute.
		 * @return A proxy for the expression.
		 */
		def sum : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.sum(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}		
		
		/**
		 * Type cast to big decimal.
		 * @return A proxy for type cast expression.
		 */
		def toBigDecimal : ExpressionProxy[java.math.BigDecimal] = {
			val tce = queryBuilder.criteriaBuilder.toBigDecimal(__leftSideExpr());
			val proxy = new ExpressionProxy(tce, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Type cast to big integer.
		 * @return A proxy for type cast expression.
		 */
		def toBigInteger : ExpressionProxy[java.math.BigInteger] = {
			val tce = queryBuilder.criteriaBuilder.toBigInteger(__leftSideExpr());
			val proxy = new ExpressionProxy(tce, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Type cast to double.
		 * @return A proxy for type cast expression.
		 */
		def toDouble : ExpressionProxy[java.lang.Double] = {
			val tce = queryBuilder.criteriaBuilder.toDouble(__leftSideExpr());
			val proxy = new ExpressionProxy(tce, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Type cast to float.
		 * @return A proxy for type cast expression.
		 */
		def toFloat : ExpressionProxy[java.lang.Float] = {
			val tce = queryBuilder.criteriaBuilder.toFloat(__leftSideExpr());
			val proxy = new ExpressionProxy(tce, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Type cast to integer.
		 * @return A proxy for type cast expression.
		 */
		def toInteger : ExpressionProxy[java.lang.Integer] = {
			val tce = queryBuilder.criteriaBuilder.toInteger(__leftSideExpr());
			val proxy = new ExpressionProxy(tce, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Type cast to long.
		 * @return A proxy for type cast expression.
		 */
		def toLong : ExpressionProxy[java.lang.Long] = {
			val tce = queryBuilder.criteriaBuilder.toLong(__leftSideExpr());
			val proxy = new ExpressionProxy(tce, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get MAX(...) expression on attribute or expression.
		 * @return A corresponding expression proxy.
		 */
		def max : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.max(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Get MIN(...) expression on attribute or expression.
		 * @return A corresponding expression proxy.
		 */
		def min : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.min(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Add given value to this expression.
		 * @param value Value to add.
		 * @return Proxy of resulting expression.
		 */
		def :+(value : VALUE) : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.sum(__leftSideExpr(), value);
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Add given value to this expression.
		 * @param value Value to add.
		 * @return Proxy of resulting expression.
		 */
		def :+(value : IExpressionProvider[VALUE]) : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.sum(__leftSideExpr(), value.__getExpression());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Subtract given value from this expression.
		 * @param value Value to subtract.
		 * @return Proxy of resulting expression.
		 */
		def :-(value : VALUE) : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.diff(__leftSideExpr(), value);
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Subtract this expression from given value.
		 * @param value Value to subtract from.
		 * @return Proxy of resulting expression.
		 */
		def -:(value : VALUE) : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.diff(value, __leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
		
		/**
		 * Subtract given value from this expression.
		 * @param value Value to subtract.
		 * @return Proxy of resulting expression.
		 */
		def :-(value : IExpressionProvider[VALUE]) : ExpressionProxy[VALUE] = {
			val expr = queryBuilder.criteriaBuilder.diff(__leftSideExpr(), value.__getExpression());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
	}
	
	/**
	 * Extensions for proxies over float type.
	 */
	trait FloatExtensions {
		/** Extractor for left side of the expression. */
		val __leftSideExpr : () => Expression[java.lang.Float];
		
		/** Query builder to use. */
		def queryBuilder : IQueryBuilder;
		
		/**
		 * Create a SUM expression on this attribute.
		 * @return A proxy for the expression.
		 */
		def sumAsDouble : ExpressionProxy[java.lang.Double] = {
			val expr = queryBuilder.criteriaBuilder.sumAsDouble(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}			
	}
	
	/**
	 * Extensions for proxies over integer type.
	 */
	trait IntegerExtensions {
		/** Extractor for left side of the expression. */
		val __leftSideExpr : () => Expression[java.lang.Integer];
		
		/** Query builder to use. */
		def queryBuilder : IQueryBuilder;
		
				/**
		 * Create a SUM expression on this attribute.
		 * @return A proxy for the expression.
		 */
		def sumAsLong : ExpressionProxy[java.lang.Long] = {
			val expr = queryBuilder.criteriaBuilder.sumAsLong(__leftSideExpr());
			val proxy = new ExpressionProxy(expr, queryBuilder);
			
			//
			return proxy;
		}
	}
	
	/**
	 * Extensions for proxies over boolean type.
	 */
	trait BooleanExtensions {
		/** Extractor for left side of the expression. */
		val __leftSideExpr : () => Expression[java.lang.Boolean];
		
		/** Query builder to use. */
		def queryBuilder : IQueryBuilder;
		
		/**
		 * Create a NOT expression on this attribute.
		 * @return A proxy for the resulting predicate.
		 */
		def unary_!() : PredicateProxy = {
			val notPredicate = queryBuilder.criteriaBuilder.not(__leftSideExpr());
			val proxy = new PredicateProxy(notPredicate, queryBuilder);
			
			//
			return proxy;
		}
	}
}
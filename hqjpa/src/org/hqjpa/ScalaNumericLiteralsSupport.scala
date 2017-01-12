package org.hqjpa

/**
 * Is used to add support for Scala numeric literals to query builders, while
 * avoiding code duplication. Scala numeric literals are not Comparable and thus 
 * have to be promoted to Comparable equivalents manually.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
trait ScalaNumericLiteralsSupport {
	/**
	 * Create query literal from given value.
	 * 
	 * @param value Type of literal value.
	 * 
	 * @param value Value to create the literal from.
	 * @return Expression proxy for the literal created.
	 */
	def literal[VALUE <: AnyRef](value : VALUE) : ExpressionProxy[VALUE];
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Byte) : ExpressionProxy[java.lang.Byte] = {
		return literal(value : java.lang.Byte);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Short) : ExpressionProxy[java.lang.Short] = {
		return literal(value : java.lang.Short);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Int) : ExpressionProxy[java.lang.Integer] = {
		return literal(value : java.lang.Integer);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Long) : ExpressionProxy[java.lang.Long] = {
		return literal(value : java.lang.Long);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Float) : ExpressionProxy[java.lang.Float] = {
		return literal(value : java.lang.Float);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Double) : ExpressionProxy[java.lang.Double] = {
		return literal(value : java.lang.Double);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Boolean) : ExpressionProxy[java.lang.Boolean] = {
		return literal(value : java.lang.Boolean);
	}
	
	/**
	 * Specialized version of literal[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def literal(value : Char) : ExpressionProxy[java.lang.Character] = {
		return literal(value : java.lang.Character);
	}
	
	/**
	 * Create query literal from given value. This is a shorthand for literal()
	 * that allows writing q(value) (assuming q is the name of query builder in
	 * some context).
	 * 
	 * @param value Type of literal value.
	 * 
	 * @param value Value to create the literal from.
	 * @return Expression proxy for the literal created.
	 */
	def apply[VALUE <: AnyRef](value : VALUE) : ExpressionProxy[VALUE] = {
		return literal(value);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */	
	def apply(value : Byte) : ExpressionProxy[java.lang.Byte] = {
		return apply(value : java.lang.Byte);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def apply(value : Short) : ExpressionProxy[java.lang.Short] = {
		return apply(value : java.lang.Short);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def apply(value : Int) : ExpressionProxy[java.lang.Integer] = {
		return apply(value : java.lang.Integer);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def apply(value : Long) : ExpressionProxy[java.lang.Long] = {
		return apply(value : java.lang.Long);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def apply(value : Float) : ExpressionProxy[java.lang.Float] = {
		return apply(value : java.lang.Float);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def apply(value : Double) : ExpressionProxy[java.lang.Double] = {
		return apply(value : java.lang.Double);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def apply(value : Boolean) : ExpressionProxy[java.lang.Boolean] = {
		return apply(value : java.lang.Boolean);
	}
	
	/**
	 * Specialized version of apply[VALUE <: AnyRef](...) for Scala numeric type.
	 */
	def apply(value : Char) : ExpressionProxy[java.lang.Character] = {
		return apply(value : java.lang.Character);
	}
}
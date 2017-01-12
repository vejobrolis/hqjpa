package org.hqjpa.generator

/**
 * Common constants.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object Consts {
	/** Names of non entity types. */
	val nonEntityTypes : Seq[String] = Vector("Byte", "Char", "Short", "Integer", "Long", "Float", "Double", "Boolean", "String", "Date");
	
	/** Names of Java primitive types. */
	val javaPrimitiveTypes : Seq[String] = Vector("Byte", "Char", "Short", "Integer", "Long", "Float", "Double", "Boolean");
}
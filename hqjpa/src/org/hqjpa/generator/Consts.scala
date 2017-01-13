package org.hqjpa.generator

/**
 * Common constants.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object Consts {
	/** Names of non entity types. */
	val nonEntityTypes : Seq[String] = Vector(
			"Byte", 
			"Char", 
			"Short", 
			"Integer", 
			"Long", 
			"Float", 
			"Double", 
			"Boolean", 
			"String", 
			"Date",
			"BigDecimal",
			"BigInteger"
		);
	
	/** Names and full class names of Java primitive types (name, full-class-name). */
	val javaPrimitiveTypes : Seq[(String, String)] = Vector(
			("Byte", "java.lang.Byte"),
			("Char", "java.lang.Char"),
			("Short","java.lang.Short"), 
			("Integer", "java.lang.Integer"), 
			("Long", "java.lang.Long"), 
			("Float", "java.lang.Float"), 
			("Double", "java.lang.Double"), 
			("Boolean", "java.lang.Boolean"),
			("BigDecimal", "java.math.BigDecimal"),
			("BigInteger", "java.math.BigInteger")
		);
}
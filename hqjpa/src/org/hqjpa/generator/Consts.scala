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
			"byte[]",
			"Char", 
			"char[]",
			"Short", 
			"short[]",
			"Integer",
			"int[]",
			"Long", 
			"long[]",
			"Float", 
			"float[]",
			"Double", 
			"double[]",
			"Boolean", 
			"boolean[]",
			"String", 
			"Date",
			"BigDecimal",
			"BigInteger"
		);
	
	/** Names and full class names of Java primitive types (name, full-class-name). */
	val javaPrimitiveTypes : Seq[(String, String)] = Vector(
			("Byte", "java.lang.Byte"),
			("byte[]", "Array[Byte]"),
			("Char", "java.lang.Char"),
			("char[]", "Array[Char]"),
			("Short","java.lang.Short"),
			("short[]", "Array[Short]"),
			("Integer", "java.lang.Integer"),
			("int[]", "Array[Int]"),
			("Long", "java.lang.Long"),
			("long[]", "Array[Long]"),
			("Float", "java.lang.Float"),
			("float[]", "Array[Float]"),
			("Double", "java.lang.Double"),
			("double[]", "Array[Double]"),
			("Boolean", "java.lang.Boolean"),
			("boolean[]", "Array[Boolean]"),
			("BigDecimal", "java.math.BigDecimal"),
			("BigInteger", "java.math.BigInteger")
		);
}
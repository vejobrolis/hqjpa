package org.hqjpa.generator

/** 
 * Helper methods for generators.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object GeneratorUtil {
	/**
	 * Tell if given type name is not an entity type name.
	 * @param typeName Type name to examine.
	 * @return True if yes, false if no.
	 */
	def isNonEntityTypeName(typeName : String) : Boolean = {
		Consts.nonEntityTypes.find { net => net == typeName }.isDefined;
	}
	
	/**
	 * Tell if given type name is a Java primitive type name.
	 * @param typeName Type name to examine.
	 * @return True if yes, false if no.
	 */
	def isPrimitiveTypeName(typeName : String) : Boolean = {
		Consts.javaPrimitiveTypes.find { jpt => jpt == typeName }.isDefined;
	}
}
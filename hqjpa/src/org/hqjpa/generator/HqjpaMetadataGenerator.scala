package org.hqjpa.generator

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object HqjpaMetadataGenerator {
	/** Name of meta-data file. */
	val fileName = "HqjpaMetadata.scala";
}

/**
 * Generates text of HQJPA meta-data object and trait used for imports of entity 
 * meta-data artifacts into scopes.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param packageName Name of package to place generate artifacts in.
 * @param entityClassNames Class names for entities to generate meta-data for.
 */
class HqjpaMetadataGenerator(var packageName : String, var entityClassNames : Seq[String]) {
	import HqjpaMetadataGenerator._
	
	/**
	 * Runs the generator.
	 * @return Text of meta-data file.
	 */
	def run() : String = {
		var result : String = "";
		
		//add package name
		result += s"package ${packageName};\n\n";
		
		//add imports
		result += "import javax.annotation.Generated;\n";
		result += "\n";
		
		//generate meta-data object
		{
			//start object
			result += "@Generated(Array(\"org.hqjpa.generator.HqjpaMetadataGenerator\"))\n";
			result += "object HqjpaMetadata {\n"
			
			//add entity fields
			result += generateEntityFields(entityClassNames);
			
			//close object
			result += "}\n";
			result += "\n";
		}

		//TODO: generate meta-data trait
		{
			//start trait
			result += "@Generated(Array(\"org.hqjpa.generator.HqjpaMetadataGenerator\"))\n";
			result += "trait HqjpaMetadata {\n"
			
			//add entity fields
			result += generateEntityFields(entityClassNames);
			
			//close trait
			result += "}\n";
			result += "\n";
		}
		
		//
		return result;
	}
	
	/**
	 * Generates entity fields for inclusion in meta-data object and trait.
	 * @param entityClassNames Class names for entities to generate meta-data for.
	 * @return Text of entity fields block.
	 */
	private def generateEntityFields(entityClassNames : Seq[String]) : String = {
		var result : String = "";
		
		//generate field lines
		entityClassNames.foreach { entityClassName =>
			val fieldLine = s"\t val ${entityClassName} = new ${entityClassName}${EntityProxyGenerator.classNameSuffix}[Null](None);\n"
			
			//add to result
			result += fieldLine;
		}
		
		//
		return result;
	}
}
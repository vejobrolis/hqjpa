package org.hqjpa.generator

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object EntityProxyGenerator {
	/** Entity class name suffix. */
	val classNameSuffix : String = "HqjpaProxy";
}

/**
 * Generates code for entity proxies.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
class EntityProxyGenerator(val entityModel : JpaEntityMetadataParser.Model) {
	import EntityProxyGenerator._
	
	/**
	 * Runs the generator.
	 * @return Text of entity proxy class file. In Scala.
	 */
	def run() : String = {
		var result : String = "";
		
		//add package
		result += s"${entityModel.packageLine}\n\n";
		
		//add original imports
		entityModel.imports.foreach { imporrt =>
			result += s"${imporrt}\n";
		}
		result += "\n";
		
		//add additional imports
		result += "import javax.persistence.criteria.From;\n";
		result += "\n";
		result += "import org.hqjpa.EntityProxy;\n"
		result += "import org.hqjpa.SingularAttributeProxy;\n";
//		result += "import org.hqjpa.SetAttributeProxy;\n";
		result += "\n";
		
		//start entity proxy class
		val entityClassName = entityModel.clasz.entityName;
		val proxyClassName = s"${entityClassName}${classNameSuffix}"
		
		result += "@Generated(Array(\"org.hqjpa.generator.EntityProxyGenerator\"))\n";
		result += 
			s"class ${proxyClassName}[FROM](val __parent : Option[EntityProxy[_, FROM, _]]) " +
			s"extends EntityProxy[FROM, ${entityClassName}, ${proxyClassName}[FROM]]" +
			s"(classOf[${entityClassName}], __parent) { \n";
		
		//generate entity attribute fields
		entityModel.clasz.fields.foreach { field =>
			val attrType = field.tyype;
			val ownerType = field.typeArgs(0);
			var valueType = field.typeArgs(1);
			
			//attributes over entities are routed through entity proxies
			if( !GeneratorUtil.isNonEntityTypeName(valueType) ) {
				//get name of entity proxy class
				val entityProxyClassName = s"${valueType}${classNameSuffix}";
				
				//build field definition parts
				val fieldDef = s"def `${field.name}` = new ${entityProxyClassName}[${ownerType}](Some(this))";
				
				//build override for parts of entity proxy dependent on field attribute type 
				val overrides = attrType match {
						case "SingularAttribute" => {
							val overrides =
								s"override def __singularAttribute : Option[SingularAttribute[${ownerType}, ${valueType}]] = { "+
								s"return Some(${entityClassName}_.`${field.name}`) " +
								s"};"
								
							//
							overrides;
						}
						
						case "SetAttribute" => {
							val overrides =
								s"override def __setAttribute : Option[SetAttribute[${ownerType}, ${valueType}]] = { "+
								s"return Some(${entityClassName}_.`${field.name}`) " +
								s"};"
								
							//
							overrides;
						}
						
						case _ => {
							val msg = 
								s"Unsupported attribute type '${attrType}' for usage over entities. " +
								s"Either error in JPA meta-data class '${entityClassName}_' or " +
								s"generator needs extending.";
							throw new AssertionError(msg); 
						}
					};
					
				//build field line
				val fieldLine = s"\t${fieldDef}{ ${overrides} };";
					
				//add to results
				result += s"${fieldLine}\n";
			}
			//attributes over non-entities are routed through attribute proxies
			else {
				//use full paths of Java primitive types for value type, otherwise Scala value types get expected in their place
				if( GeneratorUtil.isPrimitiveTypeName(valueType) ) {
					valueType = GeneratorUtil.getFullClassNameForPrimitiveType(valueType).get;	
				}
				
				//create field
				attrType match {
					case "SingularAttribute" => {
						//build field definition part
						val fieldDef = s"def `${field.name}` = new SingularAttributeProxy[${ownerType}, ${valueType}](this)";
						
						//build overrides depending on attribute type
						val overrides = 
							s"override def attribute : SingularAttribute[${ownerType}, ${valueType}] = { " +
							s"return ${entityClassName}_.`${field.name}` " +
							s"};"
						
						//build field line
						val fieldLine = s"\t${fieldDef}{ ${overrides} };";
						
						//add to results
						result += s"${fieldLine}\n";
					}
					
					//TODO: find out if non-entity fields can be reflected as sets in JPA, remove this if not, uncomment and fix if yes
//					case "SetAttribute" => {
//						//build field definition part
//						val fieldDef = s"def `${field.name}` = new SetAttributeProxy[${ownerType}, ${valueType}]()";
//						
//						//build overrides depending on attribute type
//						val overrides = 
//							s"override def attribute : SetAttribute[${ownerType}, ${valueType}] = { " +
//							s"return ${entityClassName}_.`${field.name}` " +
//							s"};"
//						
//						//build field line
//						val fieldLine = s"${fieldDef}{ ${overrides} };";
//						
//						//add to results
//						result += s"\t${fieldLine}\n";
//					}
					
					case _ => {
						val msg = 
							s"Unsupported attribute type '${attrType}' for usage over non-entities. " +
							s"Either error in JPA meta-data class '${entityClassName}_' or " +
							s"generator needs extending.";
						throw new AssertionError(msg); 
					}
				}
			}
		};
		
		//close class
		result += "}\n";
		
		//
		return result;
	}
}
package org.hqjpa.generator

import java.io.File
import org.apache.commons.io.FileUtils

/**
 * Generates code files containing definitions necessary for HQJPA.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param packagePath File system path to package containing Hibernate JPA entity classes
 * and related entity meta-data classes.
 */
class Generator(val packagePath : String) {
	//initializer
	{
		//validate inputs
		assert(packagePath != null, "Argument 'packagePath' is null.");
	}
	
	/**
	 * Run the generator. Resulting files will be placed in the same folder as source package.	
	 */
	def run() : Unit = {
		//resolve JPA metadata files
		val jpaMetadataFiles = getJpaMetadataFiles();
		
		//generate entity meta-data class models from JPA meta-data files
		val jpaEntityMetadatas = jpaMetadataFiles.map { jpaMetadataFile =>
				//get file text
				val fileText = FileUtils.readFileToString(jpaMetadataFile, "utf-8");
				
				//parse file text into entity meta-data model			
				val jpaEntityMetadata = {
						val parser = new JpaEntityMetadataParser(fileText);
						val metadata = parser.run();
						
						//
						metadata;
					};
					
				//
				((jpaMetadataFile, jpaEntityMetadata));
			};
		
		//generate HQJPA entity meta-data files
		jpaEntityMetadatas.foreach { case(jpaMetadataFile, jpaEntityMetadata) =>				
			//produce HQJPA entity proxy
			val entityProxyFileName = new File(
					jpaMetadataFile.getParent(),
					jpaMetadataFile.getName().dropRight("_.java".size) + EntityProxyGenerator.classNameSuffix + ".scala"
				);
			
			val entityProxyGen = new EntityProxyGenerator(jpaEntityMetadata);
			val entityProxyText = entityProxyGen.run();
			
			FileUtils.write(entityProxyFileName, entityProxyText, "utf-8", false);
		}	
		
		//generate HQJPA meta-data file
		if( jpaEntityMetadatas.size > 0 ) {
			//get entity class names
			val entityClassNames = jpaEntityMetadatas.map { case(jpaMetadataFile, jpaEntityMetadata) =>
					jpaEntityMetadata.clasz.entityName;
				};
				
			//get target package name from meta-data of first available entity
			val packageName = jpaEntityMetadatas.head._2.packageLine
				.drop("package".size)
				.dropRight(";".size).trim();
			
			//generate
			val generator = new HqjpaMetadataGenerator(packageName, entityClassNames);
			val metadataText = generator.run();
			
			//save to file 
			{
				//build file name
				val targetDir = jpaEntityMetadatas.head._1.getParentFile();
				val metadataFile = new File(targetDir, HqjpaMetadataGenerator.fileName);
				
				//write generated text to file
				FileUtils.write(metadataFile, metadataText, "utf-8", false);
			}
		}
	}
	
	/**
	 * Get all JPA meta-data files present in source package.
	 */
	private def getJpaMetadataFiles() : Seq[File] = {
		//resolve source package
		val srcPkgFile = new File(packagePath);
		
		//get JPA meta-data files in source package 
		val jpaMetadataFiles = srcPkgFile
			.listFiles()
			.filter { file =>
				val fileName = file.getName();
				val isJpaMetadataFile =fileName.endsWith("_.java");
				
				//
				isJpaMetadataFile;
			}
			.map { file => file.getAbsoluteFile() };
		
		//
		return jpaMetadataFiles;
	}
}
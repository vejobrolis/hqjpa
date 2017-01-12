package org.hqjpa.generator

import org.apache.tools.ant.Task
import scala.beans.BeanProperty
import org.apache.tools.ant.BuildException
import java.io.File

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object GeneratorAntTask {	
}

/**
 * Ant task for running generator.<br/>
 * <br/>
 * Properties are:<br/>
 * <ul>
 * 	<li>baseDir - base directory path for source files.</li>
 * 	<li>packageName - name of package containing entity meta-data classes.</li>
 * </ul> 
 * <br/>
 * Static members are thread safe, instance members are not.
 */
class GeneratorAntTask extends Task {
	/** Base directory path for source files. */
	@BeanProperty
	var baseDir : String = _;
	
	/** Name of package containing entity meta-data classes. */
	@BeanProperty
	var packageName : String = _;
	
	/**
	 * Runs the task.
	 */
	override def execute() : Unit = {
		//print out most important attributes for task debugging
		log(s"Ant project base directory set to '${getProject().getBaseDir().getAbsoluteFile()}'.");
		log(s"Attribute 'baseDir' set to '${baseDir}'.");
		log(s"Attribute 'packageName' set to '${packageName}'.");
		
		//validate inputs
		if( baseDir == null ) {
			throw new BuildException("Required property 'baseDir' is not set.");
		}
		
		if( packageName == null ) {
			throw new BuildException("Required property 'packageName' is not set.");
		}
		
		//resolve base directory
		val projectDirFile = getProject().getBaseDir();
		val baseDirFile = new File(projectDirFile, baseDir);
		
		log(s"Base dir resolves to '${baseDirFile.getAbsoluteFile()}'");
		
		if( !(baseDirFile.exists() && baseDirFile.isDirectory()) ) {
			throw new BuildException("Base directory provided in 'baseDir' does not exist or is not acessible.");	
		}
		
		//resolve package directory
		val packageDir = packageName.replace(".", "/");
		val packageDirFile = new File(baseDirFile, packageDir);
		
		if( !(packageDirFile.exists() && packageDirFile.isDirectory()) ) {
			throw new BuildException("Package provided in 'packageName' does not resolve into existing and acessible directory relative to 'baseDir'.");
		}
		
		//
		log("Invoking generator.");
		
		//run the generator
		val generator = new Generator(packageDirFile.getAbsolutePath());
		generator.run();
		
		//
		log("Done.");
	}
}
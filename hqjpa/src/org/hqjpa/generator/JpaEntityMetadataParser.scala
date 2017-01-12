package org.hqjpa.generator

import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, intance members are not.
 */
object JpaEntityMetadataParser {
	/** Model of JPA entity meta-data. */
	class Model {
		/** Package line. */
		var packageLine : String = "";
		
		/** Import statements. */
		var imports : Seq[String] = Vector();
		
		/** JPA entity meta-data class. */
		val clasz : ClassModel = new ClassModel();
	}
	
	/**
	 * Model of JPA entity meta-data class.
	 */
	class ClassModel {
		/** Name of meta-data class. */
		var className : String = "";
		
		/** Name of related entity. */
		var entityName : String = "";
		
		/** Annotations. */
		var annotations : Seq[String] = Vector();

		/** Fields. */
		var fields : Seq[FieldModel] = Vector();
	}
	
	/**
	 * Model of JPA entity meta-data class field.
	 */
	class FieldModel {
		/** Modifiers. */
		var modifiers : String = "";
		
		/** Name of base type. */
		var tyype : String = "";
		
		/** Names of type arguments. */
		var typeArgs : Seq[String] = Vector();
		
		/** Field name. */
		var name : String = "";
	}
}

/**
 * Parser for JPA entity meta-data classes.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param fileText Text of the file containing the JPA entity meta-data class. 
 */
class JpaEntityMetadataParser(val fileText : String) {
	import JpaEntityMetadataParser._
	
	def run() : Model = {
		val model = new Model();
		
		//make modifiable copy of given file text
		var text = fileText;
		
		//extract and subtract package name
		{
			val regex = Pattern.compile(""";""");
			model.packageLine = extractIncluding(regex, text).get.trim();
			text = subtractIncluding(regex, text)
		}

		//extract and subtract import statements
		{
			val regex = Pattern.compile("""import[^;]+;""");
			
			//extract import statements one by one
			var tryAgain = true;
			
			while( tryAgain ) {
				extractIncluding(regex, text) match {
					//success, register statement, subtract from source text, try again
					case Some(imporrt) => {
						model.imports :+= imporrt.trim();
						text = subtractIncluding(regex, text);
					}
					
					//failure, stop trying
					case None => tryAgain = false;
				}
			}
		}
		
		//extract and subtract class annotations
		{
			val regex = Pattern.compile("""@[^\(]+\([^\)]+\)""");
			
			//extract class annotations one by one
			var tryAgain = true;
			
			while( tryAgain ) {
				extractIncluding(regex, text) match {
					//success, register annotation, subtract from source text, try again
					case Some(annotation) => {
						model.clasz.annotations :+= annotation.trim();
						text = subtractIncluding(regex, text);
					}
					
					//failure, stop trying
					case None => tryAgain = false;
				}
			}
		}
		
		//extract and subtract class name
		{
			val regex = Pattern.compile("""public abstract class ([^\{]*) \{""");
			
			//find class name, subtract the line from text
			val matcher = extractNext(regex, text).get;
			text = subtractIncluding(regex, text);
			
			//extract class name and related entity class name
			model.clasz.className = matcher.group(1);
			model.clasz.entityName = model.clasz.className.dropRight(1);
		}
		
		//extract fields
		{
			val regex = Pattern.compile("""(public static volatile) ([^<]+)<([^,]+),([^>]+)> ([^;]+);""");
			
			//split text into lines (up to one field per line)
			val lines = text.split("\n").map { line => line.trim() };
			
			//run through lines
			lines.foreach { line =>
				//ignore empty lines and closing brace of the class
				if( line != "" && line != "}" ) {
					val matcher = extractNext(regex, line).get;
					
					//extract field model
					val field = new FieldModel();
					field.modifiers = matcher.group(1).trim();
					field.tyype = matcher.group(2).trim();
					field.typeArgs = Vector(matcher.group(3).trim(), matcher.group(4).trim());
					field.name = matcher.group(5).trim();
					
					//add field to class
					model.clasz.fields :+= field;
				}
			}
		}
		
		//
		return model;
	}
	
	
	/**
	 * Find first match of given regular expression in given string and subtracts from given
	 * string everything until the end of regular expression match, inclusive.
	 * @param regex Regular expression to match.
	 * @param src String to match against.
	 * @return Resulting string or original string of regular expression did not match.
	 */
	private def subtractIncluding(regex : Pattern, src : String) : String = {
		val matcher = regex.matcher(src);
		
		//search for regular expression
		if( matcher.find() ) {
			//throw everything until the end of expression match from given string
			val subtractTo = matcher.end + 1;
			val newSrc = src.slice(subtractTo, src.length());
			
			//
			return newSrc;
		}
		
		//regular expression did not match, return given string unmodified
		return src;
	}
	
	/**
	 * Matches given expression against given string.
	 * @param regex Regular expression to match.
	 * @param src String to match against.
	 * @return Some matcher on success, None on mismatch.
	 */
	private def extractNext(regex : Pattern, src : String) : Option[Matcher] = {
		val matcher = regex.matcher(src);
		
		if( matcher.find() ) {
			return Some(matcher);
		}
		
		return None;
	}
	
	/**
	 * Matches given expression against given string. Extracts substring from 
	 * the start of given string until the end of the match, inclusive.
	 * @param regex Regular expression to match.
	 * @param src String to match against.
	 * @return Some string extracted or None on mismatch.
	 */
	private def extractIncluding(regex : Pattern, src : String) : Option[String] = {
		val matcher = regex.matcher(src);
		
		//search for regular expression
		if( matcher.find() ) {
			//take everything until the end of expression match from given string
			val extractUntil = matcher.end + 1;
			val result = src.slice(0, extractUntil);
			
			//
			return Some(result);
		}
		
		//regular expression did not match, return given string unmodified
		return None;
	}
}
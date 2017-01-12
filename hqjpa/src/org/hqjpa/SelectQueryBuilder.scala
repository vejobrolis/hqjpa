package org.hqjpa

import scala.collection.JavaConverters._

import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.metamodel.Attribute
import javax.persistence.criteria.Selection
import javax.persistence.TypedQuery
import javax.persistence.criteria.Root
import javax.persistence.criteria.JoinType
import javax.persistence.JoinTable
import javax.persistence.metamodel.SetAttribute
import javax.persistence.metamodel.SingularAttribute
import javax.persistence.criteria.Join
import javax.persistence.criteria.From
import javax.persistence.criteria.Order
import scala.runtime.ScalaWholeNumberProxy
import scala.reflect.ClassTag
import javax.persistence.criteria.Subquery
import javax.persistence.criteria.AbstractQuery

/**
 * Companion object for related class.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 */
object SelectQueryBuilder {
	/**
	 * Getter for default values of generic type arguments.
	 * @param T Generic type argument to get default value for.
	 * 
	 */
	def defaultValue[T <: Any] : T = {
		class Default[T] {
			var example : T = _;
		};
		return (new Default[T]).example;
	}
	
	/**
	 * Base class for composite results of SELECT clause.<br>
	 * <br/>
	 * Static members are thread safe, instance members are not.
	 */
	abstract class CompositeResult {
		/**
		 * Allows setting result fields by index. Overriden in generated implementations. The
		 * ordering of result fields is the same as in original expression supplied to SELECT 
		 * macro.
		 * @param indexArg Index of result field.
		 * @param valueArg Value to write to the field. Runtime type of value must be assignable 
		 * to runtime type of corresponding field.
		 */
		def __set(indexArg : Int, valueArg : Any) : Unit;
	}
	
	/**
	 * Contains implementations of related Scala macros.<br/>
	 * <br/>
	 * Static methods are thread safe, instance methods are not.
	 */
	object Macros {
		import scala.language.experimental.macros
		import scala.reflect.macros._
		import scala.reflect.internal.Flags
		
		/**
		 * Macro for SELECT clause. See selectMacroImpl for details.
		 */
		def selectMacro(c : whitebox.Context)(what : c.Tree) : c.Tree = {
			return selectMacroImpl(c)(what, false);
		}
		
		/**
		 * Macro for SELECT DISTINCT clasuse. See selectMacroImpl for details.
		 */
		def selectDistinctMacro(c : whitebox.Context)(what : c.Tree) : c.Tree = {
			return selectMacroImpl(c)(what, true);
		}
		
		/**
		 * Short-hand for printing compiler information messages in macros.
		 * @param c Enclosing context of macro.
		 * @param msg Message to print.
		 */
		private def info(c : whitebox.Context)(msg : String) : Unit = {
			c.info(c.enclosingPosition, msg, false);
		}
				
		/**
		 * Implements a macro for SELECT clause. Accepts:
		 * 	a) Anonymous classes (new { ... }) where val and var fields can be assigned with
		 * 	individual fields of table classes, table classes, and expressions.
		 *		b) Single table class field, table class or expresssion.
		 * 
		 * Macro produces a block ({ ... }) that clears any previous SELECT clause, internally registers 
		 * fields being selected and returns: 
		 * 	- a substitute anonymous class with fields having types of underlying expressions of original 
		 * 	fields, in case A; 
		 * 	- an default instance of underlying type of expression in case B.
		 * 
		 * @param isSelectDistinct Indicates if select is to be marked as DISTINCT.
		 */
		private def selectMacroImpl(c : whitebox.Context)(what : c.Tree, isSelectDistinct : Boolean) : c.Tree = {
			import c.universe._						
			
//			info(c)(showRaw(what));
						
			//drill down the input expression
			val result = what match {
				//projection via anonymous class
				case Block(List(classDef : ClassDef), _) => {
					val result = generateSelectFromProjectionClass(c)(classDef, isSelectDistinct);
					
					//
					result;
				}
				
				//either single field, expression or class selection or unsupported construct
				case _ => {
					//try extracting underlying type of expression return type to see if expression is supported
					val underlyingType = extractUnderlyingType(c)(what.tpe);
					
					val result =
						//expression is not supported, show error and do not generate anything
						if( underlyingType.isEmpty ) {
							c.error(c.enclosingPosition, "Unsupported type or form of SELECT expression.");
							q"""""";	
						}
						//expression is supported, generate a corresponding select block
						else {
							generateSelectFromOneFieldExpression(c)(what, isSelectDistinct);
						}
					
					//
					result;
				}
			}

			return result;
		}
		
		/**
		 * Extracts underlying type from types of valid proxies for SELECT clause.  Part of selectMacroImpl.
		 * @param c Enclosing context of macro.
		 * @param src Type to extract from.
		 * @return Some extracted type or None if give type is not a valid proxy type for SELECT clause.
		 */
		private def extractUnderlyingType(c : whitebox.Context)(src : c.Type) : Option[c.Type] = {
			import c.universe._
			
//			info(c)(showRaw(src));
			
			//get type symbols for types supported in SELECT clause
			val entityProxyTypeSymbol = typeOf[org.hqjpa.EntityProxy[_, _, _]].typeSymbol;
			val singualAttributeProxyTypeSymbol = typeOf[org.hqjpa.SingularAttributeProxy[_, _]].typeSymbol;
			val caseExpressionProxyTypeSymbol = typeOf[org.hqjpa.CaseExprProxy[_]].typeSymbol;
			val expressionProxyTypeSymbol = typeOf[org.hqjpa.ExpressionProxy[_]].typeSymbol;
			val predicateProxyTypeSymbol = typeOf[org.hqjpa.PredicateProxy].typeSymbol;
			val subqueryProxyTypeSymbol = typeOf[org.hqjpa.SubqueryProxy[_]].typeSymbol;
			
			//checks if 'obj' has a given ('what') base type in it's base types list
			def hasBaseType(obj : Symbol, what : Symbol) : Boolean = {
				return obj.asType.toType.baseClasses.find { bct => bct == what }.isDefined;
			}
			
			//extract underlying type
			val result = src match {
				//extract from SingularAttributeProxy				
				case TypeRef(_, typeSymbol, typeParams) if(typeSymbol == singualAttributeProxyTypeSymbol) => {
					Some(typeParams(1));
				}
				
				//extract from CaseExprProxy
				case TypeRef(_, typeSymbol, typeParams) if(typeSymbol == caseExpressionProxyTypeSymbol) => {
					Some(typeParams(0));
				}
				
				//extract from ExpressionProxy
				case TypeRef(_, typeSymbol, typeParams) if (typeSymbol == expressionProxyTypeSymbol) => {
					Some(typeParams(0));
				}
				
				//extract from PredicateProxy
				case TypeRef(_, typeSymbol, typeParams) if (typeSymbol == predicateProxyTypeSymbol) => {
					Some(typeOf[java.lang.Boolean]);
				}
				
				//derived type of EntityProxy
				case TypeRef(_, typeSymbol, typeParams) if( hasBaseType(typeSymbol, entityProxyTypeSymbol) ) => {
					//ascend to related EntityProxy type, this should produce a TypeRef
					val baseType = typeSymbol.asType.toType.baseType(entityProxyTypeSymbol);
					
					//extract underlying type from generic argument list of EntityProxy
					baseType match {
						//check if we have a TypeRef as expected
						case TypeRef(_, _, typeParams) => {
							//use second type parameter for information about entity class
							typeParams(1) match {
								//try extracting
								case TypeRef(_, typeSymbol, _) => {
									Some(typeSymbol.asType.toType);
								}
								
								//failure, unexpected shape of generic arguments list
								case _ => {
									info(c)(s"Expecting type parameter as TypeRef but received '${showRaw(baseType)}'. Compiler template may have changed.");
									None;
								}
							}
						}
						
						//failure, unexpected shape of base type
						case _ => {
							info(c)(s"Expecting type as TypeRef but received '${showRaw(baseType)}'. Compiler template may have changed.");
							None;
						}
					}					
				}
				
				//extract from SubqueryProxy
				case TypeRef(_, typeSymbol, typeParams) if (typeSymbol == subqueryProxyTypeSymbol) => {
					Some(typeParams(0));
				}
				
				//type is not supported, use it as is
				case _ => None;
			}
			
			//
			return result;
		}
		
		/**
		 * Generates code for SELECT from anonymous projection class. Part of selectMacroImpl.
		 * @param c Enclosing context of macro.
		 * @param classDef Definition of anonymous projection class to reference.
		 * @param isSelectDistinct Indicates if SELECT should be marked as DISTINCT.
		 * @return A tree for code generated.
		 */
		private def generateSelectFromProjectionClass(c : whitebox.Context)(classDef : c.universe.ClassDef, isSelectDistinct : Boolean) : c.Tree = {
			import c.universe._	
			
			//extract class template
			val classTpl = classDef.children.collect { case x : Template => x }.head;
			
			//collect val and var definitions
			val valDefs = classTpl.children.collect { case vd : ValDef if (vd != noSelfType) => vd };
	
			//check if anonymous class is valid for SELECT clause
			var errorsFound = false;
			
			valDefs.foreach { valDef =>
				//extract definition parameters
				val ValDef(modifiers, TermName(termNameWithSpace), tyype, initializer) = valDef;
				
				//trim compiler added whitespace around term name							
				val termName = TermName(termNameWithSpace.trim());
				
				//do not allow vars in projection class, since they would get substituted into vals anyway
				if( modifiers.hasFlag(Flag.MUTABLE) ) {
					//output compiler error message
					val msg = s"Only val fields are allowed in SELECT projections. Field '${termNameWithSpace.trim()}' is a var.";
					c.error(c.enclosingPosition, msg);
					
					//
					errorsFound = true;
				}
				
				//disallow inclusion of unsupported types in projection class
				val underlyingType = extractUnderlyingType(c)(initializer.tpe);
				
				if( underlyingType.isEmpty ) {
					//output compiler error message
					val msg = s"Type of '${termNameWithSpace.trim()}' field is not supported in SELECT clause.";
					c.error(c.enclosingPosition, msg);
					
					//
					errorsFound = true;
				}
			}
			
			//abort code generation if errors were found
			if( errorsFound ) {
				return q"""""";
			}
			
			//generate substitute immutable fields for original val definitions
			val substituteFields = valDefs.map { valDef =>
					//extract definition parameters
					val ValDef(modifiers, TermName(fieldNameWithSpace), tyype, initializer) = valDef;

					//get names for immutable field getter and underlying variable							
					val getterName = fieldNameWithSpace.trim();
					val varName = s"__field_$getterName";
										
					//get underlying type of initializer expression
					val underlyingType = extractUnderlyingType(c)(initializer.tpe).get;
					
					//generate substitute field
					val substituteField = q"""
							private var ${TermName(varName)} : $underlyingType = $EmptyTree;							 
						""";
							
					val substituteFieldGetter = q"""
							def ${TermName(getterName)} : $underlyingType = { return ${TermName(varName)} };
						""";
					
					//
					List(substituteField, substituteFieldGetter);
				}.flatten;
				
			//generate by-index setter for substitute vals
			val byIndexSetter = {
				//create setter cases
				var cases = valDefs.zipWithIndex.map { case(valDef, valDefIndex) => 
						//extract definition parameters
						val ValDef(modifiers, TermName(fieldNameWithSpace), tyype, initializer) = valDef;

						//get name of underlying field variable							
						val varName = s"__field_${fieldNameWithSpace.trim()}";
						
						//get underlying type of initializer expression
						val underlyingType = extractUnderlyingType(c)(initializer.tpe).get;
						
						//generate setter case
						q"""
							if( indexArg == $valDefIndex ) {
								${TermName(varName)} = valueArg.asInstanceOf[$underlyingType];
								return;
							}
							"""
					};
					
					
				//generate setter method
				val setter = q"""
					override def __set(indexArg : Int, valueArg : Any) : Unit = {
						..$cases
						val msg = s"Value '$${indexArg}' of argument indexArg is out of bounds.";
						throw new java.lang.AssertionError(msg);
					}
					""";
				
				//
				setter;
			}
			
			//generate override for toString() method, to print field values
			val toString = {
				//generate (field-getter-name, field-var-name) pairs for fields
				val getterVarNamePairs = valDefs.map { valDef =>
						//extract definition parameters
						val ValDef(modifiers, TermName(fieldNameWithSpace), tyype, initializer) = valDef;
	
						//get names for immutable field getter and underlying variable							
						val getterName = fieldNameWithSpace.trim();
						val varName = s"__field_$getterName";
						
						//
						(getterName, varName);
					};
					
				//construct result string builder
				val strBuilder =  {
						//generate code for 'fieldName=value' segement builders
						var segmentBuilders = getterVarNamePairs.zipWithIndex.map { case((getterName, varName), index) =>
								val prefix = if( index == 0 ) "" else ", ";
								val segmentBuilder = q"""
										$prefix + ${getterName + "='"} + ${TermName(varName)} +"'"									
									""";
								segmentBuilder;								
							};
							
						//add prefix and suffix builders
						segmentBuilders +:= q""" "" + "(" """;
						segmentBuilders :+= q""" "" + ")" """; 
							
						//generate code for full string builder
						val strBuilder = segmentBuilders.foldLeft(q""" "" + "" """) { case(acc, value) =>
								q"""$acc + $value""";
							};

						//
						strBuilder;
					};
					
				//generate toString method
				val toString = q"""
						override def toString() : String = {
							val str = $strBuilder;
							return str;
						}
					""";
				
				//
				toString;
			}
			
			//generate row container class definition
			val rowContainerClassDef = q"""
					class __rowContainerClass extends org.hqjpa.SelectQueryBuilder.CompositeResult {
							..$substituteFields
							$byIndexSetter
							$toString
					};
				""";
								
			//generate select definition registrations
			val selectDefRegs = valDefs.map { vd =>
					//extract definition parameters
					val ValDef(modifiers, termName, tyype, initializer) = vd;
					
					//generate selection registration call, initializer must be untypechecked, otherwise compiler crashes
					q"""${c.prefix}.registerSelectDef(${c.untypecheck(initializer)}.__getSelection());"""
				};
				
			//generate the DISTINCT flag setter
			val distinct =
				if( isSelectDistinct ) {
					q"""${c.prefix}.jpaQuery.distinct(true);"""
				}
				else {
					q"""${c.prefix}.jpaQuery.distinct(false);"""
				};
				
			//generate whole select block
			val selectBlock = q"""
				{
					$rowContainerClassDef;

					$distinct

					${c.prefix}.clearSelectDefs();
					..$selectDefRegs	
				
					${c.prefix}.rowGenerator = { () => new __rowContainerClass() };
					new __rowContainerClass();
				};
				""";
					
			//
			return selectBlock;
		}
		
		/**
		 * Generates code for SELECT from a one field expression. The return type of expression must be
		 * a valid type for SELECT clause. Part of selectMacroImpl.
		 * @param c Enclosing context of macro.
		 * @param expr Expression to reference.
		 * @param isSelectDistinct Indicates if SELECT should be marked as DISTINCT.
		 * @return A tree for code generated.
		 */
		private def generateSelectFromOneFieldExpression(c : whitebox.Context)(expr : c.Tree, isSelectDistinct : Boolean) : c.Tree = {
			import c.universe._
			
			//get underlying type of expression
			val underlyingType = extractUnderlyingType(c)(expr.tpe).get;
			
			//generate the DISTINCT flag setter
			val distinct =
				if( isSelectDistinct ) {
					q"""${c.prefix}.jpaQuery.distinct(true);"""
				}
				else {
					q"""${c.prefix}.jpaQuery.distinct(false);"""
				};
			
			//generate select block
			val selectBlock = q"""
				{		
					$distinct
					
					${c.prefix}.clearSelectDefs();
					${c.prefix}.registerSelectDef(${c.untypecheck(expr)}.__getSelection());

					${c.prefix}.rowGenerator = null;

					val __defaultValOfExpr : $underlyingType = org.hqjpa.SelectQueryBuilder.defaultValue[$underlyingType];
					__defaultValOfExpr;
				};
				""";
			
			//
			return selectBlock;
			
		}
	}
	
	/**
	 * Allows selecting query result format.<br>
	 * <br/>
	 * Static members are thread safe, instance members are not.
	 * 
	 * @param ROW Type of result row of query.
	 */
	class ResultChoice[ROW <: Any](val queryBuilder : SelectQueryBuilder) {
		/** Original query. */
		private lazy val query : TypedQuery[Object] = {
				//validate query state
				assert(queryBuilder.selectDefs.size > 0, "Unable to execute a query that has no SELECT clause.");
			
				//set the select clause
				val selectList = queryBuilder.selectDefs.asJava;
				queryBuilder.jpaQuery.multiselect(selectList);
				
				//make the typed query
				val query = queryBuilder.entityManager.createQuery(queryBuilder.jpaQuery);
				
				//
				query;
			};
		
		/** COUNT query for the original query. For counting total number of rows. */
		private lazy val countQuery : TypedQuery[Object] =  {
				//validate query state
				assert(queryBuilder.firstRoot != null, "Unable to count rows in query that has no FROM clause.");
			
				//set the select clause
				val selectList = Vector(
								queryBuilder.criteriaBuilder.count(queryBuilder.firstRoot)
							)
							.asInstanceOf[Seq[Selection[_]]]
							.asJava;
				queryBuilder.jpaQuery.multiselect(selectList);
			
				//make the typed query
				val query = queryBuilder.entityManager.createQuery(queryBuilder.jpaQuery);
			
				//
				query;
			};
		
		/** COUNT DISTINCT query for the original query. For counting total number of distinct rows. */
		private lazy val countDistinctQuery : TypedQuery[Object] = {
				//validate query state
				assert(queryBuilder.firstRoot != null, "Unable to count rows in query that has no FROM clause.");
			
				//set the select clause
				val selectList = Vector(
								queryBuilder.criteriaBuilder.countDistinct(queryBuilder.firstRoot)
							)
							.asInstanceOf[Seq[Selection[_]]]
							.asJava;
				queryBuilder.jpaQuery.multiselect(selectList);
			
				//make the typed query
				val query = queryBuilder.entityManager.createQuery(queryBuilder.jpaQuery);
			
				//
				query;
			};
	
		
		/**
		 * Get multiple rows of the result.
		 * @param offset Offset index. Default is 0.
		 * @param limit Maximum number of rows to retrieve. Default is -1 for unlimited.
		 * @return A list of rows. 
		 */
		def all(offset : Int = 0, limit : Int = -1) : Seq[ROW] = {
			//set bounds on query
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			
			//get raw results
			val rawResults = query.getResultList().asScala.toVector;
			
			//transcribe into result class of query
			val transcribedResults = rawResults.map { rawRow =>
					val transcribedRow = transcribeResultRow(rawRow, queryBuilder.rowGenerator);
					transcribedRow;
				};
				
			//
			return transcribedResults.asInstanceOf[Seq[ROW]];
		}
		
		/**
		 * Get first row of the result. Will throw AssertionError if result contains no rows.
		 * @param offset Offset index. Default is 0.
		 * @return First row of the result.
		 */
		def first(offset : Int = 0) : ROW = {
			//set bounds on query
			query.setFirstResult(offset);
			query.setMaxResults(1);
			
			//get raw results
			val rawResults = query.getResultList().asScala.toVector;
			
			//check if anything was retrieved, fail if not
			assert(rawResults.size > 0, "Unable to fetch first row from empty result set. Query has returned no results.");
			
			//transcribe into result class of query
			val transcribedResults = rawResults.map { rawRow =>
					val transcribedRow = transcribeResultRow(rawRow, queryBuilder.rowGenerator);
					transcribedRow;
				};
				
			//
			return transcribedResults(0).asInstanceOf[ROW];
		}
		
		/**
		 * Get first row of the result. 
		 * @param offset Offset index. Default is 0.
		 * @return Some first row of the result or None if result contains no rows.
		 */
		def firstOption(offset : Int = 0) : Option[ROW] = {
			//set bounds on query
			query.setFirstResult(offset);
			query.setMaxResults(1);
			
			//get raw results
			val rawResults = query.getResultList().asScala.toVector;
			
			//check if anything was retrieved, return None if not
			if( rawResults.size == 0 ) {
				return None;
			}
			
			//transcribe into result class of query
			val transcribedResults = rawResults.map { rawRow =>
					val transcribedRow = transcribeResultRow(rawRow, queryBuilder.rowGenerator);
					transcribedRow;
				};
				
			//
			return Some(transcribedResults(0).asInstanceOf[ROW]);
		}		
		
		/**
		 * Count rows of the result.
		 * @return Number of rows in the result.
		 */
		def count(offset : Int = 0, limit : Int = -1) : Long = {
			val rawResult = countQuery.getSingleResult();	
			return rawResult.asInstanceOf[java.lang.Long];
		}			
		
		/**
		 * Count distinct rows of the result.
		 * @return Number of rows in the result.
		 */
		def countDistinct(offset : Int = 0, limit : Int = -1) : Long = {
			val rawResult = countDistinctQuery.getSingleResult();	
			return rawResult.asInstanceOf[java.lang.Long];
		}
	}
		
	/**
	 * Transcribe result row of TypedQuery<T> into a copy of corresponding argument of 
	 * SelectQueryBuilder.select() or SelectQueryBuilder.selectDistinct(). Assuming that ordering of
	 * the row is the same as ordering of results from a corresponding 
	 * getSelectList() call.
	 * @param row Row to transcribe.
	 * @param dstGen Generator for row containers. Must be provided for composite results and be null for single field results.
	 * @return Target instance with row transcribed into it. 
	 */
	private def transcribeResultRow(row : AnyRef, dstGen : () => AnyRef) : AnyRef = {			
		//return single field results directly
		if( dstGen == null ) {
			return row;
		}
		//transcribe composite results into container
		else {
			//get cells of the row
			val cells = row match {
					case row : Array[Object] => row;
					case _ => Array[Object](row);
				};	
			
			//generate container
			val dst = dstGen().asInstanceOf[CompositeResult];
			
			//transcribe to container
			cells.zipWithIndex.foreach { case(value, index) =>
				dst.__set(index, value);
			}
			
			//
			return dst;
		}		
	}	
}

/**
 * A query builder.<br/>
 * <br/>
 * Static members are thread safe, instance members are not.
 * 
 * @param entityManager Entity manager to use for query building.
 * @param jpaQuery Underlying JPA query.
 */
class SelectQueryBuilder(
		val entityManager : EntityManager, 
		val jpaQuery : CriteriaQuery[Object]
	) 
	extends 
		IQueryBuilder with
		ScalaNumericLiteralsSupport with
		SubquerySupport with
		SelectQuerySupport
{
	import scala.language.experimental.macros
	
	/** Underlying criteria builder. */
	override val criteriaBuilder : CriteriaBuilder = entityManager.getCriteriaBuilder();
	
	/** Generator for storage containers of selected rows. Only used for composite results. */ 
	var rowGenerator : (() => AnyRef) = _;
	
	/** First root of FROM clause. Used when making COUNT and COUNT DISTINCT versions of the query. */
	var firstRoot : Root[AnyRef] = _;
	
	/** SELECT definitions. */
	private var mSelectDefs : Seq[Selection[_]] = Vector();
	
	/** SELECT definitions. Getter. */
	def selectDefs : Seq[Selection[_]] = {
		return mSelectDefs;
	}
	
	/**
	 * Removes all SELECT definitions currently registered.
	 */
	def clearSelectDefs() : Unit = {
		mSelectDefs = Vector();
	}
	
	
	/**
	 * Registers given SELECT definition.
	 * @param selectDef SELECT definition to register. 
	 */
	def registerSelectDef(selectDef : Selection[_]) : Unit = {
		mSelectDefs :+= selectDef;		
	}
	
	/** 
	 * Create query root in FROM clause.
	 * 
	 * @param ENTITY Type of entity being selected from.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy Proxy of the entity to build query root from. 
	 * @return A new entity proxy over query root.
	 */
	def from[EP[_, ENTITY, SELF] <: EntityProxy[_, ENTITY, SELF], ENTITY, SELF](entityProxy : EP[_, ENTITY, SELF]) : SELF = {
		//create query root
		val root = jpaQuery.from(entityProxy.__entityClass);
		
		//create root entity proxy
		val rootEntityProxy = entityProxy.clone().asInstanceOf[EP[_, ENTITY, SELF]];
		rootEntityProxy.__root = Some(root);
		rootEntityProxy.__queryBuilder = this;
		
		//save first FROM root
		if( firstRoot == null ) {
			firstRoot = root.asInstanceOf[Root[AnyRef]];
		}
		
		//
		return rootEntityProxy.asInstanceOf[SELF];
	}
	
	/**
	 * Create query literal from given value.
	 * 
	 * @param value Type of literal value.
	 * 
	 * @param value Value to create the literal from.
	 * @return Expression proxy for the literal created.
	 */
	override def literal[VALUE <: AnyRef](value : VALUE) : ExpressionProxy[VALUE] = {
		val expr = criteriaBuilder.literal(value);
		val proxy = new ExpressionProxy(expr, this);
		
		return proxy;
	}		
	
	/**
	 * Set ORDER BY clause of query. Will replace any previously set ORDER BY clause.
	 * @param orders Orders to use. 
	 * @returns Self. For call chaining.
	 */
	def orderBy(orders : Order*) : SelectQueryBuilder = {
		//set the clause
		val ordersList = orders.asJava;
		jpaQuery.orderBy(ordersList);
		
		//
		return this;
	}	
	
	/**
	 * Set SELECT clause of query, marking SELECT as NON DISTINCT. Will overwrite any previously 
	 * set SELECT clause.
	 * @param what Object being selected. Can be one of the query roots or anonymous class
	 * containing fields assigned from query roots, or attributes of query roots.
	 * @return Object being selected.
	 */
	def select(what : => AnyRef) : Any = macro SelectQueryBuilder.Macros.selectMacro;
	
	/**
	 * Set SELECT clause of query, marking SELECT as DISTINCT. Will overwrite any previously set
	 * SELECT clause.
	 * @param what Object being selected. Can be one of the query roots or anonymous class
	 * containing fields assigned from query roots, or attributes of query roots.
	 * @return Object being selected.
	 */
	def selectDistinct(what : => AnyRef) : Any = macro SelectQueryBuilder.Macros.selectDistinctMacro;	
}
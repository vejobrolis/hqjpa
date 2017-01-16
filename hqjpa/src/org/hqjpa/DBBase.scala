package org.hqjpa

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.resource.transaction.spi.TransactionStatus
import org.slf4j.LoggerFactory


/**
 * Base trait for DB service objects. You can use it to add select/update/delete
 * query support to your DB service object. Or use it as an example if you are 
 * implementing your custom DB interaction scheme.<br/>
 * <br/>
 * Basic usage scenario is as follows:<br/>
 * <pre>
 * 	//create or extend your own DB service object; your can also derive from 
 * 	//HqjpaMetada to make DB entity definitions accessible via SomeDB.EntityX, 
 * 	object SomeDB extends org.hqjpa.DBBase {
 * 		... configure Hibernate, get session factory and pass it to DBBase via 
 * 		setSessionFactory(...) at some point of initialization ... 
 *    }
 *    
 *    //use your DB service object for queries
 *    SomeDB.inTransaction { dbs =>
 *    	SomeDB.select { q => ... }.all/first/firstOption/count();
 *    	SomeDB.update(...){ (q, ...) => ... }.run();
 *    
 *    	//deletes do not cascade via Hibernate mechanisms if done this way
 *    	SomeDB.delete(...){ (q, ...) => ... }.run();
 *    }
 * </pre>
 * <br/>
 * Thread safe.
 */
trait DBBase {
	/** Hibernate session factory. */
	private var sessionFactory : SessionFactory = _;
	
	/** Lock for accessing hibernate session factory. */
	private val sessionFactoryLock : Object = new Object();
	
	/** Hibernate session for current thread. */
	private val activeSession : ThreadLocal[Session] = new ThreadLocal[Session]();
	
	/** Hibernate transaction for current thread. */
	private val activeTransaction : ThreadLocal[Transaction] = new ThreadLocal[Transaction]();
	
	/** Depth of Hibernate session for current thread. */
	private val activeSessionDepth : ThreadLocal[Int] = new ThreadLocal[Int]() {
		override def initialValue() : Int = {
			return 0;
		}
	};
	
	/** Logger for this trait. */
	private val logger = LoggerFactory.getLogger(this.getClass());
		
	/**
	 * Set Hibernate session factory to use.
	 * @param sf Session factory to use.
	 */
	protected def setSessionFactory(sf : SessionFactory) : Unit = {
		//validate inputs
		assert(sf != null, "Argument 'sf' is null.");
		
		//store inputs
		sessionFactoryLock.synchronized {
			sessionFactory = sf;
		}
	}
	
	/**
	 * Get current Hibernate session factory.
	 * @return Current Hibernate session factory. Null if not set. 
	 */
	protected def getSessionFactory() : SessionFactory = {
		sessionFactoryLock.synchronized {
			return sessionFactory;
		}
	}
	
	/**
	 * Run given function inside Hibernate session and transaction. Supports nesting, but only one
	 * transaction runs at any time, irrespective of nesting depth. The ongoing transaction is commited 
	 * if outer most transaction user exits and no exception  is thrown. If outermost transaction user 
	 * exits with exception, the transaction is rolled back.<br/>
	 * <br/>
	 * You can also rollback transaction manually by calling session.getTransaction().rollback().
	 * However this should be done in known outer-most transaction user only, otherwise you may
	 * end executing SQL statements on the transaction that is rolled back already.<br/>
	 * <br/>
	 * Committing the transaction also flushes the session. 
	 * 
	 * @param function Function to run inside transaction. (session) -> result. 
	 * @return Result of the given function.
	 * 
	 * @throws java.lang.AssertionError If called without Hibernate session factory set. 
	 */
	def inTransaction[RESULT](function : (Session) => RESULT) : RESULT = {
		//start session and transaction if necessary
		if( activeSessionDepth.get() == 0 ) {
			//open new session
			val session = sessionFactoryLock.synchronized {
					//ensure session factory is set
					assert(sessionFactory != null, "Hibernate session factory must be set via setSessionFactory() before trying to start DB transaction.");
					
					//open new session
					val session = sessionFactory.openSession();
					
					//
					session;
				};
				
			//open transaction for a session
			val transaction = session.beginTransaction();
			
			//
			activeSession.set(session);
			activeTransaction.set(transaction);
			activeSessionDepth.set(1);
		}
		//reuse active transaction otherwise
		else {
			activeSessionDepth.set(activeSessionDepth.get() + 1);
		}
		
		//run given function inside transaction
		var exceptionCaught = false;
		
		val result = 
			try {
				val session = activeSession.get();
				val result = function(session);
				
				//XXX: Scala 2.11.8 generates bytecode with invalid stackmap if return statement is placed here 
				result;
			}
			//if outermost user produces exception, rollback transaction
			catch {
				//do not catch Throwable to avoid catching Scala control flow throwables (like scala.runtime.NonLocalReturnControl)
				case e @ (_: Error | _: Exception) => {
					exceptionCaught = true;
					
					//indicate exit from current transaction user
					activeSessionDepth.set(activeSessionDepth.get() - 1);
					
					//rollback transaction for outermost user, unless it is done already
					if( activeSessionDepth.get() == 0 ) {
						//rollback transaction
						val transaction = activeTransaction.get();
						val transactionStatus = transaction.getStatus();
						
						if( 
								transactionStatus == TransactionStatus.ACTIVE || 
								transactionStatus == TransactionStatus.MARKED_ROLLBACK 
							) {
							transaction.rollback();
						}
						
						//close session
						val session = activeSession.get();
						session.close();
						
						//indicate that no session and transaction is running
						activeSession.set(null);
						activeTransaction.set(null);
					}
					
					//rethrow exception from transaction user
					throw e;
				}
			}
			//commit transaction if outermost user exits and transaction is still active
			finally {
				//do nothing if transaction was rolled back due to exception
				if( !exceptionCaught ) {
					//indicate exit from current transaction user
					activeSessionDepth.set(activeSessionDepth.get() - 1);
					
					//if outermost user has completed, finalize transaction and close the session
					if( activeSessionDepth.get() == 0 ) {
						//finalize
						val transaction = activeTransaction.get();
						val transactionStatus = transaction.getStatus();
						
						transactionStatus match {
							//commit active transactions
							case TransactionStatus.ACTIVE => transaction.commit();
							
							//rollback transactions marked for rollback
							case TransactionStatus.MARKED_ROLLBACK => transaction.rollback();
							
							//do nothing otherwise
							case _ => ;
						}
						
						//close the session
						val session = activeSession.get();
						session.close();
						
						//indicate that no transaction is running
						activeSession.set(null);
						activeTransaction.set(null);
					}	
				}
			}
		
		//
		return result;
	}
	
	/**
	 * Allows creating JPA selection query. Both construction and execution of query must be done
	 * inside Hibernate session.
	 * 
	 * @param ROW Type of result row. As returned from QueryBuilder.select() or QueryBuilder.selectDistinct(). 
	 * 
	 * @param builder Query builder function.
	 * @return Query result chooser.
	 * 
	 * @throws java.lang.AssertionError If called outside Hibernate session.
	 */
	def select[ROW <: Any](builder : (SelectQueryBuilder) => ROW) : SelectQueryBuilder.ResultChoice[ROW] = {
		//get active session, fail if none is available
		val session = 
			if( activeSessionDepth.get() > 0 ) {
				activeSession.get();
			}
			else {
				val msg = "Calling select() is only allowed inside Hibernate session, because it needs a valid instance of javax.persistence.EntityManager.";
				throw new AssertionError(msg);
			};

		//start JPA select query
		val cb = session.getCriteriaBuilder();
		val jpaQuery = cb.createQuery();
		
		//create query builder
		val queryBuilder = new SelectQueryBuilder(session, jpaQuery);
		
		//build the query
		builder(queryBuilder);
		
		//build the result chooser
		val resultChoice = new SelectQueryBuilder.ResultChoice[ROW](queryBuilder);
		
		//
		return resultChoice;
	}
	
	/** 
	 * Allows creating JPA update query. Both construction and execution of query must be done
	 * inside Hibernate session.
	 * 
	 * @param OWNER Type of parent entity. Will be Null for meta-data entities.
	 * @param ENTITY Type of entity being selected from.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy HQJPA proxy of the target entity.
	 * @param queryBuilder Query builder. (query, root) => Unit. 
	 * @return A result choice over query built.
	 * 
	 * @throws java.lang.AssertionError If called outside Hibernate session.
	 */
	def update[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF]
		(entityProxy : EP[OWNER, ENTITY, SELF])
		(builder : (UpdateQueryBuilder[ENTITY], SELF) => Unit) : UpdateQueryBuilder.ResultChoice[ENTITY] = {
		
		//get active session, fail if none is available
		val session = 
			if( activeSessionDepth.get() > 0 ) {
				activeSession.get();
			}
			else {
				val msg = "Calling update() is only allowed inside Hibernate session, because it needs a valid instance of javax.persistence.EntityManager.";
				throw new AssertionError(msg);
			};

		//start JPA update query
		val cb = session.getCriteriaBuilder();
		val jpaQuery = cb.createCriteriaUpdate(entityProxy.__entityClass);
		
		//create update query builder
		val queryBuilder = new UpdateQueryBuilder(session, jpaQuery);
		
		//create JPA update query root, wrap into corresponding entity proxy
		val jpaQueryRoot = jpaQuery.from(entityProxy.__entityClass);
		
		val rootEntityProxy = entityProxy.clone().asInstanceOf[EP[OWNER, ENTITY, SELF]];
		rootEntityProxy.__root = Some(jpaQueryRoot);
		rootEntityProxy.__queryBuilder = queryBuilder;
		
		//build the query
		builder(queryBuilder, rootEntityProxy.asInstanceOf[SELF]);
		
		//build the result chooser
		val resultChoice = new UpdateQueryBuilder.ResultChoice(queryBuilder);
		
		//
		return resultChoice;
	}
		
	/** 
	 * Allows creating JPA update query. Both construction and execution of query must be done
	 * inside Hibernate session.
	 * 
	 * @param OWNER Type of parent entity. Will be Null for meta-data entities.
	 * @param ENTITY Type of entity being selected from.
	 * @param SELF Type of entity proxy class.
	 * 
	 * @param entityProxy HQJPA proxy of the target entity.
	 * @param queryBuilder Query builder. (query, root) => Unit. 
	 * @return A result choice over query built.
	 * 
	 * @throws java.lang.AssertionError If called outside Hibernate session.
	 */
	def delete[EP[OWNER, ENTITY, SELF] <: EntityProxy[OWNER, ENTITY, SELF], OWNER, ENTITY, SELF]
		(entityProxy : EP[OWNER, ENTITY, SELF])
		(builder : (DeleteQueryBuilder[ENTITY], SELF) => Unit) : DeleteQueryBuilder.ResultChoice[ENTITY] = {
		
		//get active session, fail if none is available
		val session = 
			if( activeSessionDepth.get() > 0 ) {
				activeSession.get();
			}
			else {
				val msg = "Calling delete() is only allowed inside Hibernate session, because it needs a valid instance of javax.persistence.EntityManager.";
				throw new AssertionError(msg);
			};

		//start JPA delete query
		val cb = session.getCriteriaBuilder();
		val jpaQuery = cb.createCriteriaDelete(entityProxy.__entityClass);
		
		//create update query builder
		val queryBuilder = new DeleteQueryBuilder(session, jpaQuery);
		
		//create JPA update query root, wrap into corresponding entity proxy
		val jpaQueryRoot = jpaQuery.from(entityProxy.__entityClass);
		
		val rootEntityProxy = entityProxy.clone().asInstanceOf[EP[OWNER, ENTITY, SELF]];
		rootEntityProxy.__root = Some(jpaQueryRoot);
		rootEntityProxy.__queryBuilder = queryBuilder;
		
		//build the query
		builder(queryBuilder, rootEntityProxy.asInstanceOf[SELF]);
		
		//build the result chooser
		val resultChoice = new DeleteQueryBuilder.ResultChoice(queryBuilder);
		
		//
		return resultChoice;
	}	
}
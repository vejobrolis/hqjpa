### HQJPA
Scala based SQL like DSL, running on top of JPA Criteria API, as implemented in Hibernate ORM.

Uses Scala macros. Compatible with Scala 2.11.

Currently in process of being restructured and uploaded to GitHub.

### Generating HQJPA meta-data
HQJPA requires own entity meta-data to work. This meta-data is derived from entity JPA meta-data classes by using HQJPA meta-data generator. Currently HQJPA meta-data generator is a quick solution made for working with JPA meta-data classes generated by using Hibernate tools. If your JPA meta-data classes are created in some other way you may have to patch the HQJPA meta-data generator accordingly. 

HQJPA meta-data generator is designed to be used with Apache Ant. The task definition example is as follows:
~~~ xml
<taskdef
    name="hqjpatool"
    classname="org.hqjpa.generator.GeneratorAntTask">
    <classpath>
        <fileset dir="./lib">
            <include name="**/*.jar"/>
        </fileset>
        <pathelement location="${hqjpaDir}"/>			
    </classpath>
</taskdef>
~~~
*classpath* should point to library folder containing Hibernate libraries, Scala 2.11 libraries and related stuff. *pathelement* should point to folder containing compiled HQJPA classes.

With generator task defined HQJPA meta-data can be generated by invoking the task as folllows:
~~~ xml
<hqjpatool baseDir="${dstDir}" packageName="${dstPkg}" />
~~~
*baseDir* should point to directory containing Hibernate entities and corresponding JPA meta-data classes. *packageName* should be the name of package to assign for HQJPA meta-data classes, usually it should match the package in *baseDir*. 

HQJPA meta-data generator also generates file *HqjpaMetada.scala* containing object and trait named *HqjpaMetada*. These contain values corresponding to every entity and can be used to import definitions of those entities into scopes.

### Using HQJPA for queries
First you should have or implement some sort of DB service object. HQJPA contains a trait *org.hqjpa.DBBase* that can be used to quickly add HQJPA related stuff to DB service object. If this trait does not suit you, you can use it's code to implement a solution of your own.

If using the trait, basic usage is as follows:
~~~ scala
//create or extend your own DB service object; your can also derive from 
//HqjpaMetada to make DB entity definitions accessible via SomeDB.EntityX, 
 object SomeDB extends org.hqjpa.DBBase {
    ... configure Hibernate, get session factory and pass it to DBBase via 
    setSessionFactory(...) at some point of initialization ... 
 }
 
 //use your DB service object for queries
 SomeDB.inTransaction { dbs =>
    SomeDB.select { q => ... }.all/first/firstOption/count();
    SomeDB.update(...){ (q, ...) => ... }.run();
 
    //note that deletes do not cascade via Hibernate mechanisms if done this way
    SomeDB.delete(...){ (q, ...) => ... }.run();
 }
~~~

Basic pattern for select queries is as follows:
~~~ scala
SomeDB.inTransaction { dbs =>
    //define query
    val q = SomeDB.select { q =>
        //create query roots
        val rootA = q.from(SomeDB.SomeEntityA);
        val rootB = q.from(SomeDB.SomeEntityB);
        
        //create joins (left, right, inner)
        val rootC = q.leftJoin(rootA.someRelation);
        val rootD = q.rightJoin(rootB.someRelation);
        val rootF = q.innerJoin(rootC.someRelation);
        
        //create subqueries
        val rootG = q.subquery { q =>
                ... same as for select query, except no support for projections ...
            };
        
        //create correlated subqueries
        val rootH = q.subquery(rootA) { (q, rootA) =>
                ... same as for select query, except no support for projections ...
            };
            
        //where clause, equality operator is "===" and inequality operator is "!==" 
        q.where(rootA.someAttr === someValue && rootB.someAttr > someValue);
        
        //order by clause
        q.orderBy(rootA.someAttr.asc, rootB.someAttr.desc);
        
        //simple selects
        q.select(rootA);
        q.selectDistinct(rootA.someAttr);
        
        //projections
        q.select(new { val a = rootA; val b = rootB.someAttr.count; val d = rootB.someAttr > 0; });
        q.selectDistinct(new { val a = rootA.someAttr });
    };
    
    //use query, all result selectors, except caunt, accept limit and offset
    val all = q.all();
    val first = q.first();
    val someFirstOrNone = q.firstOption();
    val count = q.count();
}
~~~

Basic pattern for update and delete queries is as follows:
~~~ scala
SomeDB.inTransaction { dbs =>
    //define update query
    val uq = SomeDB.update(SomeDB.entity){ (q, entity) =>
        //you can define subqueries in the same way as for select queries
        val sqr = q.subquery(...) { q => ... }
        
        //attribute assignments are done this way
        q.set(entity.attrA -> value, entity.attrB -> value);
        
        //where clause is same as in select queries
        q.where(entity.attrA !== someValue);
    };
    
    //use update query
    uq.run();
    
    //define delete query
    val dq = SomeDB.update(SomeDB.entity){ (q, entity) =>
        //you can define subqueries in the same way as for select queries
        val sqr = q.subquery(...) { q => ... }
        
        //where clause is same as in select queries
        q.where(entity.attrA !== someValue);
    };
    
    //use delete query
    dq.run();
}
~~~

### HQJPA is incomplete, how to extend
HQJPA is so far incomplete as to what SQL constructs are supported. Additional operators should be added to relevant trait in *org.hqjpa.OperatorExtensions*. They will automatically apply to attributes and expressions. 

If you need to add something at query scope, you should start with a corresponding query builder class or one of *org.hqjpa.SelectSubquerySupport*, *org.hqjpa.SelectQuerySupport* in case of select queries and subqueries. 

If you need to correlate more query roots in subquries, add corresponding *subquery(...)* methods to *org.hqjpa.SelectSubquerySupport*. Use existing multi-root methods as examples.

Generator classes can be found in package *org.hqjpa.generator*, you should start with *GeneratorAntTask* class, because it binds all the remaining generator classes together.

### Attribution
Initial version of this work was produced as part of software created with public funding of *CLARIN* network in *Department of Information Systems* of *Kaunas University of Technology*.

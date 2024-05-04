# Introduction

This project provides a native JDBC based object relational mapping (ORM) to java.

ORM2 - Second generation of ORM engine using jakarta.persistence annotations to declare database content.

# Usage #

## Table Declaration

Declare a table with standard java persistence API annotations.
<pre>
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity // is an entity because it has an @Id
@Table(name = "PersistExample")
public class PersistExample
{
    @Column(name = "uuid", nullable = false, length = 36)
    @Id
    private String uuid;
    @Column(name = "name", nullable = false, length = 255)
    private String name;
}
</pre>

## Caching ##

The ORM engine supports caching of persistent objects. Therefore, objects must be "clonable" to provide different
instances for different transactional contexts.
* Variant A: `implement java.io.Serializable`.
* Variant B: `extends ORMClonable<TableClass>`, a type safe cloning. Gives the persistent class more control, what needs to copied.

The current cache implementation provides a cache for each persistent class. All caches using WeakReferences, so GC can remove objects as well.

# Feature Description #

## Standard SQL and DDL functionality ##

* create/drop tables (columns and primary key)
* insert/update/delete single rows
* select row by identifier
* select all
* simple data type support for String, Integer, Date, BigDecimal

## Contexts of ORM2 Engine ##

The ORM engine, itself, has no explicit context, but can:
* register persistent object classes
* provide data source context with `datasourceContext = ORM2Engine.prepareConnection(url)`.

## Data source context ##
The data source context represents the external database. A data source context:
* provides new connections to the database `connectionContext = datasourceContext.connect()`
* caches persistent objects independent from connections

## Connection context ##
After connecting the database a connection context is provided. This context: 
* provides a simple CRUD Repository (Data Access Object) to manipulate persistent objects 
  <pre>
  dao = connectionContext.getDao(PersistExample.class)
    PersistExample po = new PersistExample()
    po.set(...);
  dao.insert(po);
    po.set(...)
  dao.update(po);
  dao.delete(po);
  </pre>
* provides extended data access objects via interface declaration of `findBy` methods.
  <pre>
  @ExtendsDao(PersistExample.class)
  public interface ExampleDao extends ORMDao&lt;PersistExample, String>
  {
    @UseField("name")
    Optional&lt;TableChild> findByName(String name);
    @UseField("domainid")
    List&lt;TableChild> findByDomainID(String domainID);
  }
  </pre>

## Performance Improvements ##

* local ORM cache for shared state (via cloneMe() and serializable)

## Open Features ##

* Relations between tables
* Foreign key definition
* Index definition
* Enum type mapping
* Embedded type mapping (like Currency, Money)
* Composite primary keys
* Native Query
* Transactional begin/commit/rollback (TransactionalContext)
* More Datatypes
* support external cache implementations like: redis, https://hazelcast.com/use-cases/microservices/
* support cache synchronization

# Environment Variables

## Service - Environment Variables

| Variable | Description | Example    |
|----------|------------|------------|
| DATASOURCE_URL | database connection string | jdbc:sqlserver://127.0.0.1:1433;database=adb;user=auser;password=secret |

<pre>
git clone git@ssh.dev.azure.com:v3/intershop-com/Products/poc-orm2
</pre>

# Project Structure

| folder      | description                                       |
|-------------|---------------------------------------------------|
| gradle      | Contains the gradle wrapper for the buils process |
| orm2-engine | ORM engine                                        |

# Gradle tasks

| Task | Description |
|--------|------------|
| test | Execute junit test for all packages |

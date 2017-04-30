# About [PostgreSQL](http://www.postgres.org)

Quoting the PostgreSQL site:

> PostgreSQL is a powerful, open source object-relational database system.
> It has more than 15 years of active development and a proven architecture that has earned it a strong reputation for
> reliability, data integrity, and correctness. It runs on all major operating systems, including Linux,
> UNIX (AIX, BSD, HP-UX, SGI IRIX, Mac OS X, Solaris, Tru64), and Windows. It is fully ACID compliant, has full
> support for foreign keys, joins, views, triggers, and stored procedures (in multiple languages).
> It includes most SQL:2008 data types, including INTEGER, NUMERIC, BOOLEAN, CHAR, VARCHAR, DATE, INTERVAL, and
> TIMESTAMP. It also supports storage of binary large objects, including pictures, sounds, or video.
> It has native programming interfaces for C/C++, Java, .Net, Perl, Python, Ruby, Tcl, ODBC, among others, and
> exceptional documentation.

## Installing and setting up PostgreSQL

Installing a PostgreSQL database is simple, and mostly uses a standard installer.
For Linux distributions, use the distribution package for PostgreSQL.
For Mac and Windows installations, use the installation instructions below.

### 1. Download the [PostgreSQL 9.3+ installer](http://www.postgresql.org/download/)

The PostgreSQL installation binary is freely available for download over the net.
While the application server does not use any overly advanced features within the database, we 
should use at least version 9.3 when installing the database.

### 2. Install PostgreSQL (Configuration options)

The PostgreSQL installer is fairly straightforward, but needs 2 non-standard choices which are apparent
during the installation:

1. **Service**. The PostgreSQL database listener should be installed as a service on your computer.
   This requires that you define a database account username ("postgres") and password ("postgres").
   Accept the request to create the operating system account, if asked for it.
2. **Encoding**. When asked to "initialize database" during the installation select the Server 
   Encoding **UTF-8**. This is important to correctly handle search and text encodings.

### 3. Create two required PostgreSQL roles ("Database Users")

It is not recommended to use the database superuser directly from an application server.
Instead, a new database role should be created and given privileges to own the database 
used by the application server. (In newer PG versions, the somewhat confusing term "role" 
is an account that can be a "user" or "group").

Launch the PgAdmin III graphical client (or the text console psql tool) and login as
the superuser `postgresql`. When logged in as the superuser, create the required database 
users `mithlonduser` and `keycloakuser` by firing the following SQL:

    begin;

    CREATE ROLE "wildflyuser" LOGIN
        ENCRYPTED PASSWORD 'md585a23914551c4146ea6469ef2be1e5a3'
        NOSUPERUSER INHERIT CREATEDB NOCREATEROLE NOREPLICATION;
    COMMENT ON ROLE "wildflyuser" IS 'WildFly Appserver DB Pool User';
    
    CREATE ROLE "mithlonduser" LOGIN
        ENCRYPTED PASSWORD 'md5dc095cc48f407588977c1ae83694342c'
        NOSUPERUSER NOINHERIT CREATEDB NOCREATEROLE NOREPLICATION;
    COMMENT ON ROLE "mithlonduser" IS 'Mithlond DB Pool User';

    commit;

You should now have two new database roles in the development database.
Their respective purpose and access credentials are as follows:

<table>
    <tr>
        <th>Role</th>
        <th>Password (Development Environment DB)</th>
        <th>Purpose</th>
    </tr>
    <tr>
        <td>mithlonduser</td>
        <td>MordorRules</td>
        <td>DB role for accessing application data.</td>
    </tr>
    <tr>
        <td>wildflyuser</td>
        <td>MuscaCacavit</td>
        <td>DB role for application server default storage.</td>
    </tr>
</table>

### 4. As "postgres" user, create the databases "MithlondDB" and "WildflyDB"

While still being logged in to the PostgreSQL database as the "postgres" user, create the 
service databases for the mithlond and keycloak services using the following commands in order:

    CREATE DATABASE "ServicesDB"
          WITH OWNER = mithlonduser
               ENCODING = 'UTF8'
               TABLESPACE = pg_default
               LC_COLLATE = 'sv_SE.UTF-8'
               LC_CTYPE = 'sv_SE.UTF-8'
               CONNECTION LIMIT = -1;
    
    COMMENT ON DATABASE "ServicesDB" IS 'Service persistence DB';

    CREATE DATABASE "WildflyDB"
          WITH OWNER "wildflyuser"
               ENCODING = 'UTF8'
               TABLESPACE = pg_default
               LC_COLLATE = 'sv_SE.UTF-8'
               LC_CTYPE = 'sv_SE.UTF-8'
               CONNECTION LIMIT = -1;

    COMMENT ON DATABASE "WildflyDB" IS 'Wildfly Appserver Default DB';

Use the PgAdmin III tool to verify that the databases are created:

<img src="../images/service_databases.png" style="margin:10px; border:1px solid black;" altText="Service Databases"/>

### All Done.
  
Database users and Databases are now set up; you can log out from the PostgreSQL database.
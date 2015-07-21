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

### 3. Create a separate account for the application

It is not recommended to use the database superuser directly from an application server.
Instead, a new database role should be created and given privileges to own the database used by the application
server. (In newer PG versions, the somewhat confusing term "role" is an account that can be a "user" or "group").

Launch the PgAdmin III graphical client (or the text console psql tool) and login as
the superuser postgresql. When logged in as the superuser, create the required database 
user `intranetuser` using the following SQL:

    CREATE ROLE intranetuser LOGIN SUPERUSER NOINHERIT CREATEDB NOCREATEROLE NOREPLICATION;
    UPDATE pg_authid SET rolcatupdate=false WHERE rolname='intranetuser';
    COMMENT ON ROLE intranetuser IS 'Intranet DB Pool User';

You should now have new database role in the database as illustrated in the image below:

<img src="../images/intranetuser_role.png" style="margin:10px;" altText="Intranetuser"/>

To set a password, right-click on the new `intranetuser` role and select `properties`.
In the second tab `definition`, the password can be set.
For most cases you want to set the same password as is used in the built configuration from
the appserver project you're using, e.g. the Postgres password in 
`appserver/wildfly/v9_0/src/main/resources/standalone/configuration/standalone.xml`

### 4. As "postgres" user, Create the database "IntranetDB"

While still being logged in to the PostgreSQL database as the "postgres" user, create the service database
for the select school service using the following command:

    CREATE DATABASE "IntranetDB"
      WITH OWNER = intranetuser
           ENCODING = 'UTF8'
           TABLESPACE = pg_default
           LC_COLLATE = 'sv_SE.utf-8'
           LC_CTYPE = 'sv_SE.utf-8'
           CONNECTION LIMIT = -1;
    
    COMMENT ON DATABASE "IntranetDB"
      IS 'Intranet Pool DB';

Use the PgAdmin III tool to verify that the database is created:

<img src="../images/intranet_db.png" style="margin:10px;" altText="IntranetDB"/>

Log out from the PostgreSQL database

### 5. Setup database structure and data

Open a terminal/shell, make sure you have the **psql** command in your path - it's included in the installation.
On OS X the command location should be something like "/Library/PostgreSQL/9.4/bin".
Navigate to the `services/service-model` project. It contains a create script that can be
run with psql using

    psql -U intranetuser -h localhost -d IntranetDB < src/db/create/intranet.sql


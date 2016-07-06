# Wildfly 10 AppServer overlay Assembly

Getting the basic infrastructure ready for running on an application server can be a daunting task.
This project builds a custom/convenient distribution on top of the WildFly 10 application server,
adding 4 things to it:

1. A suite of standard users, implying that the custom distribution application server can be launched
   immediately upon unpacking (provided that a PostgreSQL database is installed on the server; see below).
1. A PostgreSQL module (containing the JDBC driver for PostgreSQL databases)is installed into the
   application server. This enables creating connection pools to postgres databases, and the application
   server configuration file also contains a PostgreSQL Database pool definition.
2. An EclipseLink module is installed into the application server. This enables the use of EclipseLink instead
   of Hibernate as a JPA provider, and also the use of MOXy as a JAXB provider instead of Jackson.
3. A Keycloak Adapter for Wildfly 10 is installed as an Add-On in the application server. This enables installed
   WARs to use [Keycloak](http://www.keycloak.org/index.html) as an identity manager or identity broker.

## Build the Wildfly configuration overlay

The Wildfly application server requires some configuration before being able to run properly. 
This configuration is partly found within XML files and partly within the modules subdirectory 
of the application server. To simplify things, this type of application server configuration is 
built to an assembly by the project `mithlond-codestyle-appserver-wildfly-v10_0-assembly`
(use either tar.gz or zip version as per your own preference):

<img src="./images/wildfly_overlay.png" style="margin:10px; border: solid DarkGray 1px;" altText="Overlay Structure"/>

The overlay should be extracted in the WILDFLY_HOME directory **when the server is shut down**. 
Since Wildfly application server re-writes its configuration data when the server is shut down, it will 
overwrite any configuration written to any of its configuration files when the server is running. 
Hence the need to unpack the overlay archive when the server is shut down.

### WildFly modules in separate layer: Nazgul

To separate the modules of the Mithlond Codestyle project from modules shipped within the standard
distribution of the WildFly application server, the Mithlond Codestyle overlay places its modules
within a separate layer, called `nazgul`. A layer is simply a directory under which JBoss modules are
placed; the default layer found in `modules/system/layers/base` is simply called `base`. Hence, should
you want to remove the nazgul layer from your application server, simply remove the directory
`modules/system/layers/nazgul`.

Note that the `layers.conf` file is the configuration telling WildFly (through JBoss Modules) that
the nazgul layer should be used in addition to base. This layers.conf file is built as part of the overlay;
the content of the file is simply `layers=nazgul`. The structure within the file system is shown in
the image below:

<img src="./images/nazgul_layer.png" style="margin:10px; border: solid DarkGray 1px;" altText="Overlay Structure"/>

### WildFly modules in separate layer: Keycloak

The Keycloak adapter is installed into its own add-on space, named `keycloak`.
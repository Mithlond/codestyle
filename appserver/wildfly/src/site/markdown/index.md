# About mithlond-codestyle-appserver-wildfly-reactor

The Appserver Wildfly reactor contains projects that build tailored distributions of application servers.
In the case of Wildfly, 3 things are added within the Nazgul add-on -

1. A PostgreSQL module is installed into the application server.
   This enables creating connection pools to postgres databases.
2. An EclipseLink module is installed into the application server. This enables the use of EclipseLink instead
   of Hibernate as a JPA provider, and also the use of MOXy as a JAXB provider instead of Jackson.
3. A Keycloak Adapter for Wildfly 10 is installed as an Add-On in the application server. This enables installed
   WARs to use [Keycloak](http://www.keycloak.org/index.html) as an identity manager or identity broker.

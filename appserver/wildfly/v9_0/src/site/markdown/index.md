# Wildfly 9 AppServer overlay Assembly

Getting the basic infrastructure ready for running on an application server
can be a daunting task in itself. The overlay archives produced here are meant to be unpacked in 
(the root of) a vanilla application server installation to create a fully set-up 
application server.

## Build the Wildfly configuration overlay

The Wildfly application server requires some configuration before being able to run properly. 
This configuration is partly found within XML files and partly within the modules subdirectory 
of the application server. To simplify things, this type of application server configuration is 
built to an assembly by the project `mithlond-codestyle-appserver-wildfly-v9_0-assembly` 
(use either tar.gz or zip version as per your own preference):

<img src="./images/wildfly_overlay.png" style="margin:10px; border: solid DarkGray 1px;" altText="Overlay Structure"/>

The overlay should be extracted in the WILDFLY_HOME directory **when the server is shut down**. 
Since Wildfly application server re-writes its configuration data when the server is shut down, it will 
overwrite any configuration written to any of its configuration files when the server is running. 
Hence the need to unpack the overlay archive when the server is shut down.

## [Keycloak single-sign on (SSO) server](http://keycloak.jboss.org) 

**Keycloak** is a Single-sign-on application which may be installed as an 
optional extension within the Wildfly application server. By default, the overlay installs the 
modules and configuration for a Keycloak server running within the Wildfly appserver.

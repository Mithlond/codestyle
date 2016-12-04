# Wildfly 10 AppServer overlay Assembly

Getting the basic infrastructure ready for running on an application server can be a daunting task.
This project builds a custom/convenient distribution on top of the WildFly 10 application server,
adding 3 things to it:

1. A set of **standard users**, implying that the custom distribution application server can be launched
   immediately upon unpacking (provided that a PostgreSQL database is installed on the server; see below).
1. A **PostgreSQL module** (containing the JDBC driver for PostgreSQL databases)is installed into the
   application server. This enables creating connection pools to postgres databases, and the application
   server configuration file also contains a PostgreSQL Database pool definition.
2. An **EclipseLink module** is installed into the application server. This enables the use of EclipseLink instead
   of Hibernate as a JPA provider, and also the use of MOXy as a JAXB provider instead of Jackson.
3. A **Keycloak Adapter for Wildfly 10** is installed as an Add-On in the application server. This enables 
   installed WARs to use [Keycloak](http://www.keycloak.org/index.html) as an identity manager or identity broker.
   Be certain to use the same Keycloak server version as the version of the Keycloak adapter included as a 
   dependency within this project.     

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

### Keycloak configuration steps
 
After unpacking the overlay within the root directory of a vanilla WildFly 10 application server installation,
a script must be run in order to enable Keycloak within the application server. This installation process is:

1. Ensure that the application server is shut down, and cd to the root directory of the 
   application server installation
2. Fire the installation script: `./bin/jboss-cli.sh --file=bin/keycloak/keycloak-client-overlay-install-offline.cli`

The result of the script should be something like

    {"outcome" => "success"}
    {"outcome" => "success"}
    {
        "outcome" => "success",
        "result" => [("keycloak" => "1.1.0")]
    }
    {"outcome" => "success"}

The installation script adds 3 entries to Wildfly's standalone.xml configuration file, namely:

1. The Keycloak extension definition to the Wildfly server, which is found in the
   top part of the configuration file: 
   `<extension module="org.keycloak.keycloak-adapter-subsystem"/>`
2. An empty Keycloak subsystem definition: `<subsystem xmlns="urn:jboss:domain:keycloak:1.1"/>`   
3. The Keycloak security domain, which contains the definition of Keycloak's login module:   

        <security-domain name="keycloak">
            <authentication>
                <login-module code="org.keycloak.adapters.jboss.KeycloakLoginModule" flag="required"/>
            </authentication>
        </security-domain>`

When adding a WAR to the application server which should be secured by Keycloak,
an entry must be added to the Keycloak subsystem definition. This entry is generated within the
Keycloak administration console, and could look like the following.
 
        === For Presentation WAR deployments (i.e. Presentation WARs) ===
        <secure-deployment name="WAR MODULE NAME.war">
            <realm>theRealmNameSuchAsMithlond</realm>
            <realm-public-key>[paste from the KeyCloak admin Console]</realm-public-key>
            <auth-server-url>http://localhost:8090/auth</auth-server-url>
            <ssl-required>EXTERNAL</ssl-required>
            <resource>mithlond-presentation-war</resource>
            <credential name="secret">password</credential>  <== if direct client invocations are required
        </secure-deployment>

## Structure of the Wildfly 10 overlay

This overlay adds a new layer, called Nazgul, which contains Database drivers and generic module definitions.
Moreover, the overlay also contains script files which must be run if Keycloak should be used to secure web 
applications.

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
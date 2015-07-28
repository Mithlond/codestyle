# About [Wildfly](http://www.wildfly.org)

The Mithlond development environment is self-sustained and should be fully 
installable on a local development machine. The Wildfly application server is 
modern and sports powerful mechanics to simplify Java EE development.

## Installing and setting up Wildfly

Installing a development environment for Wildfly is simple, and has been 
made even simpler with the addition of a configuration overlay assembled 
during the build of this reactor.

### 1. Download the [Wildfly 9+ app server](http://wildfly.org/downloads/)

Download a full distribution release.
The current latest release is version 9.0.0.Final which implements the Java EE 7 standard.

### 2. Install WildFly in a path **without whitespace**.

The Wildfly installation home directory (called WILDFLY_HOME below) should be something like
`/Users/yourUserName/AppServers/wildfly` for Linux/Mac OS X operating systems, and `C:\Applications\wildfly` 
for the windows operating system. When you have installed Wildfly, ensure that the directory `WILDFLY_HOME/bin` 
is in your path, implying that you can launch Wildfly commands directly from the command line.

### 3. Build the Wildfly configuration overlay

The Wildfly application server requires some configuration before being able to run properly. 
This configuration is partly found within XML files and partly within the modules subdirectory 
of the application server. To simplify things, this type of application server configuration is 
built to an assembly by the project `mithlond-codestyle-appserver-wildfly-v9_0-assembly` 
(use either tar.gz or zip version as per your own preference):

<img src="../images/wildfly_overlay.png" style="margin:10px; border: solid DarkGray 1px;" altText="Overlay Structure"/>

The overlay should be extracted in the WILDFLY_HOME directory **when the server is shut down**. 
Since Wildfly application server re-writes its configuration data when the server is shut down, it will 
overwrite any configuration written to any of its configuration files when the server is running. 
Hence the need to unpack the overlay archive when the server is shut down.

### 4. Launch the server; verify Datasource "MithlondDS"

Start the application server using the command `standalone.sh` or `standalone.bat` depending on your operating system.
In the console log, you should see a log line about Wildfly picking up the something like
    
    [org.jboss.as.connector.subsystems.datasources] (MSC service thread 1-3) WFLYJCA0001: Bound data source [java:jboss/datasources/MithlondDS]

If you're runnig on Windows and get an error `Failed to start the http-interface service` it might be the management service using the same port as for example *NVIDIA Network Service*. If this happens you need to stop that service before starting wildfly, change the management port of wildfly or change the port of NVidia service.

Open the DataSources management console on the URL: 

`http://localhost:9990/console/App.html#datasources`

... (using user and password from file ACCESS.txt) and verify that the data source Mithlond DS is operational:

<img src="../images/MithlondDS.png" style="margin:10px; border: solid DarkGray 1px;" altText="Mithlond DS"/>

Your development application environment set should now be complete; a standard setup application server
for development, including a Database and DataSource is now available on your computer.




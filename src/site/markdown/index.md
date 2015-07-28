# Mithlond: Codestyle

The Mithlond: Codestyle project structure contains parent POMs and codestyle 
definitions with best practises for collaborative development.

The reactor consists has 3 main strucures:

1. **poms**. All parent poms for artifact projects (i.e. projects which produce JARs, WARs, EARs, 
   or assemblies) are located here. This structure provides simpler release management using Maven's 
   standard mechanics, as well as a means to change versions or build process for a set of projects 
   with similar needs - for example all Model or API projects - in a single file.
 
2. **codestyle**. All configuration for plugins handling code quality or introspection, as well as
   any custom validation/enforcement rules are placed within this project. Also holds license definitions and
   related header and template files.
   
3. **appserver overlays**. Getting the basic infrastructure ready for running on an application server
   can be a daunting task in itself. The overlays here are meant to be unpacked in (the root of) a vanilla
   application installation to create a fully operating application server.

## Getting the Codestyle project

Clone the repository, and get all tags:

```
git clone https://github.com/Mithlond/mithlond-codestyle.git

cd codestyle

git fetch --tags
```

## Building the Codestyle project

For the latest development build, simply run the build against the latest master branch revision:  

```
mvn clean install
```

For a particular version, checkout its release tag and build normally:
 
```
git checkout mithlond-codestyle-1.0.1

mvn clean install
```

## Using the Codestyle project

When creating multi-module Maven projects, use the mithlond-reactor pom as 
parent for the topmost pom in your reactor:

```xml
<parent>
   <groupId>se.mithlond.codestyle</groupId>
   <artifactId>mithlond-codestyle-reactor</artifactId>
   <version>[latest-release-version]</version>
</parent>   
```

When creating projects which should emit artifacts, use one of the mithlond-parent poms
as parent for your project.

```xml
<parent>
   <groupId>se.mithlond.codestyle.poms</groupId>
   <artifactId>mithlond-codestyle-parent</artifactId>
   <version>[latest-release-version]</version>
</parent>
```

The type of project you are creating defines which of the mithlond parent poms that should be used:

<table>
    <tr>
        <th>Project Type</th>
        <th>Parent POM artifactID</th>
        <th>Exported/Public?</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>Entity Model</td>
        <td>mithlond-codestyle-model-parent</td>
        <td>True</td>
        <td>Project containing Entity definitions. Possibly/likely with JPA or JAXB annotations.</td>
    </tr>
    <tr>
        <td>API</td>
        <td>mithlond-codestyle-api-parent</td>
        <td>True</td>
        <td>Project containing API specifications. Typically Interfaces, Abstract classes and 
        Exceptions, but no implementations.</td>
    </tr>
    <tr>
        <td>Implementation</td>
        <td>mithlond-codestyle-parent</td>
        <td>False</td>
        <td>Project containing implementation of specifications from API projects. 
        Typically contains EJBs, OSGi service implementations etc.</td>
    </tr>
    <tr>
        <td>Web Application</td>
        <td>mithlond-codestyle-war-parent</td>
        <td>False</td>
        <td>Project defining a Web Application/WAR. Inject APIs in compile scope and implementations in runtime.</td>
    </tr>
</table>
# Mithlond: Codestyle Parent

The Mithlond: Codestyle Parent POMs is the root of all projects producing (releasable) artifacts 
within the Mithlond project structure.

## Using the Codestyle Parent

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

### What about reactor POMs?

When creating multi-module Maven projects, use the mithlond-reactor pom as 
parent for the topmost pom in your reactor:

```xml
<parent>
   <groupId>se.mithlond.codestyle</groupId>
   <artifactId>mithlond-codestyle-reactor</artifactId>
   <version>[latest-release-version]</version>
</parent>   
```

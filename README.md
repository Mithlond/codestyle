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
          
## Release Documentation

Release documentation (including Maven site documentation) can be found
at [The Mithlond: Codestyle Documentation Site](http://mithlond.github.io/mithlond-codestyle).

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




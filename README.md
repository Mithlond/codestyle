# Mithlond: Codestyle

The Mithlond: Codestyle project structure contains parent POMs and codestyle 
definitions with best practises for collaborative development.

The reactor consists has 2 main strucures:

1. **poms**. All parent poms for artifact projects (i.e. projects which produce JARs, WARs, EARs, 
   or assemblies) are located here. This structure provides simpler release management using Maven's 
   standard mechanics, as well as a means to change versions or build process for a set of projects 
   with similar needs - for example all Model or API projects - in a single file.
 
2. **codestyle**. All configuration for plugins handling code quality or introspection, as well as
   any custom validation/enforcement rules are placed within this project. Also holds license definitions and
   related header and template files.

## Getting and building the Codestyle project locally

Clone the repository, and get all tags:

```
git clone https://github.com/Mithlond/codestyle.git

cd codestyle

git fetch --tags
```
For the latest development build, simply run the build:  

```
mvn clean install
```

For a particular version, checkout its release tag and build normally:
 
```
git checkout mithlond-codestyle-1.0.1

mvn clean install
```




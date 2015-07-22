# About mithlond-codestyle-model-parent

The Model parent should be used as the parent POM of all Model artifact projects, implying projects
using creating Entity model classes annotated with JPA and JAXB annotations.

In addition to providing all facilities included in the mithlond-codestyle-api-parent, the 
mithlond-codestyle-model-parent sets up required facilities to simplify running JPA-aware unit/integration tests. 
These facilities imply a JPA Persistence provider and required dependencies. Moreover, the actual JPA provider 
used is controlled by Maven build profiles.
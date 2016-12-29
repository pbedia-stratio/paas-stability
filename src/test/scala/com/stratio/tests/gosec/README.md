
# Stratio Gosec performance tests

## Description

Mavenized scala project to stress Stratio Gosec using  (http://gatling.io/).

## How it works

One maven profile has been created to run Stratio Gosec performance tests. (-PGOSEC)

### Manual Populatation

Manual configuration should be performed before running simulation:
- application.conf
- reference.conf
Those files contain the configuration property used by project, please have a look to this site
[site](https://stratio.atlassian.net/wiki/display/SG/Management+-+Configuration) for further information

### Manual Populatation

These are the environment variables that can be used to run simulation:

These are the environment variables that can be used to run simulation:

- **users** = define the number of users to perform simulation steps.
- **injectD** = defines the user injection ramp.
- **runD** = defines running duration.
- **userName** = defines userId to instanciate the the plugin.
- **InstanceArg** = defines plugin instance name
- **manifestPath** = defines the path where the manifest is located (“../target/dummy-0.5.0-SNAPSHOT.jar)
- **version** = defines the plugin version that will be invoqued

### Run performance test

To run Gosec plugin performance test you should run the following command:
```sh
$ mvn clean test -PGOSEC -DuserName=<userId> -Dversion=<version> -Dinstance=<instanceName>
```

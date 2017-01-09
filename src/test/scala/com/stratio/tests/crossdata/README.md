
# Stratio Crossdata performance tests

## Description

Mavenized scala project to stress Stratio Crossdata using Gatling (http://gatling.io/).

## How it works

One maven profile has been created to run Stratio Crossdata performance tests. (_-PCROSSDATA_)

### Environment variables

These are the environment variables that can be used to run simulation:

- **users**                 = define the number of users to perform simulation steps.
- **injectD**               = defines the user injection ramp.
- **runD**                  = defines running duration.
- **SCENARIO**              = defines the scenario to run("Scenario1.csv", "Scenario2.csv" or "Scenario3.csv")
- **CROSSDATA_HOST**        = defines where Crossdata instance is running.
- **CROSSDATA_PORT**        = defines the http port of Crossdata instance.
- **KEYSTORE_PATH**         = defines the path of the keystore
- **KEYSTORE_PASSWORD**     = defines the password of the keystore
- **TRUSTEDSTORE_PATH**     = defines the path of the trustedstore
- **TRUSTEDSTORE_PASSWORD** = defines the password of the trustedstore



### Run performance test

To run Crossdata performance test you should run the following command:

```sh
$ mvn test -PCROSSDATA -DSCENARIO="Scenario1.csv" -DCROSSDATA_HOST="crossdata.marathon.mesos" -DCROSSDATA_PORT="10075" -DTRUSTEDSTORE_PATH="/.../secrets/truststore.jks" -DTRUSTEDSTORE_PASSWORD="TRUSTEDSTORE_PASSWORD" -DKEYSTORE_PATH="/.../secrets/crossdata-1.jks" -DKEYSTORE_PASSWORD="KEYSTORE_PASSWORD"
```

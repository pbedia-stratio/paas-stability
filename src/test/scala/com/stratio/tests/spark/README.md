
# Zookeeper performance tests

## Description

Mavenized scala project to stress Spark using Gatling (http://gatling.io/).

## How it works

One maven profile has been created to run Spark performance tests. (_-PSPARK_)

### Requirements

If you want to launch Spark tests against a Stratio PaaS instance you need to install sshpass to obtain the connection token.

### Environment variables

These are the environment variables that can be used to run simulation:

- **DCOS_USER**      	= defines DCOS admin user. (Ex. "admin@demo.stratio.com").
- **REMOTE_USER**     	= defines user for remote ssh connection in cluster machines. (Ex. "root").
- **REMOTE_PASSWORD**   = defines remote ssh password.
- **MASTER_MESOS**     	= defines Mesos-Master of the cluster used to run the simulation. (Ex. "127.0.0.1").
- **SERVICE_NAME**     	= defines service name used in PaaS deployement. (Ex. "spark1").

- **RAMP_USERS**      	= defines the user injection ramp.
- **TEST_DURATION**   	= defines running duration.

### Run performance test

To run Spark performance test against Crossdata DataSource defined within the associationId.csv feeder you should run the following command:

```sh
$ mvn test -PSPARK -DTEST_DURATION=5 -DRAMP_USERS=5 -DDCOS_USER=admin@stratio.com -DREMOTE_USER=root -DREMOTE_PASSWORD=password -DMASTER_MESOS=127.0.0.1 -DSERVICE_NAME=spark-test
```
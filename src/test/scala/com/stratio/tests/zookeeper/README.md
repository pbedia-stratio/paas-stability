
# Zookeeper performance tests

## Description

Mavenized scala project to stress Zookeeper using Gatling (http://gatling.io/).

## How it works

One maven profile has been created to run Zookeeper performance tests. (_-PZOOKEEPER_)

### Security Prerequisites

To run Zookeeper tests against a secured zookeeper you have to point JAAS and KRB5 properties to a file correctly configured.
In jaas.conf you have to configure keytab property with a valid keytab for your zookeeper secured.

Jaas.conf example:
```sh
Client {
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  storeKey=true
  useTicketCache=false
  keyTab="/path/to/zkClient.keytab"
  principal="your-principal";
};
```
Krb5.conf example
```sh
[libdefaults]
default_realm = DEMO.STRATIO.COM
dns_lookup_realm = false
[realms]
DEMO.STRATIO.COM = {
  kdc = idp.integration.labs.stratio.com
  admin_server = idp.integration.labs.stratio.com
  default_domain = demo.stratio.com
}
[domain_realm]
.demo.stratio.com = DEMO.STRATIO.COM
demo.stratio.com = DEMO.STRATIO.COM
```

### Environment variables

These are the environment variables that can be used to run simulation:

- **JAAS**      	= by default "/opt/zookeeper/conf/jaas.conf".
- **KRB5**   		= by default "/etc/krb5.conf".
- **SERVERS**      	= defines where zookeeper is running. (Ex. "zookeeper-sec.marathon.mesos:2181").
- **PRINCIPAL**     = defines the principal. (Ex. "zookeeper/zookeeper-plugin-agent@DEMO.STRATIO.COM").

### Run performance test

To run Zookeeper performance test you should run the following command:

```sh
$ mvn test -PZOOKEEPER -DSERVERS=zookeeper-sec.marathon.mesos:2181 -DSERVERS=zookeeper/zookeeper-plugin-agent@DEMO.STRATIO.COM
```
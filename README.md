#  Paas-Stability
# Stability tests for PaaS services

## Description

In order to test the reliability of the Stratio PaaS, we have created a simple mechanism to execute 
kind of stability tests that mimic the behaviour of a number of users interacting with the clusters.

These users should interact with the frameworks installed within the Stratio PaaS and should perform 
common operations, for instance: create a kafka topic and produce some messages, create and remove 
zookeeper znods, run spark jobs...

In order to do so we have created this project which using [Gatling](http://gatling.io/#/) will execute 
a stability scenario against a running Stratio PaaS instance. 

## Running the scenario

To run the scenario you just need to clone this repo and run the following command within the recently
created repo:

```
$ mvn gatling:execute 
```
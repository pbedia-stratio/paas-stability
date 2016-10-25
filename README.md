
# Viewer system testing project

## Description

Mavenized scala project to stress Viewer using Gatling (http://gatling.io/).

## How it works

Different maven profiles have been created to run the perf tests.

### Populate profile

This maven profile should be executed if you want to automatically populate your Viewer instance with some Datasources, Dataviews and Widgets. Current this profile might be executed by running the following command:

```sh
$ mvn test -PPRE
```
it works as following:

 - For each element within the sources_placeholders.json file the script will: create a datasource, a dataview, a page and a page-widget association.
 - For each page-widget association created the script will write an entry in the associationId.csv with the following info: DS,PWID (where DS is the Datasource name and the PWID is the id of the association). This file will be used as the feeder for the rest of the scripts to run the performance tests.

IMPORTANT: This profile is not creating the widget anymore. This operation requires now to upload a zip file with the widget and we've not managed to upload the file using Gatling, so there is a val (widgetId ) where you should specify the id of the widget of the Viewer App that you will executed the tests against.

If you have an already populated Viewer instance you can skip this profile execution and just manually create the associationId.csv feeder file with the datasource and page-widget associations that you want to run the performance tests against.

### Run-all-the-performance-tests profile

To run all the performance tests against all the DataSources defined within the associationId.csv feeder you should run the following command:

```sh
$ mvn test -PALL
```

### Run-just-one-DS-tests profile

You might want to run the performance tests just for one of the Datasources defined within the associationId.csv feeder file. To do so run the following commands:

```sh
$ mvn test -P<DATASOURCE>
```
Where DATASOURCE is the Datasource to run the tests against (see the pom.xml file to find out the different profiles)
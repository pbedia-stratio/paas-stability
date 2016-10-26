package com.stratio.tests.kafka

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.ELFileBody
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class KafkaSimulation extends PerformanceTest {
  feederAssoc.records.foreach(fA => {
//    if (fA.get("TOPIC").get.equals(dtopic)) {
//    if (true) {
    scns += scenario(fA.get("TOPIC").toString())
      .exec(flattenMapIntoAttributes(fA))
        .exec(Prod.produceData)
//    }
    println (fA.get("TOPIC"))
  }
  )

  logger.error("Scenarios size: {}",scns.size )
  if (scns.size < 1) {
    throw new AssertionError("No scenarios")
  }

  setUp(
      scns.toList.map(_.inject(rampUsers(users) over (60 seconds))))
    .maxDuration(1 minutes)
    .uniformPauses(5)
    .assertions(
        global.responseTime.max.lessThan(3000),
        global.successfulRequests.percent.greaterThan(95)
    )

}

trait PerformanceTest extends Simulation {

  def logger = LoggerFactory.getLogger(this.getClass)


  object Prod {
    val HTTPproducer = "http://".concat(System.getProperty("REST_PROXY", "127.0.0.1:80")).concat("/topics/").concat(System.getProperty("TOPIC", "hola"))
    println (HTTPproducer)
    val sentHeaders = Map("Content-Type" -> "application/vnd.kafka.json.v1+json")
    println (sentHeaders)
    val produceData =
      forever(
        pace(5 seconds, 10 seconds).exec(
          http("POST /data")
            .post(HTTPproducer)
            .headers(sentHeaders)
            .body(ELFileBody("producerBody.txt")).asJSON
//            .body(StringBody("""{"records":[{"value":{"foo":"amparo"}}]}"""
              .stripMargin)).asJSON
//            .check(jsonPath("$.offsets")
            .check()
//              .saveAs("response"))
//            .check(responseTimeInMillis.lessThanOrEqual(10000L))
        )
      )
    print(produceData)
  }


  val feederAssoc = csv("topicList.csv")

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))

//  val dtopic = this.getClass.getSimpleName.replace("Data", "").toLowerCase

  val scns = new ListBuffer[ScenarioBuilder]()

//  val HTTPproducer = "http://".concat(System.getProperty("BROKER_IP", "127.0.0.1")).concat("/topics/").concat(System.getProperty("TOPIC", "test"))
//  val sentHeaders = Map("Content-Type" -> "application/vnd.kafka.json.v1+json")
//
//  val producerRequest = http("Producer url")
//    .post(HTTPproducer)
//    .headers(sentHeaders)
//
//  object order{
//    val dataStart = new AtomicInteger(1)
//  }
}


package com.stratio.tests.kafka

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.http.Predef.http
import org.slf4j.LoggerFactory

trait Common {

  def logger = LoggerFactory.getLogger(this.getClass)


  val HTTPproducer = "http://".concat(System.getProperty("BROKER_IP", "127.0.0.1")).concat("/topics/").concat(System.getProperty("TOPIC", "test"))
  val HTTPcreateConsumer = "http://".concat(System.getProperty("BROKER_IP", "127.0.0.1")).concat("/consumers/").concat(System.getProperty("MY_JSON", "test"))
  val HTTPobtainMsg = "http://".concat(System.getProperty("BROKER_IP", "127.0.0.1")).concat("/consumers/").concat(System.getProperty("MY_JSON", "test"))
    .concat("/instances/").concat(System.getProperty("CONSUMER", "1")).concat("/topics/").concat(System.getProperty("TOPIC", "test"))

  val sentHeaders = Map("Content-Type" -> "application/vnd.kafka.json.v1+json")


  val producerRequest = http("Producer url")
    .post(HTTPproducer)
    .headers(sentHeaders)

  val createConsumer = http("Create consumer url")
    .post(HTTPcreateConsumer)
    .headers(sentHeaders)

  val messageRequest = http("Obtain message url")
    .post(HTTPobtainMsg)
    .headers(sentHeaders)

  object order{
    val dataStart = new AtomicInteger(1)
  }
}

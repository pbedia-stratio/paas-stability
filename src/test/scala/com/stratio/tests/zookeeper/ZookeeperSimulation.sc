//package com.stratio.tests.zookeeper
//
//
//import akka.actor.{Props, ActorRef}
//import io.gatling.core.Predef._
//import io.gatling.core.action.Chainable
//import io.gatling.core.action.builder.ActionBuilder
//import io.gatling.core.config.Protocols
//import io.gatling.core.result.message.OK
//import org.apache.curator.framework.CuratorFrameworkFactory
//import org.apache.curator.framework.recipes.cache.NodeCache
//import org.apache.curator.framework.recipes.cache.NodeCacheListener
//import org.apache.curator.framework.recipes.cache.{NodeCacheListener, NodeCache}
//import org.apache.curator.retry.ExponentialBackoffRetry
//
//import scala.concurrent.duration._
//
//
//class ZookeeperSimulation extends Simulation {
//
//  val connect = new ActionBuilder {
//    def build(next: ActorRef, protocols: Protocols) = {
//      system.actorOf(Props(new ZookeeperPerf(next)))
//    }
//  }
//
//  val scn = scenario("Stratio Paas Stability Tests")
//    .repeat(System.getProperty("repeat", "1").toInt) { exec(connect) }
//
//  setUp(
//    scn.inject(
//      rampUsers(System.getProperty("NUMBER_USERS", "10").toInt) over(60 seconds)
//    )
//  )
//  /** ASSERTIONS TO BE ADDED...
//    *.assertions(global.responseTime.max.lessThan(100000), global.successfulRequests.percent.greaterThan(95), global
//    *.responseTime.mean.lessThan(50000)) */
//}
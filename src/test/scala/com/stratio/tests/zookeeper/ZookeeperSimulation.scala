package com.stratio.tests.zookeeper

import io.gatling.core.Predef._
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.feeder._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioContext
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry

import scala.concurrent.duration._
import scala.concurrent.forkjoin.ThreadLocalRandom

class ZookeeperSimulation extends Simulation {

  val servers = System.getProperty("SERVERS", "127.0.0.1:2181")
  val retryPolicy = new ExponentialBackoffRetry(1000, 3)
  val curatorZookeeperClient = CuratorFrameworkFactory.newClient(servers, retryPolicy)
  curatorZookeeperClient.start
  curatorZookeeperClient.getZookeeperClient.blockUntilConnectedOrTimedOut
  val recordsByGroup: Map[String, IndexedSeq[Record[String]]] =
    csv("src/test/resources/feeders/znodes.csv").records.groupBy{ record => record("group") }
  val znodesByGroup: Map[String, IndexedSeq[String]] =
    recordsByGroup.mapValues{ records => records.map {record => record("znode")} }

  lazy val createZnodes = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      new CreateZnodes(next, ctx, curatorZookeeperClient)
    }
  }

  val scn = scenario("Zookeeper stability test")
    .feed(csv("src/test/resources/feeders/users.csv"))
      .exec { session =>
        session("group").validate[String].map { group =>
          val znodes = znodesByGroup(group)
          val selectedZnode = znodes(ThreadLocalRandom.current.nextInt(znodes.length))
          session.set("znode", selectedZnode)
        }
      }.forever(pace(1 seconds, 5 seconds).exec(createZnodes))

    setUp(scn.inject(rampUsers(3) over (15 seconds))).maxDuration(15 seconds)
}



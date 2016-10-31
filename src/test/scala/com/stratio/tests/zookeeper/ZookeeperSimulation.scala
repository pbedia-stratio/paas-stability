package com.stratio.tests.zookeeper

import io.gatling.core.Predef._
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.feeder._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioContext}
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.data.Stat

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

  val createZnodes = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val createMethod = {znode:String => curatorZookeeperClient.create().forPath(znode) }
      val statusCheck = {stat:Stat => stat == null }
      new ZnodeOperation(next, ctx, curatorZookeeperClient, "Create znode", statusCheck, createMethod)
    }
  }

  val setData = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val setDataMethod = {znode:String => curatorZookeeperClient.setData().forPath(znode, "test".getBytes()) }
      val statusCheck = {stat:Stat => stat != null }
      new ZnodeOperation(next, ctx, curatorZookeeperClient, "Set data", statusCheck, setDataMethod)
    }
  }

  val getData = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val getDataMethod = {znode:String => curatorZookeeperClient.getData().forPath(znode) }
      val statusCheck = {stat:Stat => stat != null }
      new ZnodeOperation(next, ctx, curatorZookeeperClient, "Get data", statusCheck, getDataMethod)
    }
  }

  val removeZnodes = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val removeMethod = {znode:String => curatorZookeeperClient.delete().forPath(znode) }
      val statusCheck = {stat:Stat => stat != null }
      new ZnodeOperation(next, ctx, curatorZookeeperClient, "Remove znode", statusCheck, removeMethod)
    }
  }

  val createZnodeSetDataGetDataAndRemoveZnodes = ChainBuilder(List(createZnodes, setData, getData, removeZnodes))
  val znodesCreationAndRemovingScenario = scenario("Create and remove znodes")
    .feed(csv("src/test/resources/feeders/users.csv"))
    .exec { session =>
      session("group").validate[String].map { group =>
        val znodes = znodesByGroup(group)
        val selectedZnode = znodes(ThreadLocalRandom.current.nextInt(znodes.length))
        session.set("znode", selectedZnode)
      }
    }.forever(pace(1 seconds, 5 seconds).exec(createZnodeSetDataGetDataAndRemoveZnodes))

  setUp(znodesCreationAndRemovingScenario.inject(rampUsers(2) over(15 seconds))).maxDuration(15 seconds)
}
package com.stratio.tests.zookeeper

import com.stratio.tests.commons.TemporizedOperation
import com.stratio.tests.commons.zookeeper.ZnodeHandler
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

class ZookeeperSimulation extends Simulation with ZnodeHandler {

  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))
  val servers = System.getProperty("SERVERS", "127.0.0.1:2181")
  val retryPolicy = new ExponentialBackoffRetry(1000, 3)
  val curatorZookeeperClient = CuratorFrameworkFactory.newClient(servers, retryPolicy)
  curatorZookeeperClient.start
  curatorZookeeperClient.getZookeeperClient.blockUntilConnectedOrTimedOut
  val recordsByGroup: Map[String, IndexedSeq[Record[String]]] =
    csv("src/test/resources/feeders/znodes.csv").records.groupBy{ record => record("group") }
  val znodesByGroup: Map[String, IndexedSeq[String]] =
    recordsByGroup.mapValues{ records => records.map {record => record("znode")} }
  val genericErrorMessage = "Error performing the action: "

  def getErrorMessage(actionName: String) = s"""$genericErrorMessage $actionName"""

  val createZnodes = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val statusCheck = { stat:Stat => stat == null }
      val createZnode = { znode:String => curatorZookeeperClient.create().forPath(znode) }
      val createZnodeOperation = { session: Session =>
        znodeOperationBuilder(session, statusCheck, curatorZookeeperClient, createZnode) 
      }
      val actionName = "Create znode"
      val errorMessage = getErrorMessage(actionName)
      new TemporizedOperation(next, ctx, actionName, createZnodeOperation, Some(errorMessage))
    }
  }

  val setData = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val statusCheck = { stat:Stat => stat != null }
      val setZnode = { znode:String => curatorZookeeperClient.setData().forPath(znode, "test".getBytes()) }
      val setZnodeOperation = { session: Session =>
        znodeOperationBuilder(session, statusCheck, curatorZookeeperClient, setZnode)
      }

      val actionName = "Set data"
      val errorMessage = getErrorMessage(actionName)
      new TemporizedOperation(next, ctx, actionName, setZnodeOperation, Some(errorMessage))
    }
  }

  val getData = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val getData = {znode:String => curatorZookeeperClient.getData().forPath(znode) }
      val statusCheck = {stat:Stat => stat != null }
      val getZnodeOperation = { session: Session =>
        znodeOperationBuilder(session, statusCheck, curatorZookeeperClient, getData)
      }
      val actionName = "Get data"
      val errorMessage = getErrorMessage(actionName)
      new TemporizedOperation(next, ctx, actionName, getZnodeOperation, Some(errorMessage))
    }
  }

  val removeZnodes = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val removeZnode = { znode:String => curatorZookeeperClient.delete().forPath(znode) }
      val statusCheck = { stat:Stat => stat != null }
      val getZnodeOperation = { session: Session =>
        znodeOperationBuilder(session, statusCheck, curatorZookeeperClient, removeZnode)
      }
      val actionName = "Remove znode"
      val errorMessage = getErrorMessage(actionName)
      new TemporizedOperation(next, ctx, actionName, getZnodeOperation, Some(errorMessage))
    }
  }

  val createZnodeSetDataGetDataAndRemoveZnodes = ChainBuilder(List(createZnodes, setData, getData, removeZnodes))
  val znodesCreationAndRemovingScenario = scenario("Create, set data and get data and remove znode.")
    .feed(csv("src/test/resources/feeders/users.csv"))
    .exec { session =>
      session("group").validate[String].map { group =>
        val znodes = znodesByGroup(group)
        val selectedZnode = znodes(ThreadLocalRandom.current.nextInt(znodes.length))
        session.set("znode", selectedZnode)
      }
    }.forever(pace(1 seconds, 5 seconds).exec(createZnodeSetDataGetDataAndRemoveZnodes))

  setUp(znodesCreationAndRemovingScenario.inject(rampUsers(3) over(15 seconds))).maxDuration(runDuration minutes)
}
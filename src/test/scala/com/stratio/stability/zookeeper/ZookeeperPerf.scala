package com.stratio.tests


import akka.actor.{Props, ActorRef}
import io.gatling.core.Predef._
import io.gatling.core.action.Chainable
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.core.result.message.OK
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.cache.{NodeCacheListener, NodeCache}
import org.apache.curator.retry.ExponentialBackoffRetry

import scala.concurrent.duration._


class ZookeeperPerf(val next: ActorRef) extends Chainable {

//  val logger = LoggerFactory.getLogger(this.getClass.getName)

  def execute(session: Session) {
    var start: Long = 0L
    var end: Long = 0L
    var status: Status = OK
    var errorMessage: Option[String] = None

    val servers = System.getProperty("SERVERS", "127.0.0.1:2181")
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)

    //    val curatorZookeeperClient = CuratorFrameworkFactory.newClient("localhost:2181,localhost:2182,localhost:2183", retryPolicy)
    val curatorZookeeperClient = CuratorFrameworkFactory.newClient(servers, retryPolicy)
    curatorZookeeperClient.start
    curatorZookeeperClient.getZookeeperClient.blockUntilConnectedOrTimedOut

    val znodePath = "/test_node"
    val originalData = new String(curatorZookeeperClient.getData.forPath(znodePath)) // This should be "Some data"

    /* Zookeeper NodeCache service to get properties from ZNode */
    val nodeCache = new NodeCache(curatorZookeeperClient, znodePath)
    nodeCache.getListenable.addListener(new NodeCacheListener {
      @Override
      def nodeChanged = {
        try {
          val dataFromZNode = nodeCache.getCurrentData
          //          val currentData = csv("associationId.csv")
          val currentData = "foo"
          val newData = currentData // This should be some new data after it is changed in the Zookeeper ensemble
//          val newData = new String(currentData.getData) // This should be some new data after it is changed in the Zookeeper ensemble
        } catch {
          case ex: Exception => logger.error("Exception while fetching properties from zookeeper ZNode, reason " + ex.getCause)
        }
      }
      nodeCache.start
    })
  }
}

package com.stratio.tests.commons.zookeeper

import io.gatling.core.Predef._
import org.apache.curator.framework.CuratorFramework
import org.apache.zookeeper.data.Stat

case class ExecutionTime(startTime: Long, endTime: Long)

trait ZnodeHandler {

  def getZnodePath(session: Session) = {
    val sessionZnode = session.attributes("znode").asInstanceOf[String]
    s"""/$sessionZnode"""
  }

  def znodeOperationBuilder(
                             session: Session,
                             statusCheck: Stat => Boolean,
                             zookeeperClient: CuratorFramework,
                             methodToBeExecuted: String => Any): Unit = {
    val znode = getZnodePath(session)
    val stat = zookeeperClient.checkExists().forPath(znode)
    if (statusCheck(stat)) {
      methodToBeExecuted(znode)
    }
  }
}


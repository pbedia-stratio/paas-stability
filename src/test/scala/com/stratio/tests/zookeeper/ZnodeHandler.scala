package com.stratio.tests.zookeeper

import io.gatling.core.Predef._
import org.apache.curator.framework.CuratorFramework

case class ExecutionTime(startTime: Long, endTime: Long)

trait ZnodeHandler {

  def getZnodePath(session: Session) = {
    val sessionZnode = session.attributes("znode").asInstanceOf[String]
    s"""/$sessionZnode"""
  }

  def withTimer(curatorZookeeperClient: CuratorFramework, znodePath: String)(blockOfCode: String => Any): (Long, Long) = {
    val start = System.currentTimeMillis
    blockOfCode(znodePath)
    val end = System.currentTimeMillis
    (start, end)
  }
}


package com.stratio.tests.zookeeper

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext
import org.apache.curator.framework.CuratorFramework


class RemoveZnodes(val next: Action, ctx: ScenarioContext, curatorZookeeperClient: CuratorFramework) extends ChainableAction {

  override def name: String = "Remove znodes"
  override def execute(session: Session) {
    var start: Long = 0L
    var end: Long = 0L
    var status: Status = OK
    var errorMessage: Option[String] = None
    val sessionZnode = session.attributes("znode").asInstanceOf[String]
    val znodePath = s"""/$sessionZnode"""
    try {
      val stat = curatorZookeeperClient.checkExists().forPath(znodePath)
      start = System.currentTimeMillis
      if (stat != null) {
        curatorZookeeperClient.delete().forPath(znodePath)
      }
      end = System.currentTimeMillis
    } catch {
      case e: Exception =>
        errorMessage = Some(e.getMessage)
        logger.error(s"""Error removing znode with path: $znodePath""", e)
        status = KO
    } finally {
      val responseTime = new ResponseTimings(start, end)
      val requestName = "Remove znodes"
      val message = errorMessage
      val extraInfo = Nil
      val responseCode: Option[String] = None
      ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, message, extraInfo)
      next ! session

    }
  }
}

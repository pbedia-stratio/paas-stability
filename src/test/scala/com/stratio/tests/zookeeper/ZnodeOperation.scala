package com.stratio.tests.zookeeper

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext
import org.apache.curator.framework.CuratorFramework
import org.apache.zookeeper.data.Stat

import scala.util.Try

class ZnodeOperation(val next: Action,
                     ctx: ScenarioContext,
                     curatorZookeeperClient: CuratorFramework,
                     actionName: String,
                     statusCheck: Stat => Boolean,
                     methodToBeExecuted: String => Any)
  extends ChainableAction
  with ZnodeHandler {

  override def name: String = actionName
  override def execute(session: Session) {
    var status: Status = OK
    var errorMessage: Option[String] = None
    val znodePath = getZnodePath(session)
    var startTime = 0L
    var endTime = 0L

    try {
      val stat = curatorZookeeperClient.checkExists().forPath(znodePath)
      val times = withTimer(curatorZookeeperClient, znodePath) { znode =>
         if (statusCheck(stat)) {
          methodToBeExecuted(znode)
        }
      }
      startTime = times._1
      endTime = times._2
    } catch {
      case e: Exception =>
        errorMessage = Some(e.getMessage)
        logger.error(s"""Error performing the action: $actionName on znode with path: $znodePath""", e)
        status = KO
    } finally {
      val responseTime = new ResponseTimings(startTime, endTime)
      ctx.coreComponents.statsEngine.logResponse(session, name, responseTime, status, None, errorMessage, Nil)
      next ! session
    }
  }
}




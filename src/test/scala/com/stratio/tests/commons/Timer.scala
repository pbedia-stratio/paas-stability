package com.stratio.tests.commons

import io.gatling.core.session.Session

trait Timer {
  def withTimer(session: Session)(blockOfCode: Session => Any): (Long, Long) = {
    val start = System.currentTimeMillis
    blockOfCode(session)
    val end = System.currentTimeMillis
    (start, end)
  }
}

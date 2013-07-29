package com.scalaloader.reporter.console

import com.scalaloader.actor.TestCaseResultEvent
import scala.concurrent.duration._
import Console._

/**
 * User: stas
 * Date: 20/06/13, 14:16
 */
object PrintableImplicits {
  implicit def testCaseToPrintable(tc: TestCaseResultEvent): Printable = new Printable {
    def toConsoleString: String = {
      import tc.result.measure._
      val duration = end - start millisecond
      val result = error map (s => s"${RED}Failed: $s") orElse Some(s"${GREEN}Succesful") map (s => s"$s${WHITE}") get

      f"\tDuration = $duration, Result = $result"
    }
  }
}

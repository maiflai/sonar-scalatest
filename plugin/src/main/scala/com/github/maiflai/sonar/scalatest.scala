package com.github.maiflai.sonar

import java.nio.file.Path
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

import com.github.maiflai.sonar.Result.Fail

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.xml.{Node, Text}

object scalatest {

  private val name: Path => String = _.getFileName.toString

  val isDiscoverySuite: Path => Boolean = name.andThen { it =>
    it.startsWith("TEST-org.scalatest.tools.DiscoverySuite-") && it.endsWith(".xml")
  }

  val isTestSuite: Path => Boolean = p => {
    if (isDiscoverySuite(p)) false
    else name.andThen { it =>
      it.startsWith("TEST-") && it.endsWith(".xml")
    }(p)
  }

  val parseTestSuite: Node => TestSuiteResult = p => {
    val stringToDuration: String => Duration = time => {
      Duration(time.toDouble, TimeUnit.SECONDS)
    }
    val nodeToTestSuiteResult: Node => (TestClass, Duration) = n => {
      val name = n \@ "name"
      val time = n \@ "time"
      TestClass(name) -> stringToDuration(time)
    }
    val nodeToTestException: Node => Fail = n => {
      val message = n \@ "message"
      val stacktrace = n.child match {
        case Text(s) :: Nil => s
        case ArrayBuffer(Text(s)) => s
      }
      Fail(message, stacktrace)
    }
    val nodeToUnitTestResult: Node => UnitTestResult = n => {
      val name = n \@ "name"
      val time = n \@ "time"
      val failure = (n \ "failure").headOption.map(nodeToTestException)
      val skipped = (n \ "skipped").headOption
      UnitTestResult(
        TestName(name),
        stringToDuration(time),
        failure.
          orElse(skipped.map(_=>Result.Skipped)).
          getOrElse(Result.Pass))
    }
    val (spec, time) = nodeToTestSuiteResult(p)
    val testCases = (p \\ "testcase").map(nodeToUnitTestResult)
    TestSuiteResult(spec, time, testCases)
  }

}
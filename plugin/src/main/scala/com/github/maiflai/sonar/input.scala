package com.github.maiflai.sonar

import scala.concurrent.duration.Duration

case class TestName(value: String)

case class TestClass(value: String)

sealed trait Result

object Result {

  case object Pass extends Result

  case object Skipped extends Result

  case class Fail(message: String, stackTrace: String) extends Result

}

case class UnitTestResult(name: TestName, time: Duration, result: Result)

case class TestSuiteResult(className: TestClass, wallTime: Duration, testCases: Seq[UnitTestResult])

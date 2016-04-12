package com.github.maiflai.sonar

import com.github.maiflai.sonar.Result.{Fail, Pass, Skipped}
import com.github.maiflai.sonar.ScalaTestSensor._
import com.github.maiflai.sonar.scalatest.parseTestSuite
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.{Sensor, SensorContext}
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.resources.Project
import org.sonar.api.test.TestCase
import org.sonar.api.utils.log.Loggers

import scala.xml.XML

class ScalaTestSensor(bob: ResourcePerspectives, fileSystem: FileSystem) extends Sensor {

  private val log = Loggers.get(classOf[ScalaTestSensor])

  private val perspectives = new PerspectivesHack(bob)

  override def shouldExecuteOnProject(project: Project): Boolean =
    fileSystem.languages().contains("scala")


  override def analyse(module: Project, context: SensorContext): Unit = {
    val settings = new ScalaTestSensorSettings(context)
    val testReports = settings.xmlRoots.flatMap(discoverXmlFiles)
    log.debug("Discovered {} XML files", testReports.size)
    val testSuiteResults = testReports.map(p => parseTestSuite(XML.loadFile(p.toFile)))
    val input = source(fileSystem, settings.testRoots)

    testSuiteResults.foreach { testSuiteResult =>

      log.debug("Processing {}", testSuiteResult.className.value)
      val maybeInputFile = input(testSuiteResult.className)
      maybeInputFile match {
        case None => log.warn("Failed to find source file for {}", testSuiteResult.className.value)
        case Some(inputFile) =>
          log.debug("Recording results for {}", inputFile.relativePath())
          val testPlan = perspectives.testPlan(inputFile)
          testSuiteResult.testCases.foreach { s =>
            testPlan.addTestCase(s.name.value).
              setType(TestCase.TYPE_UNIT).
              setDurationInMs(s.time.toMillis).
              setMessage(resultToMessage(s.result)).
              setStackTrace(resultToStackTrace(s.result)).
              setStatus(resultToStatus(s.result))
          }

          val skipped: Double = testSuiteResult.testCases.count(_.result == Result.Skipped)
          val failed: Double = testSuiteResult.testCases.count(_.result.isInstanceOf[Result.Fail])

          context.saveMeasure(inputFile, CoreMetrics.TESTS, testSuiteResult.testCases.size: Double)
          context.saveMeasure(inputFile, CoreMetrics.SKIPPED_TESTS, skipped)
          context.saveMeasure(inputFile, CoreMetrics.TEST_FAILURES, failed)
          context.saveMeasure(inputFile, CoreMetrics.TEST_EXECUTION_TIME, testSuiteResult.wallTime.toMillis: Double)
      }
    }


  }

  override def toString: String = getClass.getSimpleName
}

object ScalaTestSensor {

  val resultToStatus: Result => TestCase.Status = {
    case Pass => TestCase.Status.OK
    case Fail(_, _) => TestCase.Status.FAILURE
    case Skipped => TestCase.Status.SKIPPED
  }

  val resultToStackTrace: Result => String = {
    case Fail(_, stacktrace) => stacktrace
    case _ => null
  }

  val resultToMessage: Result => String = {
    case Fail(message, _) => message
    case _ => null
  }
}
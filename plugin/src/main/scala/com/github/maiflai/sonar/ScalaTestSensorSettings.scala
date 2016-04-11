package com.github.maiflai.sonar

import java.nio.file.Paths

import org.sonar.api.batch.SensorContext

class ScalaTestSensorSettings(sensorContext: SensorContext) {

  val testSourcePath = Paths.get(string("sonar.tests").getOrElse("src/test/scala"))

  val xmlPath = Paths.get(string("sonar.junit.reportsPath").getOrElse("build/test-results"))

  private def string(key: String): Option[String] = {
    if (sensorContext.settings().hasKey(key))
      Some(sensorContext.settings().getString(key))
    else None
  }
}
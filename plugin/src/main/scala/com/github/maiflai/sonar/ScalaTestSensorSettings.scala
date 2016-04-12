package com.github.maiflai.sonar

import java.nio.file.{Path, Paths}

import org.sonar.api.batch.SensorContext

class ScalaTestSensorSettings(sensorContext: SensorContext) {

  val testRoots: Seq[Path] = paths("sonar.tests")

  val xmlRoots: Seq[Path] = paths("sonar.junit.reportsPath")

  private def paths(key: String): Seq[Path] =
    sensorContext.settings().getStringArray(key).map(Paths.get(_)).toSeq
}
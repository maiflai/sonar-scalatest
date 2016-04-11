package com.github.maiflai.sonar

import java.util

import org.sonar.api.SonarPlugin

import scala.collection.JavaConversions._

class ScalaTestPlugin extends SonarPlugin {
  override def getExtensions: util.List[_] = List(classOf[ScalaTestSensor])
}

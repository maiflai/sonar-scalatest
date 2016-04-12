package com.github.maiflai.sonar

import java.io.Serializable
import java.lang.Double
import java.nio.file.Paths
import java.util
import java.util.Date

import com.github.maiflai.sonar.ScalaTestSensorSettingsSpec.TestContext
import org.scalatest.{Matchers, WordSpec}
import org.sonar.api.batch.fs.{FileSystem, InputFile, InputPath}
import org.sonar.api.batch.rule.ActiveRules
import org.sonar.api.batch.sensor.dependency.NewDependency
import org.sonar.api.batch.sensor.duplication.NewDuplication
import org.sonar.api.batch.sensor.highlighting.NewHighlighting
import org.sonar.api.batch.sensor.issue.NewIssue
import org.sonar.api.batch.sensor.measure.NewMeasure
import org.sonar.api.batch.{AnalysisMode, Event, SensorContext}
import org.sonar.api.config.PropertyDefinition.builder
import org.sonar.api.config.{PropertyDefinitions, Settings}
import org.sonar.api.design.Dependency
import org.sonar.api.measures.{Measure, MeasuresFilter, Metric}
import org.sonar.api.resources.{ProjectLink, Resource}

import scala.collection.JavaConverters._

class ScalaTestSensorSettingsSpec extends WordSpec with Matchers {

  "the test source path" should {
    "support a simple path" in {
      new ScalaTestSensorSettings(new TestContext()).testRoots should have length 1
    }
    "support multiple paths" in {
      new ScalaTestSensorSettings(new TestContext(scalas = Some(Seq("a", "b")))).testRoots shouldBe Seq(Paths.get("a"), Paths.get("b"))
    }
  }
  "the xml source path" should {
    "support a simple path" in {
      new ScalaTestSensorSettings(new TestContext()).testRoots should have length 1
    }
    "support multiple paths" in {
      new ScalaTestSensorSettings(new TestContext(xml = Some(Seq("a", "b")))).xmlRoots shouldBe Seq(Paths.get("a"), Paths.get("b"))
    }
  }
}

object ScalaTestSensorSettingsSpec {


  //noinspection NotImplementedCode,ScalaDeprecation
  class TestContext(scalas: Option[Seq[String]] = None, xml: Option[Seq[String]] = None) extends SensorContext {

    import org.sonar.api.rules.Violation

    val testSource = builder("sonar.tests").multiValues(true).defaultValue("src/test/scala").build()

    val xmlSource = builder("sonar.junit.reportsPath").multiValues(true).defaultValue("build/test-reports").build()

    val _settings = new Settings(new PropertyDefinitions(Seq(testSource, xmlSource).asJava))

    scalas.foreach(s => _settings.setProperty(testSource.key(), s.toArray))
    xml.foreach(s => _settings.setProperty(xmlSource.key(), s.toArray))

    override def saveDependency(dependency: Dependency): Dependency = ???

    override def isExcluded(reference: Resource): Boolean = ???

    override def deleteLink(key: String): Unit = ???

    override def isIndexed(reference: Resource, acceptExcluded: Boolean): Boolean = ???

    override def saveViolations(violations: util.Collection[Violation]): Unit = ???

    override def getParent(reference: Resource): Resource = ???

    override def getOutgoingDependencies(from: Resource): util.Collection[Dependency] = ???

    override def getMeasures[M](filter: MeasuresFilter[M]): M = ???

    override def getMeasures[M](resource: Resource, filter: MeasuresFilter[M]): M = ???

    override def saveSource(reference: Resource, source: String): Unit = ???

    override def deleteEvent(event: Event): Unit = ???

    override def saveViolation(violation: Violation, force: Boolean): Unit = ???

    override def saveViolation(violation: Violation): Unit = ???

    override def saveResource(resource: Resource): String = ???

    override def getEvents(resource: Resource): util.List[Event] = ???

    override def getDependencies: util.Set[Dependency] = ???

    override def getIncomingDependencies(to: Resource): util.Collection[Dependency] = ???

    override def index(resource: Resource): Boolean = ???

    override def index(resource: Resource, parentReference: Resource): Boolean = ???

    override def saveLink(link: ProjectLink): Unit = ???

    override def getMeasure[G <: Serializable](metric: Metric[G]): Measure[G] = ???

    override def getMeasure[G <: Serializable](resource: Resource, metric: Metric[G]): Measure[G] = ???

    override def getChildren(reference: Resource): util.Collection[Resource] = ???

    override def createEvent(resource: Resource, name: String, description: String, category: String, date: Date): Event = ???

    override def getResource[R <: Resource](reference: R): R = ???

    override def getResource(inputPath: InputPath): Resource = ???

    override def saveMeasure(measure: Measure[_ <: Serializable]): Measure[_ <: Serializable] = ???

    override def saveMeasure(metric: Metric[_ <: Serializable], value: Double): Measure[_ <: Serializable] = ???

    override def saveMeasure(resource: Resource, metric: Metric[_ <: Serializable], value: Double): Measure[_ <: Serializable] = ???

    override def saveMeasure(resource: Resource, measure: Measure[_ <: Serializable]): Measure[_ <: Serializable] = ???

    override def saveMeasure(inputFile: InputFile, metric: Metric[_ <: Serializable], value: Double): Measure[_ <: Serializable] = ???

    override def saveMeasure(inputFile: InputFile, measure: Measure[_ <: Serializable]): Measure[_ <: Serializable] = ???

    override def newDuplication(): NewDuplication = ???

    override def activeRules(): ActiveRules = ???

    override def newHighlighting(): NewHighlighting = ???

    override def analysisMode(): AnalysisMode = ???

    override def fileSystem(): FileSystem = ???

    override def newDependency(): NewDependency = ???

    override def settings(): Settings = _settings

    override def newMeasure[G <: Serializable](): NewMeasure[G] = ???

    override def newIssue(): NewIssue = ???
  }

}

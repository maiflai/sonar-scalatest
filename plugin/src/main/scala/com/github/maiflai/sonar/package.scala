package com.github.maiflai

import java.nio.file._

import org.sonar.api.batch.fs.InputFile

package object sonar {

  type DiscoverXmlFiles = Path => Seq[Path]

  type ParseXmlFile = Path => TestSuiteResult

  type DiscoverScalaFile = TestClass => Option[InputFile]

  val discoverXmlFiles: DiscoverXmlFiles = path => {
    import scala.collection.JavaConversions._
    Files.newDirectoryStream(path).iterator().
      filter(scalatest.isTestSuite).
      toSeq
  }

  def source(fs: org.sonar.api.batch.fs.FileSystem): DiscoverScalaFile = testClass => {
    import scala.collection.JavaConversions._
    val outerClass = testClass.value.takeWhile(_ != '!')
    val bestGuessFile = "src/test/scala/" + outerClass.replace('.', '/').+(".scala")
    val predicate = fs.predicates().hasRelativePath(bestGuessFile)
    fs.inputFiles(predicate).headOption
  }


}

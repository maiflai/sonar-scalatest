package com.github.maiflai.sonar

import java.nio.file.{Files, Path, Paths}

import com.github.maiflai.sonar.ScalaTestSpec._
import com.github.maiflai.sonar.scalatest.{isDiscoverySuite, isTestSuite, parseTestSuite}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.xml.XML

class ScalaTestSpec extends WordSpec with PropertyChecks with Matchers {

  "isDiscoverySuite" should {
    "not match a test spec" in {
      forAll(anyTestSuite) { name =>
        isDiscoverySuite(name) shouldBe false
      }
    }
    "not match a random file" in {
      forAll(anyRandomPath) { p =>
        isTestSuite(p) shouldBe false
      }
    }
    "match a discovery suite" in {
      forAll(anyDiscoverySuite) { p =>
        isDiscoverySuite(p) shouldBe true
      }
    }
  }

  "isTestSuite" should {
    "not match a Discovery file" in {
      forAll(anyDiscoverySuite) { p =>
        isTestSuite(p) shouldBe false
      }
    }
    "not match a random file" in {
      forAll(anyRandomPath) { p =>
        isTestSuite(p) shouldBe false
      }
    }
    "match a test spec" in {
      forAll(anyTestSuite) { p =>
        isTestSuite(p) shouldBe true
      }
    }
  }

  "parseTestSuite" should {
    "parse an empty suite" in {
      parseTestSuite(<testsuite name="bob" time="0"/>) shouldBe TestSuiteResult(TestClass("bob"), 0.seconds, Seq.empty)
    }
    "parse a pass" in {
      parseTestSuite(
        <testsuite name="bob" time="0">
          <testcase name="rita" class="com.Rita" time="0.01"></testcase>
        </testsuite>) shouldBe TestSuiteResult(TestClass("bob"), 0.seconds, UnitTestResult(TestName("rita"), 0.01.seconds, Result.Pass) :: Nil)
    }
    "parse a failure" in {
      parseTestSuite(
        <testsuite name="bob" time="0.10">
          <testcase name="rita" class="com.Rita" time="0.01">
            <failure message="her!" type="class org.scalatest.exceptions.TestFailedException">sue</failure>
          </testcase>
        </testsuite>) shouldBe TestSuiteResult(TestClass("bob"), 0.10.seconds, UnitTestResult(TestName("rita"), 0.01.seconds, Result.Fail("her!", "sue")) :: Nil)
    }
    "parse a skip" in {
      parseTestSuite(
        <testsuite name="bob" time="0.10">
          <testcase name="rita" class="com.Rita" time="0.01">
            <skipped/>
          </testcase>
        </testsuite>) shouldBe TestSuiteResult(TestClass("bob"), 0.10.seconds, UnitTestResult(TestName("rita"), 0.01.seconds, Result.Skipped) :: Nil)
    }
    "load the sample file" in {
      val source = Paths.get("src/test/resources/TEST-hello.MySpec.xml")
      val testSuiteResult = parseTestSuite(XML.loadFile(source.toFile))
      testSuiteResult.testCases.map(_.name.value) shouldBe List("bob", "rita", "sue")
    }
    "load one of the examples without exception" in {
      import scala.collection.JavaConversions._
      val files = Files.newDirectoryStream(Paths.get("src/test/examples/akka-streams")).toSeq
      forAll(Gen.oneOf(files)) { source =>
        noException shouldBe thrownBy(parseTestSuite(XML.loadFile(source.toFile)))
      }
    }
  }
}

object ScalaTestSpec {
  private val anyNonEmptyString = Gen.uuid.map(_.toString)
  val anyRandomPath: Gen[Path] = anyNonEmptyString.map(Paths.get(_))
  val anyDiscoverySuite: Gen[Path] = Gen.uuid.map(u => Paths.get(s"TEST-org.scalatest.tools.DiscoverySuite-$u.xml"))
  val anyTestSuite: Gen[Path] = for {
    className <- Gen.identifier
  } yield Paths.get(s"TEST-$className.xml")
}

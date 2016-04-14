package com.github.maiflai.sonar

import java.nio.file.Paths

import com.github.maiflai.sonar.Result.{Fail, Pass, Skipped}
import com.github.maiflai.sonar.ScalaTestSensor._
import com.github.maiflai.sonar.ScalaTestSensorTest._
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpec}
import org.sonar.api.batch.fs._
import org.sonar.api.batch.fs.internal.DefaultFileSystem
import org.sonar.api.component.{Component, Perspective, ResourcePerspectives}
import org.sonar.api.resources.Resource

class ScalaTestSensorTest extends WordSpec with PropertyChecks with Matchers {

  "resultToMessage" should {
    "report null for non-failure" in {
      forAll(anyNonFailure) { r =>
        resultToMessage(r) shouldBe null
      }
    }
    "report message for failure" in {
      forAll(anyFail) { f =>
        resultToMessage(f) shouldBe f.message
      }
    }
  }

  "resultToStatus" should {
    "always report something" in {
      forAll(anyResult) { r =>
        resultToStatus(r) should not be null
      }
    }
  }

  "resultToStackTrace" should {
    "report null for non-failure" in {
      forAll(anyNonFailure) { r =>
        resultToStackTrace(r) shouldBe null
      }
    }
    "report message for failure" in {
      forAll(anyFail) { f =>
        resultToStackTrace(f) shouldBe f.stackTrace
      }
    }
  }

  "toString" should {
    "contain only alphabetical characters as it is used by the logger" in {
      new ScalaTestSensor(UnimplementedResourcePerspectives, CurrentFileSystem).toString should fullyMatch regex "[a-zA-Z]+"
    }
  }

}

object ScalaTestSensorTest {

  //noinspection NotImplementedCode
  private object UnimplementedResourcePerspectives extends ResourcePerspectives {
    override def as[P <: Perspective[_ <: Component]](perspectiveClass: Class[P], resource: Resource): P = ???

    override def as[P <: Perspective[_ <: Component]](perspectiveClass: Class[P], inputPath: InputPath): P = ???

    override def as[P <: Perspective[_ <: Component]](perspectiveClass: Class[P], component: Component): P = ???
  }
  private def CurrentFileSystem = new DefaultFileSystem(Paths.get("."))

  val anyFail = for {
    m <- Gen.identifier
    s <- Gen.identifier
  } yield Fail(m, s)
  val anyPass = Gen.delay(Pass)
  val anySkipped = Gen.delay(Skipped)

  val anyResult: Gen[Result] = Gen.oneOf(anyPass, anySkipped, anyFail)
  val anyNonFailure: Gen[Result] = Gen.oneOf(anyPass, anySkipped)
}

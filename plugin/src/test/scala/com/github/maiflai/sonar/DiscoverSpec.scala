package com.github.maiflai.sonar

import java.nio.file.{Files, Paths}

import org.scalatest.{FunSuite, Matchers}

class DiscoverSpec extends FunSuite with Matchers {

  test("discover src/test/resources") {
    discoverXmlFiles(Paths.get("src/test/resources")) shouldBe Seq(Paths.get("src/test/resources/TEST-hello.MySpec.xml"))
  }

  test("discover an empty directory") {
    discoverXmlFiles(Files.createTempDirectory("discover-spec")) shouldBe Seq.empty
  }

}

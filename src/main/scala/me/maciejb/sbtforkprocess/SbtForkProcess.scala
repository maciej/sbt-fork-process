package me.maciejb.sbtforkprocess

import sbt.Def.Initialize
import sbt.Keys._
import sbt._

class SbtForkProcess {

  private[this] def classpathOption(classpath: Seq[File]) = "-classpath" :: Path.makeString(classpath) :: Nil
  private[this] def scalaOptions(mainClass: String): Initialize[Task[List[String]]] = fullClasspath map { (cp) =>
    classpathOption(Attributed.data(cp)) ::: mainClass :: Nil
  }
  private[this] def forkOptions: Initialize[Task[ForkOptions]] =
    (baseDirectory, javaOptions, outputStrategy, envVars, javaHome, connectInput) map {
      (base, options, strategy, env, javaHomeDir, connectIn) =>
        // bootJars is empty by default because only jars on the user's classpath should be on the boot classpath
        ForkOptions(bootJars = Nil, javaHome = javaHomeDir, connectInput = connectIn, outputStrategy = strategy,
          runJVMOptions = options, workingDirectory = Some(base), envVars = env)
    }

  def forkRunClass(mainClass: String): Initialize[Task[Process]] = Def.task {
    Fork.java.fork(forkOptions.value, scalaOptions(mainClass).value)
  }

}

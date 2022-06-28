#!/usr/bin/env -S scala-cli shebang

import io.Source._

val includeRegex = """<([A-z0-9/]+\.scala)>""".r

def read(file: String) = fromFile(file).mkString

println(
    includeRegex.replaceAllIn(
        read(args(0)),
        m => s"```scala\n${read(m.group(1))}```\n<div class=\"code-source\"><small>Source: [`${m.group(1)}`](${m.group(1)})</small></div>"
    )
)

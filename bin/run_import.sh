#!/usr/bin/env bash
CWD="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

ROOT_DIR=$(realpath $CWD/..)
JAR=$ROOT_DIR/target/scala-2.13/ArangoQuery-assembly-0.1.jar

MAIN_CLASS=vn.vietvu.arango.ImportGraphData

java -cp $JAR: $MAIN_CLASS

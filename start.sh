#!/usr/bin/env bash
sbt -jvm-debug "8000" -Dlogger.file=conf/logger.xml -Dhttps.port="10000" "run 9000";
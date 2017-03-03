
@echo off
REM This script will add logback jars to your classpath.

set LB_HOME=c:/SET/THIS/PARAMETER/TO/THE/FOLDER/WHERE/YOU/INSTALLED/LOGBACK
REM echo %LB_HOME%

set CLASSPATH=%CLASSPATH%;%LB_HOME%/logback-classic-1.1.9.jar
set CLASSPATH=%CLASSPATH%;%LB_HOME%/logback-core-1.1.9.jar
set CLASSPATH=%CLASSPATH%;%LB_HOME%/logback-examples/logback-examples-1.1.9.jar
set CLASSPATH=%CLASSPATH%;%LB_HOME%/logback-examples/lib/slf4j-api-1.7.22.jar

REM echo %CLASSPATH%

#!/bin/bash

cd ../game/
sbt "~;jetty:stop;jetty:start"

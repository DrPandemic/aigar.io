FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.17_8_1.8.3_2.13.10

RUN mkdir -p /game/target
WORKDIR /game

COPY ./src/ /game/src/
COPY ./project/ /game/project/
COPY ./.lib/ /game/.lib/

RUN sbt jetty:start

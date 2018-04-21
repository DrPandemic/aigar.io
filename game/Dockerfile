FROM vadivelk/alpine-sbt

RUN mkdir /game
RUN mkdir /game/target
WORKDIR /game

COPY ./src/ /game/src/
COPY ./project/ /game/project/

RUN sbt jetty:start
FROM gafiatulin/alpine-sbt

RUN mkdir /game
WORKDIR /game

COPY ./ /game/

RUN sbt jetty:start

RUN rm -rf /game/*
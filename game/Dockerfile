FROM gafiatulin/alpine-sbt

RUN mkdir /game
RUN mkdir /game/target
WORKDIR /game

COPY ./src/ /game/src/
COPY ./project/ /game/project/

RUN echo 'SBT_OPTS="$SBT_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=1099"' > /root/.sbtconfig
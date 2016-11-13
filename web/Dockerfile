FROM cusspvz/node:latest

RUN mkdir /src
WORKDIR /src

COPY package.json /src/package.json
RUN \
  apk update && \
  apk add $BUILD_APKS && \
  npm install node-sass && \
  apk del $BUILD_APKS

RUN npm install
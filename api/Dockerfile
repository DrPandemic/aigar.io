FROM nginx

RUN mkdir -p /src/dist/web
RUN mkdir /etc/nginx/logs/

COPY api/conf.d/* /etc/nginx/conf.d/
COPY api/mime_types /etc/nginx/mime_types
COPY web/dist/* /src/dist/web/

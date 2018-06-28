FROM funkygibbon/nginx-pagespeed

RUN mkdir -p /src/dist/web
RUN mkdir /etc/nginx/logs/

COPY api/sites-enabled/* /etc/nginx/sites-enabled/
COPY api/mime_types /etc/nginx/mime_types
COPY web/dist/* /src/dist/web/

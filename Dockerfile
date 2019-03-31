FROM nginx:alpine
COPY default.conf /etc/nginx/conf.d/default.conf
COPY resources/public/index.html /usr/share/nginx/html/index.html
COPY resources/public/*.jpg /usr/share/nginx/html/
RUN mkdir -p /usr/share/nginx/html/js/compiled
COPY resources/public/js/compiled/app.js /usr/share/nginx/html/js/compiled/

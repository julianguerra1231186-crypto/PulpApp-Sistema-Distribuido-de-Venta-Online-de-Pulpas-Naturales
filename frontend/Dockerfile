FROM nginx:alpine

# Eliminar config default de nginx
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copiar configuración nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar archivos estáticos del frontend
COPY . /usr/share/nginx/html

# Limpiar archivos Docker del directorio estático
RUN rm -f /usr/share/nginx/html/Dockerfile \
          /usr/share/nginx/html/docker-entrypoint.sh \
          /usr/share/nginx/html/nginx.conf \
          /usr/share/nginx/html/.dockerignore

# Copiar entrypoint
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]

# =========================================================
# Dockerfile — Frontend Zentrix (Nginx)
# =========================================================
# Sirve archivos estáticos via Nginx.
#
# COMPATIBILIDAD:
#   - Docker Compose: escucha en puerto 80 (default)
#   - Cloud Run: escucha en $PORT (inyectado por la plataforma)
#
# El script docker-entrypoint.sh reemplaza el puerto en
# nginx.conf antes de iniciar nginx.
# =========================================================

FROM nginx:alpine

# Copiar archivos estáticos del frontend
COPY . /usr/share/nginx/html

# Copiar configuración nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar y dar permisos al entrypoint
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Limpiar archivos que no son del frontend
RUN rm -f /usr/share/nginx/html/Dockerfile \
          /usr/share/nginx/html/docker-entrypoint.sh \
          /usr/share/nginx/html/nginx.conf \
          /usr/share/nginx/html/.dockerignore

# Cloud Run inyecta PORT; default 80 para Docker Compose
ENV PORT=80

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]

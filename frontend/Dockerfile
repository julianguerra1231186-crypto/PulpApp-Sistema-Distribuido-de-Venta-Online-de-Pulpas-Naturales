# =========================================================
# Dockerfile — Frontend Zentrix (Nginx + Cloud Run)
# =========================================================
#
# PROBLEMA ANTERIOR:
#   1. nginx.conf tenía proxy_pass a "api-gateway:8080" que no
#      se resuelve en Cloud Run → nginx crasheaba al arrancar.
#   2. docker-entrypoint.sh tenía línea vacía antes del shebang.
#
# SOLUCIÓN:
#   - nginx.conf solo sirve estáticos (sin proxy).
#   - Entrypoint limpio: exec nginx -g 'daemon off;'
#   - Puerto fijo 8080 (Cloud Run inyecta PORT=8080).
#
# COMPATIBILIDAD:
#   - Cloud Run: escucha en 8080, sirve estáticos, JS llama
#     directamente al gateway por HTTPS.
#   - Docker Compose: mapea 3000:8080, JS llama a localhost:8090
#     (gateway expuesto en el host).
#
# =========================================================

FROM nginx:alpine

# Quitar config default de nginx
RUN rm -f /etc/nginx/conf.d/default.conf

# Copiar configuración personalizada
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar archivos estáticos
COPY . /usr/share/nginx/html

# Limpiar archivos de infraestructura del directorio público
RUN rm -f /usr/share/nginx/html/Dockerfile \
          /usr/share/nginx/html/docker-entrypoint.sh \
          /usr/share/nginx/html/nginx.conf \
          /usr/share/nginx/html/.dockerignore

# Copiar y dar permisos al entrypoint
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Puerto que Cloud Run espera (PORT=8080)
EXPOSE 8080

# Nginx en foreground — requerido por Cloud Run
ENTRYPOINT ["/docker-entrypoint.sh"]

# =========================================================
# Dockerfile — Frontend (Nginx)
# =========================================================
# Sirve archivos estáticos via Nginx.
# El proxy /api/ redirige al API Gateway internamente.
#
# COMPATIBILIDAD:
#   - Docker Compose: se conecta a api-gateway via red interna
#   - Cloud Run: se despliega como servicio independiente
# =========================================================
FROM nginx:alpine
COPY . /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

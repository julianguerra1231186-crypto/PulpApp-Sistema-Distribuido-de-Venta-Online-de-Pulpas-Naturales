#!/bin/sh
# =========================================================
# docker-entrypoint.sh — Frontend Zentrix
# =========================================================
# Reemplaza LISTEN_PORT en nginx.conf con el valor de $PORT.
# Cloud Run inyecta $PORT automáticamente.
# En Docker Compose, $PORT no existe y se usa 80 por defecto.
# =========================================================

PORT="${PORT:-80}"

sed -i "s/LISTEN_PORT/$PORT/g" /etc/nginx/conf.d/default.conf

echo "[Zentrix Frontend] Starting nginx on port $PORT"

exec nginx -g "daemon off;"

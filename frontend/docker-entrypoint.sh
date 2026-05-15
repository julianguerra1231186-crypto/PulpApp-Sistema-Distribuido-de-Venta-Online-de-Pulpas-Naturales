#!/bin/sh
# =========================================================
# docker-entrypoint.sh — Frontend Zentrix
# =========================================================
# Inicia nginx en foreground (requerido por Cloud Run y Docker).
# Cloud Run necesita que el proceso principal NO se demonice.
# =========================================================
exec nginx -g 'daemon off;'

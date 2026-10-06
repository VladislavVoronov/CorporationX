#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
services=(user_service project_service post_service payment_service notification_service analytics_service achievement_service account_service url_shortener_service)
for service in "${services[@]}"; do
  echo "Checking $service"
  (cd "$service" && ./gradlew --no-daemon check bootJar "$@")
done

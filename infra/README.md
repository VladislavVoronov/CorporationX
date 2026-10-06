# Локальная инфраструктура

Из корня проекта:

```bash
./infra/run.sh
./infra/stop.sh
```

PostgreSQL: `localhost:15432`, Redis: `localhost:16379`, MinIO: `http://localhost:19000`, консоль MinIO: `http://localhost:19001`, почта Maildev: `http://localhost:11080` (SMTP: `localhost:11025`). Учётные данные PostgreSQL и MinIO: `user` / `password`, только для локальной разработки.

PostgreSQL создаёт общую базу `corporationx` при первом запуске. Данные хранятся в Docker volumes; `stop.sh` их сохраняет. Контейнеры имеют отдельное имя проекта `corporationx-starter`.

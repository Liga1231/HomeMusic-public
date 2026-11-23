# Руководство по быстрому развертыванию HomeMusic

Форк с оптимизациями производительности, исправлением очередей «Моей Волны» и кэшированием избранного.

---

## 1. Системные требования
- **ОС:** Linux (Debian 11/12, Ubuntu 22.04/24.04 или аналогичные).
- **Пакеты:** `docker`, `docker-compose-plugin`, `git`, `python3`, `ffmpeg`, `curl`.
- **Диск:** Рекомендуется SSD (даже недорогой SATA SSD) для быстрой работы SQLite и обложек.

---

## 2. Клонирование репозитория

```bash
git clone https://github.com/Liga1231/HomeMusic-public.git /opt/HomeMusic
cd /opt/HomeMusic
```

---

## 3. Настройка конфигурации (.env)

Скопируйте шаблон конфигурации:
```bash
cp .env.example .env
nano .env
```

**Обязательные параметры в `.env`:**
```env
# Путь к хранилищу музыки (содержит library/, staging/, .trash/, .backups/)
MUSIC_ROOT=/opt/music

# Локальные URL сервера (замените на IP вашей машины или имя Tailscale)
PUBLIC_MUSIC_URL=http://<IP-СЕРВЕРА>:4533
PUBLIC_HUB_URL=http://<IP-СЕРВЕРА>:8080

# URL сервиса векторов (Embedder)
EMBEDDER_URL=http://host.docker.internal:8090

# Опционально: прокси для скачивания с YouTube / SoundCloud при блокировках
OUTBOUND_PROXY=http://host.docker.internal:2080
OUTBOUND_PROXY_HOSTS=youtube.com,youtu.be,googlevideo.com,youtube-nocookie.com,ytimg.com,soundcloud.com,sndcdn.com
```

Создайте директории под музыку:
```bash
sudo mkdir -p /opt/music/library /opt/music/staging /opt/music/.trash /opt/music/.backups
sudo chmod -R 777 /opt/music
```

---

## 4. Настройка Embedder (Векторный поиск вкуса на CPU)

Для серверов без дискретной видеокарты NVIDIA эмбеддер работает на CPU через systemd:

```bash
# 1. Создание venv и установка зависимостей
sudo python3 -m venv /opt/embedder_venv
sudo /opt/embedder_venv/bin/pip install --upgrade pip
sudo /opt/embedder_venv/bin/pip install -r embedder/requirements.txt

# 2. Установка systemd сервиса
sudo cp scripts/homemusic-embedder.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now homemusic-embedder.service

# 3. Проверка статуса
sudo systemctl status homemusic-embedder.service
```

---

## 5. Запуск основных контейнеров (Hub + Navidrome)

```bash
docker compose up -d --build
```

Проверка запущенных контейнеров:
```bash
docker compose ps
```
Должны работать:
* `homemusic_hub` (порт 8080)
* `homemusic_navidrome` (порт 4533)
* `homemusic_youtube_pot` (вспомогательный сервис YouTube)

---

## 6. Первичный вход и создание пользователя

1. Откройте в браузере Web-интерфейс:
   `http://<IP-СЕРВЕРА>:8080`
2. Зарегистрируйте администратора (аккаунт автоматически свяжется с Navidrome).
3. Добавьте любимые треки или запустите импорт из Яндекс.Музыки / Spotify.

---

## 7. Подключение мобильного приложения (Tempus / HomeMusic)

1. Установите APK из папки `android/` или релизов.
2. В приложении укажите URL сервера:
   * **URL:** `http://<IP-СЕРВЕРА>:8080` (или домен Tailscale)
   * **Логин / Пароль:** ваш созданный аккаунт.
3. В настройках звука для мобильной сети и Wi-Fi выберите:
   * **Кодек транскодирования:** `Opus`
   * **Битрейт:** `160` (или `128`) для экономии 50% трафика и плавного воспроизведения.

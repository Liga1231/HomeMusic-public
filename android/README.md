# HomeMusic для Android

HomeMusic — Android-клиент на основе [Tempus](https://github.com/eddyizm/tempus) для своего сервера Navidrome и Хаба HomeMusic.
Библиотека, очередь и плеер работают нативно. Каталог, загрузки, ассистент и свои файлы открываются через PWA Хаба внутри
приложения. Дополнения HomeMusic включают миксы, «Мою волну», умное перемешивание, офлайн-синхронизацию и обновления через Хаб.

В этом каталоге находятся исходники приложения. Настройка сервера описана в [корневом README](../README.md), контракт Хаба —
в [hub/API.md](../hub/API.md). Происхождение снимка и отличия публичной конфигурации — в [SOURCE.md](SOURCE.md).

## Требования

| Компонент | Версия |
|---|---|
| JDK для Gradle и компиляции | 21 |
| Android SDK Platform (`compileSdk`) | 37 |
| Android SDK Build-Tools | 36.1.0 |
| Минимальная версия Android на устройстве | 7.0 / API 24 |

Используйте включённый Gradle Wrapper (`gradlew` / `gradlew.bat`). Версии Gradle, Android Gradle Plugin и зависимостей
зафиксированы в исходниках. SDK можно установить через Android Studio; путь задайте в локальном `local.properties`
(`sdk.dir=...`) или через `ANDROID_HOME`. Сам файл `local.properties` не добавляется в Git.

## Адреса своего сервера

В публичной сборке все адреса по умолчанию пустые. Создайте локальный файл настроек в каталоге `android/`:

```powershell
Copy-Item homemusic.properties.example homemusic.properties
```

Заполните его адресами своего развёртывания. Файл `homemusic.properties` исключён из Git.

| Ключ | Назначение | Пример |
|---|---|---|
| `hubUrl` | Публичный адрес Хаба | `https://hub.example.com` |
| `hubLocalUrl` | Необязательный адрес Хаба в домашней сети | `http://192.0.2.10:8080` |
| `musicUrl` | Предложенный при входе адрес Navidrome | `https://music.example.com` |
| `musicLocalUrl` | Необязательный домашний адрес Navidrome | `http://192.0.2.10:4533` |

Адреса из таблицы — примеры. Замените их своими; ненужные локальные адреса оставьте пустыми. Логины, пароли и токены
в этот файл не нужны. Во время работы приложение предпочитает доступный локальный адрес соответствующему публичному.

Каждый адрес можно переопределить на один запуск Gradle: `-PhmHubUrl`, `-PhmHubLocalUrl`, `-PhmMusicUrl`, `-PhmMusicLocalUrl`.
Например:

```powershell
.\gradlew assembleHomemusicDebug "-PhmHubUrl=https://hub.example.com" "-PhmMusicUrl=https://music.example.com"
```

На первом экране можно ввести адрес своего Navidrome вручную. Адреса Хаба доступны после входа в настройках → HomeMusic.
Для регистрации по приглашению, почте или через Google настройте адреса Хаба и Navidrome перед сборкой: эти варианты
показываются только при поддержке соответствующим Хабом. Google дополнительно требует собственной настройки OAuth,
описанной в [корневом README](../README.md#вход-через-google-по-желанию-разовая-настройка-15-минут).

## Тесты и debug APK

Из каталога `android/` в PowerShell:

```powershell
.\gradlew testHomemusicDebugUnitTest assembleHomemusicDebug
```

В Linux/macOS используются те же задачи:

```bash
./gradlew testHomemusicDebugUnitTest assembleHomemusicDebug
```

Готовые APK находятся в `app/build/outputs/apk/homemusic/debug/`; для обычной установки подходит универсальный APK.
Debug-сборка имеет пакет `app.homemusic.debug` и устанавливается рядом с релизным приложением `app.homemusic`.
Её аккаунты, библиотека на устройстве и настройки хранятся отдельно.

Исходники сохраняют также flavor `tempus` и `degoogled`. HomeMusic-функции включены во flavor `homemusic`.

## Release APK и подпись

Для сборки release:

```powershell
.\gradlew assembleHomemusicRelease
```

Результат находится в `app/build/outputs/apk/homemusic/release/`. Без `keystore.properties` он остаётся **неподписанным**
и не готов к установке. Для собственного выпуска создайте свой ключ подписи, скопируйте `keystore.properties.example`
в `keystore.properties` и укажите путь к хранилищу, alias и пароли. Эти файлы и каталог `keystore/` исключены из Git.
Ключ официальных выпусков в исходники не входит.

Версию можно задать параметрами `-PhmVersionCode` и `-PhmVersionName`:

```powershell
.\gradlew assembleHomemusicRelease "-PhmVersionCode=1" "-PhmVersionName=1.0.0"
```

Сохраняйте один ключ для своих последующих выпусков и увеличивайте `versionCode`. APK с другим ключом нельзя установить
обновлением поверх официального приложения. Для знакомства с исходниками используйте отдельную debug-сборку.
Встроенный установщик обновлений проверяет пакет и подпись: обновление через ваш Хаб также должно быть подписано
тем же ключом, что установленное приложение.

## Вклад и лицензия

Правила изменений и проверок — в [CONTRIBUTING.md](CONTRIBUTING.md). Android-приложение распространяется на условиях
[GNU General Public License v3.0](LICENSE). Оно основано на работе авторов Tempus и Tempo; исходный README с полным
разделом Credits сохранён в [README.tempus.md](README.tempus.md#credits), рекомендации апстрима —
в [CONTRIBUTING.tempus.md](CONTRIBUTING.tempus.md).

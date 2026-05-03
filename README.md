# Автоматизация тестирования веб‑сервиса покупки тура

## Короткое описание проекта

Проект реализует автоматизацию тестирования веб‑сервиса для покупки туров с оплатой дебетовой картой или оформлением
кредита. Приложение взаимодействует с банковскими сервисами (Payment Gate и Credit Gate) и сохраняет результаты операций
в СУБД.

Реализованы:

- UI автотесты (Selenide, Page Object)
- API автотесты (REST Assured)
- Проверка состояния базы данных (MySQL / PostgreSQL)
- Формирование отчетов Allure

---

## Начало работы

Проект запускается локально.  
Необходимо последовательно поднять базу данных, запустить приложение и выполнить тесты.

---

## 1. Предварительные условия

Для работы с проектом необходимо установить:

- ☕ Java 11
- 🐙 Git
- 🐳 Docker Desktop
- 🌐 Google Chrome / Chromium
- 🧠 IntelliJ IDEA (рекомендуется, для скачивания программы переходим по ссылке:https://www.jetbrains.com/ru-ru/idea/
  и устанавливаем под свою ОС)
- 🏗 Gradle (используется wrapper ./gradlew, установка не требуется)

---

### ☕ Установка Java 11

Официальные источники:

- Oracle JDK: https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
- OpenJDK (рекомендуется): https://adoptium.net/
- Выбираем дистрибутив под свою ОС

---

### Настройка JAVA_HOME:

### 🪟 Windows

1. Установить Java 11
2. Путь установки (пример): C:\Program Files\Java\jdk-11


3. Перейти:

Панель управления → Система → Дополнительные параметры системы → Переменные среды

4. Создать переменную:

JAVA_HOME = C:\Program Files\Java\jdk-11

5. Добавить в PATH:

%JAVA_HOME%\bin

6. Проверка:

```
java -version
echo %JAVA_HOME%
```

---

### 🍎 macOS

#### Определение пути к Java

Выполните команду, чтобы проверить доступные версии Java:

```
/usr/libexec/java_home -V
```

#### Добавление переменных окружения в ~/.zshrc

1. Откройте файл ~/.zshrc в текстовом редакторе (например, с помощью nano):

```
nano ~/.zshrc
```

2. Добавьте следующие строки:

```
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
export PATH=$JAVA_HOME/bin:$PATH
```

3. Сохраните изменения и закройте редактор:

- для nano: нажмите Ctrl + O, затем Enter для сохранения, далее Ctrl + X для выхода.

#### Применение изменений

- Примените внесённые изменения, перезагрузив конфигурацию оболочки:

```
source ~/.zshrc
```

#### Проверка настройки

- Убедитесь, что Java настроена корректно:

```
java -version
echo $JAVA_HOME
```

---

### 🐧 Linux (Например: Ubuntu / Debian)

#### Установка OpenJDK 11

1. **Обновите список пакетов:**

   ```
   sudo apt update
   ```
2. **Установите OpenJDK 11:**

```
sudo apt install openjdk-11-jdk
```

3. **Настройка переменных окружения в**```~/.bashrc```

- Откройте файл ~/.bashrc в текстовом редакторе:

```
nano ~/.bashrc
```

- Добавьте следующие строки в конец файла:

```
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

- Сохраните изменения и закройте редактор:

    - Нажмите Ctrl + O, затем Enter для сохранения файла.
    - Нажмите Ctrl + X, чтобы выйти из редактора nano.

4. **Применение изменений**

- Перезагрузите конфигурацию оболочки, чтобы применить изменения:

```
source ~/.bashrc
```

5. **Проверка настройки**

- Убедитесь, что Java установлена и настроена правильно:

1. Проверьте версию Java:

```
java -version
```

2. Проверьте значение переменной JAVA_HOME:

```
echo $JAVA_HOME
```

3. Проверьте, какая версия Java используется по умолчанию:

```
which java
```

### Дополнительные рекомендации и диагностика на Linux:

1. **Если путь к ```JAVA_HOME``` не найден**
    - Путь ```/usr/lib/jvm/java-11-openjdk-amd64``` может отличаться в зависимости от архитектуры системы.

---

2. **Чтобы найти установленный JDK, выполните:**
   ```
   ls /usr/lib/jvm/
   ```

---

3. **Используйте подходящий путь в команде ```export JAVA_HOME```:**
    - для ARM64: /usr/lib/jvm/java-11-openjdk-arm64;
    - для других архитектур: ищите папку, содержащую java-11.

---

4. **Проверка альтернативных версий Java**
    - Если у вас установлено несколько версий Java, выберите нужную с помощью:
   ```
   sudo update-alternatives --config java
   ```
   Команда покажет список доступных версий и позволит выбрать активную.

---

5. **Настройка для всех пользователей системы**
    - Вместо редактирования ```~/.bashrc``` настройте переменные окружения глобально:
        1. Создайте файл в ```/etc/profile.d/```:
      ```
      sudo nano /etc/profile.d/java.sh
      ```
        2. Добавьте строки:
      ```
      export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
      export PATH=$JAVA_HOME/bin:$PATH
      ```
        3. Сделайте файл исполняемым:
      ```
      sudo chmod +x /etc/profile.d/java.sh
      ```
        4. Примените изменения:
      ```
      source /etc/profile
      ```

---

### 🐙 Git

Официальный сайт:
https://git-scm.com/downloads

---

#### 🪟 Windows

Скачать установщик и установить:
https://git-scm.com/download/win

Проверка:

```
git --version
```

#### 🍎 macOS

Установить через Homebrew:

```
brew install git
```

Проверка:

```
git --version
```

#### 🐧 Linux (Например: Ubuntu / Debian)

Установить через Terminal:

```
sudo apt update
sudo apt install git
```

Проверка:

```
git --version
```

---

### 🐳 Docker Desktop

Официальный сайт:
https://www.docker.com/products/docker-desktop/
---

#### 🪟 Windows / 🍎 macOS

Скачать и установить:
https://www.docker.com/products/docker-desktop/

После установки запустить Docker Desktop.
Проверка:

```
docker --version
docker compose version
```

#### 🐧 Linux (Например: Ubuntu / Debian)

Установка Docker Engine:
https://docs.docker.com/engine/install/

Проверка:

```
docker --version
docker compose version
```

---

### 🌐 Google Chrome / Chromium

Официальный сайт Chrome:
https://www.google.com/chrome/

Chromium:
https://www.chromium.org/getting-involved/download-chromium/
---

#### 🪟 Windows / 🍎 macOS

Скачать установщик с сайта и установить стандартным способом.

#### 🐧 Linux

```
sudo apt update
sudo apt install google-chrome-stable
```

Проверка:

```
google-chrome --version
# или
chromium-browser --version
```

---

### 🧠 IntelliJ IDEA

Официальный сайт:
https://www.jetbrains.com/idea/download/
---

#### 🪟 Windows / 🍎 macOS / 🐧 Linux

- Скачать Community Edition (достаточно)
  Установить как обычное приложение

Проверка:

- открыть любой Java проект

---

### 🏗 Gradle (не требуется установка)

Проект использует Gradle Wrapper.
Проверка:

#### 🪟 Windows:

```
gradlew -v
```

#### 🍎 macOS / 🐧 Linux:

```
./gradlew -v
```

---

## 2. Установка и запуск

### 1. Клонирование проекта для 🪟 Windows/🍎 macOS/🐧 Linux

   ```
   git clone https://github.com/ValentinKnArt30/kursovaya
   cd kursovaya
   ```

---

### 2. Запуск базы данных (🐳 Docker) 🪟 Windows/🍎 macOS/🐧 Linux

#### 🐘 PostgreSQL

   ```
   docker compose up -d postgres
   ```

#### 🐬 MySQL

   ```
   docker compose up -d mysql
   ```

---

### 3. Запуск приложения

### Для 🪟 Windows

#### 🐘 PostgreSQL

- однострочный вариант (Windows cmd / PowerShell):

   ```
   java -Ddb.url=jdbc:postgresql://localhost:5432/aqashop -Ddb.user=test -Ddb.password=test -jar aqa-shop.jar
   ```

- многострочный PowerShell вариант:

   ```
   java -Ddb.url=jdbc:postgresql://localhost:5432/aqashop `
   -Ddb.user=test `
   -Ddb.password=test `
   -jar aqa-shop.jar
   ```

#### 🐬 MySQL

- однострочный вариант (Windows cmd / PowerShell):

   ```
   java -Ddb.url=jdbc:mysql://localhost:3306/aqashop -Ddb.user=test -Ddb.password=test -jar aqa-shop.jar
   ```

- многострочный PowerShell вариант:

   ```
   java -Ddb.url=jdbc:mysql://localhost:3306/aqashop `
   -Ddb.user=test `
   -Ddb.password=test `
   -jar aqa-shop.jar
   ```

### Для 🐧 Linux/🍎 macOS

#### 🐘 PostgreSQL

   ```
   java -Ddb.url=jdbc:postgresql://localhost:5432/aqashop \
   -Ddb.user=test \
   -Ddb.password=test \
   -jar aqa-shop.jar
   ```

#### 🐬 MySQL

   ```
   java -Ddb.url=jdbc:mysql://localhost:3306/aqashop \
   -Ddb.user=test \
   -Ddb.password=test \
   -jar aqa-shop.jar
   ```

---

### 4. Запуск автотестов

### Для 🪟 Windows

#### 🐘 PostgreSQL

- однострочный вариант (Windows cmd / PowerShell):

   ```
   gradlew.bat test -Ddb.url=jdbc:postgresql://localhost:5432/aqashop -Ddb.user=test -Ddb.password=test
   ```

- многострочный PowerShell вариант:

   ```
   gradlew.bat test `
   -Ddb.url=jdbc:postgresql://localhost:5432/aqashop `
   -Ddb.user=test `
   -Ddb.password=test
   ```

#### 🐬 MySQL

- однострочный вариант (Windows cmd / PowerShell):

   ```
   gradlew.bat test -Ddb.url=jdbc:mysql://localhost:3306/aqashop -Ddb.user=test -Ddb.password=test
   ```

- многострочный PowerShell вариант:

   ```
   gradlew.bat test `
   -Ddb.url=jdbc:mysql://localhost:3306/aqashop `
   -Ddb.user=test `
   -Ddb.password=test
   ```

### Для 🐧 Linux/🍎 macOS

#### 🐘 PostgreSQL

   ```
   ./gradlew test \
   -Ddb.url=jdbc:postgresql://localhost:5432/aqashop \
   -Ddb.user=test \
   -Ddb.password=test
   ```

#### 🐬 MySQL

   ```
   ./gradlew test \
   -Ddb.url=jdbc:mysql://localhost:3306/aqashop \
   -Ddb.user=test \
   -Ddb.password=test
   ```

---

### 5. Отчет 🧪 Allure

### Для 🪟 Windows

   ```
   gradlew.bat allureServe
   ```

- Откроется браузер с отчетом Allure, если не открылся, то перейти по ссылке представленном в логе отработки Allure

### Для 🐧 Linux/🍎 macOS

   ```
   ./gradlew allureServe
   ```

- Перейти по ссылке представленном в логе отработки Allure

---

### Важно

Перед запуском тестов убедитесь:

- Docker контейнер с БД запущен
- приложение (jar) запущено
- выбран правильный тип БД (PostgreSQL или MySQL)

---

## 3. Примеры полного запуска

---

#### 🐘 PostgreSQL (полный цикл)

#### 🐧 Linux / 🍎 macOS

```
1. docker compose up -d postgres

2. java -Ddb.url=jdbc:postgresql://localhost:5432/aqashop \
-Ddb.user=test \
-Ddb.password=test \
-jar aqa-shop.jar

3. ./gradlew test \
-Ddb.url=jdbc:postgresql://localhost:5432/aqashop \
-Ddb.user=test \
-Ddb.password=test

4. ./gradlew allureServe
```

#### 🪟 Windows

- PowerShell / CMD (смешанный, но корректный вариант):

 ```
1. docker compose up -d postgres
 
2. java -Ddb.url=jdbc:postgresql://localhost:5432/aqashop -Ddb.user=test -Ddb.password=test -jar aqa-shop.jar
 
3. gradlew.bat test -Ddb.url=jdbc:postgresql://localhost:5432/aqashop -Ddb.user=test -Ddb.password=test
 
4. gradlew.bat allureServe
 ```

- PowerShell многострочный:

 ```
1. docker compose up -d postgres

2. java -Ddb.url=jdbc:postgresql://localhost:5432/aqashop `
-Ddb.user=test `
-Ddb.password=test `
-jar aqa-shop.jar

3. gradlew.bat test `
-Ddb.url=jdbc:postgresql://localhost:5432/aqashop `
-Ddb.user=test `
-Ddb.password=test

4. gradlew.bat allureServe
 ```

---

#### 🐬 MySQL (полный цикл)

#### 🐧 Linux / 🍎 macOS

```
1. docker compose up -d mysql

2. java -Ddb.url=jdbc:mysql://localhost:3306/aqashop \
-Ddb.user=test \
-Ddb.password=test \
-jar aqa-shop.jar

3. ./gradlew test \
-Ddb.url=jdbc:mysql://localhost:3306/aqashop \
-Ddb.user=test \
-Ddb.password=test

4. ./gradlew allureServe
```

#### 🪟 Windows

- PowerShell / CMD (смешанный, но корректный вариант):

 ```
1. docker compose up -d mysql

2. java -Ddb.url=jdbc:mysql://localhost:3306/aqashop -Ddb.user=test -Ddb.password=test -jar aqa-shop.jar

3. gradlew.bat test -Ddb.url=jdbc:mysql://localhost:3306/aqashop -Ddb.user=test -Ddb.password=test

4. gradlew.bat allureServe
 ```

- PowerShell многострочный:

 ```
1. docker compose up -d mysql

2. java -Ddb.url=jdbc:mysql://localhost:3306/aqashop `
-Ddb.user=test `
-Ddb.password=test `
-jar aqa-shop.jar

3. gradlew.bat test `
-Ddb.url=jdbc:mysql://localhost:3306/aqashop `
-Ddb.user=test `
-Ddb.password=test

4. radlew.bat allureServe
 ```

---

## 4. Структура проекта

```
src
├── test
│    ├── java
│    │    ├── ru
│    │    │    └── netology
│    │    │    │    │    ├── data
│    │    │    │    │    │    ├── API.java
│    │    │    │    │    │    ├── DataGenerator.java
│    │    │    │    │    │    └── SQL.java
│    │    │    │    │    ├── pages
│    │    │    │    │    │    ├── CreditPage.java
│    │    │    │    │    │    ├── MainPage.java
│    │    │    │    │    │    └── PaymentPage.java
│    │    │    │    │    ├── tests
│    │    │    │    │    │    ├── APITest.java
│    │    │    │    │    │    └── UITest.java

reports
├── AllureReport.png
├── Plan.md
├── Report.md
└── Summary.md
```

---

## 5. Лицензия

Проект выполнен в учебных целях в рамках курса по автоматизации тестирования.

Использование разрешено без ограничений.

---
[![kursovaya](https://github.com/ValentinKnArt30/kursovaya/actions/workflows/gradle.yml/badge.svg)](https://github.com/ValentinKnArt30/kursovaya/actions/workflows/gradle.yml)
# tpo-3 — Функциональное UI-тестирование tutu.ru

Лабораторная работа по тестированию: 12 E2E-сценариев на **Selenium WebDriver** с использованием паттерна **Page Object**, параллельный запуск в Chrome и Firefox.

## Что внутри

- **11 use cases** — реальные пользовательские путешествия по сайту tutu.ru: поиск ж/д билетов с фильтром «Сапсан», поиск авиабилетов с календарём и фильтром «С багажом», поиск автобусов / отелей / электричек, открытие модалки авторизации, поиск в справочной + один негативный сценарий. Все каталожные тесты (Авиа / Ж/д / Автобусы / Отели) стартуют с главной `tutu.ru` и переключаются на нужный каталог кликом по вкладке формы.
- **Page Object** — 13 страничных классов в `src/test/java/pages/`, локаторы только через **XPath** (включая `data-ti`-атрибуты и label-based).
- **Только явные ожидания** `WebDriverWait` — никаких `Thread.sleep`.
- **TestNG** — три профиля запуска: parallel / chrome / firefox.

## Структура

```
src/test/java/
├── base/BaseTest.java              # TestNG @Parameters("browser"), single driver, WebDriverWait
├── pages/                          # 13 Page Object'ов
│   ├── Page.java                   # абстрактный базовый
│   ├── HomePage.java
│   ├── TrainPage.java
│   ├── TrainResultsPage.java
│   ├── AviaPage.java
│   ├── AviaResultsPage.java
│   ├── BusPage.java
│   ├── BusResultsPage.java
│   ├── HotelPage.java
│   ├── HotelResultsPage.java
│   ├── ElectrichkaPage.java
│   ├── ElectrichkaResultsPage.java
│   ├── FaqPage.java
│   └── LoginModalPage.java
├── tests/                          # 7 классов с 11 тестовыми методами (UC-01..UC-08, UC-10..UC-12)
│   ├── HomePageTest.java           # UC-10
│   ├── TrainTest.java              # UC-01, UC-02, UC-03
│   ├── AviaTest.java               # UC-04, UC-05, UC-12
│   ├── BusTest.java                # UC-06
│   ├── HotelTest.java              # UC-07
│   ├── ElectrichkaTest.java        # UC-08
│   └── FaqTest.java                # UC-11
└── utils/
    ├── Constants.java
    ├── DateUtils.java
    └── DriverFactory.java          # создание WebDriver по имени браузера

src/test/resources/
├── log4j.properties
├── testng-parallel.xml             # chrome + firefox параллельно
├── testng-chrome.xml               # только chrome
└── testng-firefox.xml              # только firefox

docs/
├── use-cases.md                    # описание 12 прецедентов использования
├── checklist.md                    # чек-лист покрытия
├── scenarios.md                    # пошаговые шаги Given/When/Then с локаторами
├── diagram.puml                    # PlantUML use-case диаграмма
└── diagram.md                      # ASCII-вариант диаграммы
```

## Подготовка к запуску

### Требования

- **Java 21** или новее.
- **Maven 3.6+**.
- **Google Chrome** установлен в системе. Для нестандартного пути к бинарнику — передать `-Dchrome.binary=/path/to/chrome`.
- **Firefox** установлен в системе. Для нестандартного пути — передать `-Dfirefox.binary=/path/to/firefox`.
- Доступ в интернет при первом запуске (для загрузки совместимых драйверов).

### Драйверы

Драйверы загружаются автоматически — в Selenium 4.43 встроен **Selenium Manager**: при первом запуске он определит установленные версии Chrome/Firefox и скачает совместимые `chromedriver`/`geckodriver` в `~/.cache/selenium/`. Никаких ручных действий не требуется, бинарники драйверов в проекте не нужны.

## Запуск тестов

### Параллельно в обоих браузерах (по умолчанию)

```bash
mvn clean test
```

или явно:

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-parallel.xml
```

### Только в Chrome

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-chrome.xml
```

### Только в Firefox

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-firefox.xml
```

### Запуск одного класса

```bash
mvn test -Dtest=TrainTest -DsuiteXmlFile=src/test/resources/testng-chrome.xml
```

### Запуск одного метода

```bash
mvn test -Dtest=TrainTest#uc01_searchTrainsMoscowSpbWithDate -DsuiteXmlFile=src/test/resources/testng-chrome.xml
```

### Headless

По умолчанию тесты запускаются с видимым окном (саджесты ж/д страницы стабильнее в headed-режиме). Для headless:

```bash
mvn test -Dheadless=true
```

## Просмотр use-case диаграммы

`docs/diagram.puml` — PlantUML-исходник. Сгенерировать PNG:

```bash
# если установлен plantuml через brew
plantuml docs/diagram.puml

# или открыть в браузере онлайн
# https://www.plantuml.com/plantuml/uml/
# вставить содержимое docs/diagram.puml
```

ASCII-эквивалент — в `docs/diagram.md`.

## Документация

- **`docs/use-cases.md`** — полное описание прецедентов использования (актор, предусловия, основной поток, постусловие).
- **`docs/scenarios.md`** — пошаговые тестовые сценарии с XPath-локаторами и ассертами.
- **`docs/checklist.md`** — чек-лист покрытия (по разделам сайта, типам взаимодействий, требованиям лабораторной).
- **`docs/diagram.puml`** / **`docs/diagram.md`** — use-case диаграмма.

## Технические заметки

- Все локаторы — XPath. Используются стабильные `data-ti`-атрибуты для React-компонентов tutu.ru и label-based XPath для форм без `for`-привязки.
- Все ожидания — `WebDriverWait` с `ExpectedConditions`. Никакого `Thread.sleep`.
- На страницах `tutu.ru` есть chat-widget iframe и sticky overlay — они скрываются JS-вставкой в `Page.open()` через `hideOverlayWidgets()`, чтобы не блокировать клики.
- Каталожные тесты (Авиа / Ж/д / Автобусы / Отели) стартуют на `tutu.ru`; `HomePage.click*Tab()` кликает по `[data-ti='tab-unified-*']`, форма переключается на нужный каталог и работает с тем же набором `data-ti`-локаторов (`input-root` / `input` / `trip-dates` / `submit-button`).
- Календарь (`[data-ti='calendar']`) использует `data-date` с epoch миллисекундами (Europe/Moscow) — точное попадание в нужный день.

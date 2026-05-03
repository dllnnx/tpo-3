# tpo-3 — Функциональное UI-тестирование tutu.ru

Лабораторная работа по тестированию: 12 E2E-сценариев на **Selenium WebDriver** с использованием паттерна **Page Object**, параллельный запуск в Chrome и Firefox.

## Что внутри

- **12 use cases** — реальные пользовательские путешествия по сайту tutu.ru: поиск ж/д билетов с фильтром «Сапсан», поиск авиабилетов с календарём и фильтром «С багажом», поиск автобусов / отелей / электричек, открытие модалки авторизации, поиск в справочной + один негативный сценарий.
- **Page Object** — 14 страничных классов в `src/test/java/pages/`, локаторы только через **XPath** (включая `data-ti`-атрибуты, label-based и стабильные `name`-атрибуты).
- **Только явные ожидания** `WebDriverWait` — никаких `Thread.sleep`.
- **TestNG** — три профиля запуска: parallel / chrome / firefox.

## Структура

```
src/test/java/
├── base/BaseTest.java              # TestNG @Parameters("browser"), single driver, WebDriverWait
├── pages/                          # 14 Page Object'ов
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
│   ├── AeroexpressPage.java
│   ├── FaqPage.java
│   └── LoginModalPage.java
├── tests/                          # 8 классов с 12 тестовыми методами (UC-01..UC-12)
│   ├── HomePageTest.java           # UC-10
│   ├── TrainTest.java              # UC-01, UC-02, UC-03
│   ├── AviaTest.java               # UC-04, UC-05, UC-12
│   ├── BusTest.java                # UC-06
│   ├── HotelTest.java              # UC-07
│   ├── ElectrichkaTest.java        # UC-08
│   ├── AeroexpressTest.java        # UC-09
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

- Все локаторы — XPath. Используются стабильные `data-ti`-атрибуты для React-компонентов tutu.ru, label-based XPath (`label/following::input`) для форм без `for`-привязки, и `name`-атрибуты для legacy форм (например, ж/д страница на jQuery).
- Все ожидания — `WebDriverWait` с `ExpectedConditions`. Никакого `Thread.sleep`.
- На страницах `tutu.ru` есть chat-widget iframe и sticky overlay — они скрываются JS-вставкой в `Page.open()` через `hideOverlayWidgets()`, чтобы не блокировать клики.
- Для `avia.tutu.ru` календарь использует `data-date` с epoch миллисекундами (Europe/Moscow timezone) — точное попадание в нужный день.
- Для `/poezda/` календарь — стандартный jQuery-UI datepicker с навигацией «Пред»/«След».

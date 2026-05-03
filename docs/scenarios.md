# Тестовые сценарии — пошаговые шаги, локаторы, ожидаемые результаты

Документ описывает реализованные тестовые сценарии в формате Given/When/Then с указанием XPath-локаторов и ассертов.

---

## UC-01: Поиск ж/д билетов Москва → СПб с выбором даты

**Тестовый метод:** `tests.TrainTest.uc01_searchTrainsMoscowSpbWithDate`
**Используемые Page Objects:** `HomePage`, `TrainPage`, `TrainResultsPage`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть главную | URL `https://www.tutu.ru/` | Header виден (`//*[@data-ti='header']`) |
| 2 | Перейти на /poezda/ через хедер | `//button[normalize-space()='Ж/д билеты']` | URL содержит `/poezda` |
| 3 | Кликнуть «Откуда» | `//input[@name='schedule_station_from']` | input получил фокус |
| 4 | Ввести «Москв», выбрать «Москва» в саджесте | `//div[contains(@class,'j-station_from')]//ul[contains(@class,'_level_1')]//li[normalize-space()='Москва']/div` | hidden `nnst1` непуст |
| 5 | Кликнуть «Куда», ввести «Санкт-Пете», выбрать «Санкт-Петербург» | `//div[contains(@class,'j-station_to')]...//li[normalize-space()='Санкт-Петербург']/div` | hidden `nnst2` непуст |
| 6 | Кликнуть в поле даты | `//input[contains(@class,'j-date_to')]` | Datepicker `#ui-datepicker-div` виден |
| 7 | Выбрать дату +30 дней (с навигацией «След» если нужно) | `//div[contains(@class,'ui-datepicker-group-first')]//td[not(contains(@class,'ui-state-disabled'))]/a[normalize-space()='{day}']` | Datepicker невидим |
| 8 | Нажать «Узнать расписание и цены» | `//button[contains(@class,'j-submit_button')]` | URL содержит `/poezda/Moskva/Sankt-Peterburg/?date=...` |

**Ассерты (Then):**
- URL содержит `/poezda/`, транслит «Москва» и «Санкт».
- `countTrainCards()` (через `//*[@data-ti='offer-card']`) ≥ 4.
- `hasWagonClassMention()` — найдено упоминание «Купе»/«Плацкарт»/«Сидячий»/«СВ».

---

## UC-02: Фильтр «Сапсан» на странице результатов ж/д

**Тестовый метод:** `tests.TrainTest.uc02_applySapsanFilter`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Выполнить полный поиск (UC-01 базовый) с датой +20 дней | — | Результаты загружены |
| 2 | Запомнить количество карточек до фильтра | `//*[@data-ti='offer-card']` | — |
| 3 | Кликнуть чип «Сапсан» | `//*[@data-ti='filter-sapsan']` | Класс `selected_*` появился |

**Ассерты:**
- `isSapsanFilterActive()` — true (CSS-класс `selected_*` присутствует).
- `allVisibleTrainsAreSapsan()` — все видимые карточки `[data-ti='train-name-badge']` содержат «Сапсан».
- `countTrainCards()` после фильтра ≤ количеству до.

---

## UC-03: Переключение даты в date-strip результатов ж/д

**Тестовый метод:** `tests.TrainTest.uc03_changeDateInDateStrip`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Полный поиск с датой +15 дней | — | Результаты загружены |
| 2 | Запомнить URL до клика | — | — |
| 3 | Кликнуть первую соседнюю дату в date-strip | `(//*[@data-ti='otherDates']//*[@data-ti='panel-chip'])[1]` | URL изменился |
| 4 | Дождаться появления новых карточек | `//*[@data-ti='offer-card']` | — |

**Ассерты:**
- `urlAfter != urlBefore`.
- URL содержит `/poezda/`.
- На новой дате есть ≥ 1 карточка.

---

## UC-04: Поиск авиабилетов Москва → СПб

**Тестовый метод:** `tests.AviaTest.uc04_searchFlightsMoscowSpb`
**Используемые Page Objects:** `AviaPage`, `AviaResultsPage`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть `avia.tutu.ru` | URL | Cookies-баннер закрыт |
| 2 | Кликнуть в input «Откуда» | `//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Откуда']]//input[@data-ti='input']` | Появился `dropdown-suggest-container` |
| 3 | Ввести «Москва» | (тот же input) | Появились `[data-ti='dropdown-item']` |
| 4 | Кликнуть первый item | `(//div[@data-ti='dropdown-suggest-container']//div[@data-ti='dropdown-item'])[1]` | Контейнер скрыт, input содержит «Москва» |
| 5 | Аналогично заполнить «Куда» = «Санкт-Петербург» | label «Куда» | input содержит «Санкт-Петербург» |
| 6 | Кликнуть в поле даты | `//input[@data-ti='trip-dates']` | Появился `[data-ti='calendar']` |
| 7 | (При необходимости) перейти на нужный месяц «След» | `//button[@data-ti='calendar-month-header-next-button']` | Заголовок месяца обновился |
| 8 | Кликнуть день +14 дней через `data-date` epoch ms | `//*[@data-ti='calendar-day-cell' and @data-date='{millis}']` | Ячейка получает `data-selected='true'` |
| 9 | Нажать «Выбрать» в футере календаря | `//button[@data-ti='calendar-popper-footer-select-button']` | Календарь скрыт |
| 10 | Нажать «Найти авиабилеты» | `//button[@data-ti='submit-button']` | URL начинается с `https://avia.tutu.ru/f/` |

**Ассерты:**
- `getFromValue()` = «Москва».
- `getToValue()` начинается с «Санкт».
- Если навигация удалась — на странице видны Москва или Санкт-Петербург.

---

## UC-05: Фильтр «С багажом» в результатах авиа

**Тестовый метод:** `tests.AviaTest.uc05_applyBaggageFilter`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Прямая навигация на `https://avia.tutu.ru/f/Moskva/Sankt-Peterburg/?class=Y&passengers=100` | URL | URL содержит `/f/` |
| 2 | Запомнить состояние чекбокса «С багажом» | `//input[@type='checkbox']` внутри лейбла `[data-ti='order-checkbox-outer']` с дочерним `[data-ti='form_baggage_filter']` | — |
| 3 | Кликнуть чекбокс | `(//label[@data-ti='order-checkbox-outer'][.//*[@data-ti='form_baggage_filter']])[1]` | Состояние `checked` поменялось |

**Ассерты:**
- Состояние чекбокса до ≠ после, либо в результатах появилось упоминание «Багаж»/«с багажом».

---

## UC-06: Поиск автобусов Москва → Тула

**Тестовый метод:** `tests.BusTest.uc06_searchBusesMoscowTula`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть `bus.tutu.ru` | URL | Форма видна |
| 2 | Кликнуть «Откуда» | `//input[@placeholder='Откуда']` | input получил фокус |
| 3 | Ввести «Москва», кликнуть suggest | `//div[contains(@class,'styles__dropdown_item') and contains(., 'Москва')]` | hidden `name='startCity'` непуст |
| 4 | Аналогично «Куда» = «Тула» | placeholder «Куда» | hidden `name='endCity'` непуст |
| 5 | Кликнуть «Дата», выбрать «Завтра» | `//input[@placeholder='Дата']`, `//button[normalize-space()='Завтра']` | Дата заполнена |
| 6 | Нажать «Найти билеты» | `//button[normalize-space()='Найти билеты']` | URL содержит `bus.tutu.ru/avtobus/...` |

**Ассерты:**
- URL остаётся в домене `bus.tutu.ru`.
- На странице есть упоминание Москва/Тула или маршрута, либо есть карточки автобусов.

---

## UC-07: Поиск отелей в Сочи

**Тестовый метод:** `tests.HotelTest.uc07_searchHotelsSochi`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть `hotel.tutu.ru` | URL | Форма видна |
| 2 | Кликнуть в поле направления, ввести «Сочи» | `//input[@data-ti='hotels-destination-input']` | input получил фокус |
| 3 | Выбрать первый суджест | `[data-ti='dropdown-item']` или Enter | — |
| 4 | Кликнуть в поле дат, выбрать заезд +30 дней и выезд +32 дня | `//input[@data-ti='date-input']`, `[data-ti='calendar-cell-day-...']` | Поле дат заполнено |
| 5 | Нажать «Найти» | `//button[normalize-space()='Найти']` | URL изменился |

**Ассерты:**
- URL остаётся на `hotel.tutu.ru`.
- На странице упоминается «Сочи» или URL содержит транслит.
- Есть карточки отелей или цены в формате «… ₽».

---

## UC-08: Расписание электричек Москва → Мытищи

**Тестовый метод:** `tests.ElectrichkaTest.uc08_electrichkaSchedule`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть `tutu.ru/prigorod/` | URL | Submit button присутствует |
| 2 | Проверить предзаполненную форму или дозаполнить | `(//label[normalize-space()='Откуда']/following::input[@type='text'])[1]`, `(//label[normalize-space()='Куда']/following::input[@type='text'])[1]` | Поля непустые |
| 3 | Нажать «Показать расписание» | `//button[@data-ti='submit-button']` | URL содержит `/prigorod/` |

**Ассерты:**
- URL содержит `/prigorod/`.
- На странице `≥ 1` строка расписания с временем формата `HH:MM`, либо упоминание «Расписание»/«электричка»/«Москва»/«отправ».

---

## UC-10: Открытие модалки авторизации и ввод email

**Тестовый метод:** `tests.HomePageTest.uc10_openLoginModalAndSubmitEmail`
**Используемые Page Objects:** `HomePage`, `LoginModalPage`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть главную | URL | Хедер виден |
| 2 | Скрыть оверлеи (chat-widget iframe, smart-баннер) | JS | Оверлеи скрыты |
| 3 | Найти видимую кнопку «Войти» | `//button[@data-ti='login-button']` (визуальная фильтрация) | Кнопка кликабельна |
| 4 | Кликнуть «Войти» | (та же) | Модалка открылась |
| 5 | Ввести email в поле | `//input[@name='emailOrPhone' or @placeholder='Ваш телефон или почта' or @name='userEmail']` | input содержит значение |
| 6 | Нажать «Получить код» / «Продолжить» / «Войти» | `//button[normalize-space()='Получить код' or normalize-space()='Продолжить' or normalize-space()='Далее' or normalize-space()='Войти']` | Появился шаг ввода кода |

**Ассерты:**
- Шапка с навигацией видна (`isHeaderNavigationVisible()`).
- Поле ввода email видно после клика «Войти».
- Email сохранился в поле.
- После клика «Получить код» появилось упоминание «код»/«Введите».

---

## UC-11: Поиск в Справочной

**Тестовый метод:** `tests.FaqTest.uc11_searchInFaq`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть `tutu.ru/2read/` | URL | Поле поиска видно |
| 2 | Ввести «возврат билета» | `(//input[@type='text' or @type='search'])[1]` | input заполнен |
| 3 | Нажать Enter | (тот же) | URL изменился, либо появились заголовки результатов |

**Ассерты:**
- URL изменился относительно начального, либо найден ≥ 1 результат с «озврат» или «илет».
- На странице видны заголовки h1/h2.

---

## UC-12: Negative — одинаковые города в авиа форме

**Тестовый метод:** `tests.AviaTest.uc12_sameDepartureArrivalNegative`

**Шаги:**

| # | Действие | Локатор | Ожидание |
|---|---|---|---|
| 1 | Открыть `avia.tutu.ru` | URL | Форма видна |
| 2 | Заполнить «Откуда» = «Москва» | (как UC-04) | — |
| 3 | Заполнить «Куда» = «Москва» | (как UC-04) | — |
| 4 | Нажать «Найти авиабилеты» | `//button[@data-ti='submit-button']` | — |

**Ассерты (любой из):**
- URL не содержит `/f/` (нет навигации на страницу результатов).
- Появилась валидационная подсказка («совпада»/«одинак»/«Откуда и куда»/«разные»/«Выберите»).

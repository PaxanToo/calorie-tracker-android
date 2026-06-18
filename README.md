# EatSnap

Приложение для Android на Kotlin, которое помогает следить за питанием, рассчитывает КБЖУ и содержит ИИ-помощника для анализа еды и подбора блюд посредством фотографий.

Данный проект является дипломным. Разработчик: Игнатов П. В.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Ollama](https://img.shields.io/badge/Ollama-000000?style=for-the-badge&logo=ollama&logoColor=white)
![Room](https://img.shields.io/badge/Room-0B9973?style=for-the-badge&logo=android&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)

## Скриншоты

<div align="center">

<img src="screenshots/home.png" width="200" alt="Главный экран">
<img src="screenshots/profile_3.png" width="200" alt="Анкета создания профиля">
<img src="screenshots/profile_2.png" width="200" alt="Профиль календарь">
<img src="screenshots/profile_1.png" width="200" alt="Профиль рекомендации">
<img src="screenshots/achievements.png" width="200" alt="Достижения">
<img src="screenshots/chatAi.png" width="200" alt="ИИ-чат">
<img src="screenshots/food_1.png" width="200" alt="Продукты">
<img src="screenshots/food_2.png" width="200" alt="Продукты по категориям">
  
</div>


## Функционал

- Расчёт дневной нормы калорий и БЖУ по данным профиля пользователя
- Учёт съеденных продуктов с ручным добавлением из базы продуктов
- ИИ-помощник с тремя режимами работы:
    - **Обычный чат** — вопросы и ответы по питанию
    - **Оценка калорий** — определение КБЖУ блюда по фотографии
    - **Подбор блюда** — предложение рецепта по имеющимся продуктам
- Добавление рассчитанных КБЖУ блюд в дневник питания
- Система получения достижений пользователем
- Сохранение профиля, прогресса и истории (Room, DataStore)
- Локальный ИИ на базе Ollama без облачных сервисов

## Стек технологий

| Компонент | Технология                |
|-----------|---------------------------|
| Язык | Kotlin                    |
| UI | Jetpack Compose           |
| Локальная БД | Room, DataStore           |
| Сеть | OkHttp                    |
| Асинхронность | Coroutines, Flow          |
| Архитектура | MVVM + Clean Architecture |
| Сервер | Node.js, Express          |
| Локальная LLM | Ollama, Qwen3.5:4b        |
| Туннелирование | fxtun                     |


## Системные требования

### Android-приложение
- Android 8.0+ (API 26+)
- Минимум 2 ГБ оперативной памяти

### Сервер ИИ
- ОС: Windows 10+
- Видеокарта: NVIDIA (CUDA) или AMD (Vulkan) с минимум 6+ ГБ видеопамяти
- Оперативная память: 8+ ГБ
- Node.js 18+




# 

# \# Filmorate

# 

# Filmorate — REST API-приложение для работы с фильмами и пользователями.

# 

# Приложение позволяет:

# \- Добавлять, обновлять и получать фильмы.

# \- Добавлять, обновлять и получать пользователей.

# \- Ставить и удалять лайки фильмам.

# \- Получать список популярных фильмов.

# \- Добавлять и удалять друзей.

# \- Получать список друзей пользователя.

# \- Находить общих друзей.

# \- Хранить жанры фильмов и возрастные рейтинги MPA.

# 

# \## Технологии

# 

# \- Java 21

# \- Spring Boot 3.5.12

# \- Maven

# \- Lombok

# \- JUnit 5

# \- Mockito

# \- H2 Database (для дальнейшей реализации хранения данных)

# 

# \## Структура базы данных

# 

# Для хранения данных разработана реляционная схема базы данных.

# 

# !\[Схема базы данных Filmorate](docs/filmorate\_database.png)

# 

# \### Описание таблиц

# 

# | Таблица | Назначение |

# |---|---|

# | users | Информация о пользователях |

# | films | Информация о фильмах |

# | genres | Справочник жанров |

# | film\_genres | Связь фильмов с жанрами |

# | mpa\_ratings | Возрастные рейтинги MPA |

# | film\_likes | Лайки пользователей |

# | friendships | Связи между пользователями |

# | friendship\_statuses | Статусы дружбы |

# 

# \### Связи между таблицами

# 

# \- Один фильм может иметь несколько жанров.

# \- Один жанр может относиться к нескольким фильмам.

# \- Каждый фильм имеет один возрастной рейтинг MPA.

# \- Пользователи могут ставить лайки фильмам.

# \- Пользователи могут отправлять запросы на добавление в друзья.

# \- Каждая связь дружбы имеет свой статус.

# 

# \### Статусы дружбы

# 

# | Статус | Описание |

# |---|---|

# | UNCONFIRMED | Запрос на добавление в друзья не подтверждён |

# | CONFIRMED | Запрос на добавление в друзья подтверждён |

# 

# \### Возрастные рейтинги MPA

# 

# | Рейтинг | Описание |

# |---|---|

# | G | Нет возрастных ограничений |

# | PG | Рекомендуется просмотр с родителями |

# | PG-13 | Детям до 13 лет просмотр не желателен |

# | R | Просмотр до 17 лет только со взрослым |

# | NC-17 | Просмотр лицам до 18 лет запрещён |

# 

# \## Примеры SQL-запросов

# 

# \### 1. Получение всех фильмов

# 

# ```sql

# SELECT \*

# FROM films;

# ```

# 

# \### 2. Получение всех пользователей

# 

# ```sql

# SELECT \*

# FROM users;

# ```

# 

# \### 3. Получение фильма вместе с его жанрами и рейтингом MPA

# 

# ```sql

# SELECT

# &#x20;   f.film\_id,

# &#x20;   f.name,

# &#x20;   f.description,

# &#x20;   f.release\_date,

# &#x20;   f.duration,

# &#x20;   m.name AS mpa\_rating,

# &#x20;   g.name AS genre

# FROM films f

# LEFT JOIN mpa\_ratings m

# &#x20;   ON f.mpa\_id = m.mpa\_id

# LEFT JOIN film\_genres fg

# &#x20;   ON f.film\_id = fg.film\_id

# LEFT JOIN genres g

# &#x20;   ON fg.genre\_id = g.genre\_id

# WHERE f.film\_id = 1;

# ```

# 

# \### 4. Получение N самых популярных фильмов

# 

# ```sql

# SELECT

# &#x20;   f.film\_id,

# &#x20;   f.name,

# &#x20;   COUNT(fl.user\_id) AS likes\_count

# FROM films f

# LEFT JOIN film\_likes fl

# &#x20;   ON f.film\_id = fl.film\_id

# GROUP BY f.film\_id, f.name

# ORDER BY likes\_count DESC, f.film\_id

# LIMIT 10;

# ```

# 

# \### 5. Получение списка друзей пользователя

# 

# ```sql

# SELECT DISTINCT u.\*

# FROM friendships f

# JOIN users u

# &#x20;   ON u.user\_id = CASE

# &#x20;       WHEN f.user\_id = 1 THEN f.friend\_id

# &#x20;       ELSE f.user\_id

# &#x20;   END

# WHERE (

# &#x20;   f.user\_id = 1

# &#x20;   OR f.friend\_id = 1

# )

# AND f.status\_id = (

# &#x20;   SELECT status\_id

# &#x20;   FROM friendship\_statuses

# &#x20;   WHERE name = 'CONFIRMED'

# );

# ```

# 

# \### 6. Получение общих друзей двух пользователей

# 

# ```sql

# WITH first\_user\_friends AS (

# &#x20;   SELECT DISTINCT

# &#x20;       CASE

# &#x20;           WHEN user\_id = 1 THEN friend\_id

# &#x20;           ELSE user\_id

# &#x20;       END AS friend\_id

# &#x20;   FROM friendships

# &#x20;   WHERE (user\_id = 1 OR friend\_id = 1)

# &#x20;     AND status\_id = (

# &#x20;         SELECT status\_id

# &#x20;         FROM friendship\_statuses

# &#x20;         WHERE name = 'CONFIRMED'

# &#x20;     )

# ),

# second\_user\_friends AS (

# &#x20;   SELECT DISTINCT

# &#x20;       CASE

# &#x20;           WHEN user\_id = 2 THEN friend\_id

# &#x20;           ELSE user\_id

# &#x20;       END AS friend\_id

# &#x20;   FROM friendships

# &#x20;   WHERE (user\_id = 2 OR friend\_id = 2)

# &#x20;     AND status\_id = (

# &#x20;         SELECT status\_id

# &#x20;         FROM friendship\_statuses

# &#x20;         WHERE name = 'CONFIRMED'

# &#x20;     )

# )

# SELECT u.\*

# FROM users u

# JOIN first\_user\_friends f1

# &#x20;   ON u.user\_id = f1.friend\_id

# JOIN second\_user\_friends f2

# &#x20;   ON u.user\_id = f2.friend\_id;

# ```

# 

# \### 7. Добавление лайка фильму

# 

# ```sql

# INSERT INTO film\_likes (film\_id, user\_id)

# VALUES (1, 2);

# ```

# 

# \### 8. Удаление лайка

# 

# ```sql

# DELETE FROM film\_likes

# WHERE film\_id = 1

# &#x20; AND user\_id = 2;

# ```

# 

# \### 9. Добавление пользователя в друзья

# 

# ```sql

# INSERT INTO friendships (user\_id, friend\_id, status\_id)

# VALUES (

# &#x20;   1,

# &#x20;   2,

# &#x20;   (SELECT status\_id

# &#x20;    FROM friendship\_statuses

# &#x20;    WHERE name = 'UNCONFIRMED')

# );

# ```

# 

# \### 10. Подтверждение дружбы

# 

# ```sql

# UPDATE friendships

# SET status\_id = (

# &#x20;   SELECT status\_id

# &#x20;   FROM friendship\_statuses

# &#x20;   WHERE name = 'CONFIRMED'

# )

# WHERE user\_id = 1

# &#x20; AND friend\_id = 2;

# ```

# 

# \## Запуск проекта

# 

# Для сборки проекта выполните:

# 

# ```bash

# mvn clean install

# ```

# 

# Для запуска приложения:

# 

# ```bash

# mvn spring-boot:run

# ```

# 

# Приложение запускается локально на порту 8080.

# 

# \## Тестирование

# 

# Для запуска тестов:

# 

# ```bash

# mvn clean test

# ```

# 

# \## Автор

# 

# Проект Filmorate в рамках курса Java-разработчик от Яндекс Практикума.




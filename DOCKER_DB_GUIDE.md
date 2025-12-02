# 🐳 Docker Compose для розробки

Цей файл дозволяє швидко підняти PostgreSQL базу даних для локальної розробки.

## 📋 Що включено

- **PostgreSQL 15** - основна база даних
- **pgAdmin 4** - веб-інтерфейс для управління БД
- **Auto-initialization** - автоматичне створення користувача та БД

---

## 🚀 Швидкий старт

### 1. Встановіть Docker Desktop

**Windows:**
- Завантажте Docker Desktop з https://www.docker.com/products/docker-desktop
- Встановіть та перезавантажте комп'ютер
- Запустіть Docker Desktop

**Linux:**
```bash
sudo apt-get update
sudo apt-get install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

### 2. Запустіть базу даних

```bash
# Перейдіть в папку проєкту
cd D:/work/military-warehouse-template

# Запустіть контейнери
docker-compose up -d

# Перевірте статус
docker-compose ps
```

### 3. Перевірте підключення

База даних буде доступна через **5-10 секунд** після запуску.

```bash
# Перевірте логи
docker-compose logs postgres

# Очікуваний вивід:
# database system is ready to accept connections
```

---

## 📊 Параметри підключення

### PostgreSQL:
```
Host:     localhost
Port:     5432
Database: warehouse_db
Username: warehouse_user
Password: warehouse_pass
JDBC URL: jdbc:postgresql://localhost:5432/warehouse_db
```

### pgAdmin (веб-інтерфейс):
```
URL:      http://localhost:5050
Email:    admin@warehouse.local
Password: admin
```

---

## 🔧 Корисні команди

### Керування контейнерами

```bash
# Запустити контейнери
docker-compose up -d

# Зупинити контейнери
docker-compose stop

# Перезапустити
docker-compose restart

# Зупинити та видалити контейнери
docker-compose down

# Зупинити та видалити ВСЕ (включно з даними!)
docker-compose down -v
```

### Моніторинг

```bash
# Переглянути логи
docker-compose logs -f postgres

# Переглянути статус
docker-compose ps

# Переглянути використання ресурсів
docker stats
```

### Підключення до БД через командний рядок

```bash
# Підключитися до PostgreSQL через psql
docker-compose exec postgres psql -U warehouse_user -d warehouse_db

# Основні команди psql:
\dt              # Список таблиць
\d table_name    # Структура таблиці
\l               # Список баз даних
\q               # Вийти
```

### Резервне копіювання та відновлення

```bash
# Створити backup
docker-compose exec postgres pg_dump -U warehouse_user warehouse_db > backup.sql

# Відновити з backup
docker-compose exec -T postgres psql -U warehouse_user warehouse_db < backup.sql
```

---

## 🔌 Підключення з IntelliJ IDEA

### 1. Відкрийте Database панель
- View → Tool Windows → Database (або Alt+1)

### 2. Додайте нове підключення
- Натисніть "+" → Data Source → PostgreSQL

### 3. Введіть параметри:
```
Host: localhost
Port: 5432
Database: warehouse_db
User: warehouse_user
Password: warehouse_pass
```

### 4. Тестуйте підключення
- Натисніть "Test Connection"
- Якщо потрібно, завантажте драйвери PostgreSQL

---

## 🌐 Використання pgAdmin

### 1. Відкрийте браузер
```
http://localhost:5050
```

### 2. Увійдіть
```
Email: admin@warehouse.local
Password: admin
```

### 3. Додайте сервер
- Клік правою кнопкою на "Servers" → Create → Server

**General:**
- Name: `Local Warehouse DB`

**Connection:**
```
Host: postgres
Port: 5432
Maintenance database: warehouse_db
Username: warehouse_user
Password: warehouse_pass
```

### 4. Збережіть пароль
- Відмітьте "Save password"

---

## 🧪 Запуск додатку з Docker БД

### 1. Переконайтеся що Docker БД запущена
```bash
docker-compose ps
```

### 2. Запустіть Spring Boot додаток

**Через IntelliJ IDEA:**
- Клік правою на `WarehouseApplication.java`
- Run 'WarehouseApplication'

**Через Maven:**
```bash
mvn spring-boot:run
```

**Через командний рядок:**
```bash
mvn clean package
java -jar target/military-warehouse-*.jar
```

### 3. Перевірте підключення
```
http://localhost:8080/actuator/health
```

Очікувана відповідь:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

---

## 🐛 Troubleshooting

### Проблема: "Port 5432 already in use"

**Рішення 1:** Зупиніть існуючий PostgreSQL
```bash
# Windows
net stop postgresql-x64-15

# Linux
sudo systemctl stop postgresql
```

**Рішення 2:** Змініть порт у docker-compose.yml
```yaml
ports:
  - "5433:5432"  # Використайте 5433 замість 5432
```

І в application.properties:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/warehouse_db
```

### Проблема: "Connection refused"

**Перевірте чи запущений Docker:**
```bash
docker ps
```

**Перевірте статус контейнера:**
```bash
docker-compose ps
docker-compose logs postgres
```

**Перезапустіть контейнери:**
```bash
docker-compose restart
```

### Проблема: "Authentication failed"

**Перевірте credentials у application.properties:**
```properties
spring.datasource.username=warehouse_user
spring.datasource.password=warehouse_pass
```

**Пересоздайте контейнери:**
```bash
docker-compose down -v
docker-compose up -d
```

### Проблема: "Cannot start service postgres"

**Windows:** Переконайтеся що Docker Desktop запущений

**Linux:** Перевірте Docker daemon
```bash
sudo systemctl status docker
sudo systemctl start docker
```

---

## 📁 Структура даних

### Volumes
Docker зберігає дані у volumes, які зберігаються навіть після видалення контейнерів:

```
postgres_data   - дані PostgreSQL
pgadmin_data    - налаштування pgAdmin
```

### Переглянути volumes:
```bash
docker volume ls
```

### Видалити volumes (⚠️ ВИДАЛИТЬ ВСІ ДАНІ):
```bash
docker-compose down -v
```

---

## 🔄 Налаштування для різних середовищ

### Розробка (development)
```bash
docker-compose up -d
```
Використовує `application.properties`

### Тестування (test)
Використовує `application-test.properties` (Testcontainers)

### Продакшн (production)
⚠️ НЕ використовуйте docker-compose.yml для продакшну!
Використовуйте окрему БД з:
- Автоматичними backup
- Реплікацією
- Моніторингом
- SSL підключенням

---

## 📚 Додаткові ресурси

- [Docker Documentation](https://docs.docker.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [pgAdmin Documentation](https://www.pgadmin.org/docs/)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)

---

## 💡 Поради

1. **Завжди зупиняйте Docker після роботи:**
   ```bash
   docker-compose stop
   ```

2. **Періодично робіть backup:**
   ```bash
   docker-compose exec postgres pg_dump -U warehouse_user warehouse_db > backup_$(date +%Y%m%d).sql
   ```

3. **Переглядайте логи при проблемах:**
   ```bash
   docker-compose logs -f
   ```

4. **Для чистого старту видаліть volumes:**
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

---

**Готово! База даних працює! 🎉**

Тепер можна запускати Spring Boot додаток та розробляти API.

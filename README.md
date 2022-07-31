# easy-backend
Сервис работает в связке с микросервисом https://github.com/viktorxdev/easy-payment

Для локального запуска необходимо поднять два докер-контейнера: 
1) для запуска кафки:
- скопируйте и сохраните приведенный ниже текст в docker-compose.yml файл 

```
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.0.1
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  broker:
    image: confluentinc/cp-kafka:7.0.1
    container_name: broker
    ports:
    # To learn about configuring Kafka for access across networks see
    # https://www.confluent.io/blog/kafka-client-cannot-connect-to-broker-on-aws-on-docker-etc/
      - "9092:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://broker:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
 ```      
- из директории с сохранненым файлом выполните команду ```docker-compose up -d```
2) для запуска бд постгре выполните команды:
- ```docker pull postgres```
- ```docker run --name easy_payment -p 5432:5432 -e POSTGRES_USER=easy -e POSTGRES_PASSWORD=easy -d postgres```


ВНИМАНИЕ! в application.yaml в свойстве app.path.transactions необходимо указать путь к файлу csv в который будет происходить запись данных,
либо запустите проект командой с указанием пути ```./gradlew bootRun --args='--app.path.transactions=ПУТЬ ДО ФАЙЛА'```

Для удобства локального запуска все пароли прописаны в application.yaml, не повторяйте такое на проде!

---
###ТЕСТОВОЕ ЗАДАНИЕ
####Описание
Существует некоторый бизнес-процесс – пополнение счета консультанта с помощью транзакций с
банковской карты в рублях.
Требуется реализовать сервисы
Микросервис Payment
Должен иметь endpoint на получение {host}/pay
POST запроса с телом json в формате
```
{
accountId: 1, # ID аккаунта консультанта
amount: 100.2 # Сумма пополнения в рублях
}
```

Endpoint сервиса должен получать, обрабатывать этот json и отправлять в некоторую очередь
Queue таких транзакций.
Микросервис Backend
- Должен читать очередь Queue
- Сохранять транзакции из очереди Queue в базу данных Postgres «payments»
- Каждые 5 минут записывать в файл transaction.csv в локальной папке транзакции в
  формате
```
id;account_id;amount;datetime_transaction<br>
1;1;100.2;2022-07-28 07:00:00
```
####Задание
1. Спроектировать БД Postgres «payments»
2. Создать сервис “Payment” реализовать эндпоинт для приёма запросов
3. Реализовать очередь обмена сообщениями между сервисами
4. Наполнить БД Postgres «payments» транзакциями из запросов
5. Наполнить файл transaction.csv транзакциями из запросов
   Технологии
- Java 11+ /Kotlin 1.3+
- Spring Boot 2+
- Postgres 9.6+
- Любой брокер очередей
  

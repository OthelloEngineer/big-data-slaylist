import json
from kafka import KafkaProducer
from faker import Faker

fake = Faker()
KAFKA_BOOTSTRAP: list[str] = ["kafka:9092"]
DEFAULT_ENCODING: str = "utf-8"
producer = KafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP)
topic = "INGESTION"

def generate_random_data():
    return {
        "name": fake.name(),
        "address": fake.address(),
        "email": fake.email(),
        "phone_number": fake.phone_number(),
        "company": fake.company(),
        "job": fake.job(),
        "city": fake.city(),
        "timestamp": fake.iso8601()
    }


def produce_data_to_kafka():
    while(True):
        data = generate_random_data()
        print(data)
        producer.send(
        topic=topic,
        value=json.dumps(data).encode(DEFAULT_ENCODING),
    )
        producer.flush()

if __name__ == '__main__':
    produce_data_to_kafka()

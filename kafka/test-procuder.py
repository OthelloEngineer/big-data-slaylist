import json
from kafka import KafkaProducer
from faker import Faker
import random

fake = Faker()
KAFKA_BOOTSTRAP: list[str] = ["kafka:9092"]
DEFAULT_ENCODING: str = "utf-8"
producer = KafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP)
topic = "test"
JSON_FILE_PATH = "playlists.json"

def generate_random_data():
    return {
        "name": fake.name(),
        "Followers": random.randint(0,100)
    }

def load_json_data(file_path):
    with open(file_path, 'r') as f:
        return json.load(f)


def produce_data_to_kafka():
    # for x in range(200):
    #     data = generate_random_data()
    #     print(data)
    #     producer.send(
    #     topic=topic,
    #     value=json.dumps(data).encode(DEFAULT_ENCODING),
    # )
    #     producer.flush()
    data_list = load_json_data(JSON_FILE_PATH)

    for data in data_list:
        print (data)
        producer.send (
            topic=topic,
            value=json.dumps(data).encode(DEFAULT_ENCODING)
        )
        producer.flush
        
if __name__ == '__main__':
    produce_data_to_kafka()

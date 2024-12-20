from flask import Flask, request
from kafka import KafkaConsumer, KafkaProducer
import requests
import threading
import time
import queue
import json

# Flask application
app = Flask(__name__)

# Kafka configuration
KAFKA_BROKER = 'kafka:9092'
INPUT_TOPIC = 'ARTISTID'
OUTPUT_TOPIC = 'ARTIST'

consumer = KafkaConsumer(
    INPUT_TOPIC,
    bootstrap_servers=KAFKA_BROKER,
    auto_offset_reset='earliest'
)
producer = KafkaProducer(
    bootstrap_servers=KAFKA_BROKER,
    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
    key_serializer=lambda k: k.encode('utf-8')
)

# Token management
tokens = queue.Queue()
tokens_list = []  # To hold token values
token_lock = threading.Lock()

# API rate limiting
RATE_LIMIT = 1  # Requests per second per token

# Function to fetch artist data from Spotify API
def fetch_artist_data(artist_id):
    while True:
        token = tokens.get()
        try:
            headers = {
                'Authorization': f'Bearer {token}'
            }
            artist_id_request = artist_id.strip('"')
            url = f'https://api.spotify.com/v1/artists/{artist_id_request}'
            response = requests.get(url, headers=headers)
            print(f"Requesting data for artist_id: {artist_id}")
            print(f"Response status: {response.status_code}, body: {response.text}")


            if response.status_code == 200:
                data = response.json()
                output_key = data['uri']
                output_value = {
                    'genres': data['genres'],
                    'name': data['name'],
                    'popularity': data['popularity'],
                    'uri': data['uri']
                }
                producer.send(OUTPUT_TOPIC, key=output_key, value=output_value)
                tokens.put(token)  # Return the token back to the queue
                break
            elif response.status_code == 429:
                retry_after = int(response.headers.get('Retry-After', 1))
                time.sleep(retry_after)
            else:
                print(f"Error: {response.status_code} for artist {artist_id}")
                tokens.put(token)  # Return the token back to the queue
                break
        except Exception as e:
            print(f"Exception: {e}")
            tokens.put(token)  # Return the token back to the queue
        finally:
            time.sleep(1 / RATE_LIMIT)  # Enforce rate limiting

# Kafka consumer thread
def consume_kafka():
    for message in consumer:
        artist_id = message.value.decode('utf-8')
        threading.Thread(target=fetch_artist_data, args=(artist_id,)).start()

# Token refresh endpoint
@app.route('/add_token/<token>', methods=['POST'])
def add_token(token):
    if token:
        with token_lock:
            tokens_list.append(token)
            tokens.put(token)
        return {"message": "Token added successfully"}, 200
    else:
        return {"error": "Token is required"}, 400

# Start Flask and Kafka consumer
if __name__ == '__main__':
    threading.Thread(target=consume_kafka, daemon=True).start()
    app.run(host='0.0.0.0', port=5000)

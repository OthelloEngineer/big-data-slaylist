from flask import Flask, request
from kafka import KafkaConsumer, KafkaProducer
import requests
import threading
import time
import json
from threading import Timer

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
tokens = []
current_token_index = 0

# Processed artist IDs
processed_artist_ids = set()
processed_lock = threading.Lock()

# Batch management
batch_lock = threading.Lock()
artist_batch = []
batch_send_timeout = 2
batch_last_updated = time.time()

# API rate limiting
RATE_LIMIT = 1  # Requests per second

def switch_token():
    global current_token_index
    if tokens:
        current_token_index = (current_token_index + 1) % len(tokens)
        print(f"Token switched to: {tokens[current_token_index]}")
    Timer(50 * 60, switch_token).start()  # Schedule the next switch

# Fetch artist data
def fetch_artist_data(batch):
    global tokens, current_token_index

    if not tokens:
        print("No tokens available. Please set tokens using the /set_token endpoint.")
        return

    token = tokens[current_token_index]
    headers = {
        'Authorization': f'Bearer {token}'
    }

    artist_ids = ",".join(batch).replace("\"", "")
    url = f'https://api.spotify.com/v1/artists?ids={artist_ids}'

    try:
        response = requests.get(url, headers=headers)
        print(f"Requesting data for batch: {artist_ids}")
        print(f"Response status: {response.status_code}, body: {response.text}")

        if response.status_code == 200:
            artists_data = response.json().get('artists', [])
            for artist_data in artists_data:
                output_key = artist_data['uri']
                output_value = {
                    'genres': artist_data['genres'],
                    'name': artist_data['name'],
                    'popularity': artist_data['popularity'],
                    'uri': artist_data['uri']
                }
                producer.send(OUTPUT_TOPIC, key=output_key, value=output_value)
                producer.flush()

                with processed_lock:
                    processed_artist_ids.add(artist_data['id'])

        elif response.status_code == 429:
            retry_after = int(response.headers.get('Retry-After', 1))
            print(f"Rate limited. Retrying after {retry_after} seconds.")
            time.sleep(retry_after)

        else:
            print(f"Error: {response.status_code} for batch {artist_ids}")

    except Exception as e:
        print(f"Exception fetching artist data: {e}")

    time.sleep(1 / RATE_LIMIT)  # Apply rate limiting

def process_batches():
    global artist_batch, batch_last_updated

    while True:
        time.sleep(1)  # Check every second

        with batch_lock:
            if artist_batch and (time.time() - batch_last_updated >= batch_send_timeout):
                print(f"Timeout reached. Sending batch of size {len(artist_batch)}")
                batch_to_process = list(artist_batch)
                artist_batch.clear()
                batch_last_updated = time.time()
                fetch_artist_data(batch_to_process)

# Kafka consumer thread
def consume_kafka():
    global batch_last_updated, artist_batch

    try:
        for message in consumer:
            artist_id = message.value.decode('utf-8')

            with batch_lock:
                artist_batch.append(artist_id)
                batch_last_updated = time.time()

                if len(artist_batch) >= 50:
                    print("Batch size reached. Sending batch.")
                    batch_to_process = list(artist_batch)
                    artist_batch.clear()
                    batch_last_updated = time.time()
                    fetch_artist_data(batch_to_process)
    except Exception as e:
        print(f"Kafka consumer exception: {e}. Restarting consumer loop.")

# Token management endpoints
@app.route('/set_token/<new_token>', methods=['POST'])
def set_token(new_token):
    global tokens

    if new_token:
        tokens.append(new_token)
        print(f"Token {new_token} added successfully.")
        return {"message": f"Token {new_token} added successfully"}, 200
    else:
        return {"error": "Token is required"}, 400

@app.route('/current_token', methods=['GET'])
def get_current_token():
    if tokens:
        return {"current_token": tokens[current_token_index]}, 200
    else:
        return {"error": "No tokens available"}, 400

# Start the token switch timer
Timer(50 * 60, switch_token).start()

# Start Flask and background threads
if __name__ == '__main__':
    threading.Thread(target=consume_kafka, daemon=True).start()
    threading.Thread(target=process_batches, daemon=True).start()
    app.run(host='0.0.0.0', port=5000)

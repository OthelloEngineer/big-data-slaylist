from flask import Flask, request, jsonify
from kafka import KafkaProducer, KafkaConsumer
import threading
import json
import time

app = Flask(__name__)

# Kafka configuration
KAFKA_BROKER = 'kafka:9092'
ARTISTID_TOPIC = 'ARTISTID'
ARTIST_TOPIC = 'ARTIST'
PLAYLISTS_TOPIC = 'PLAYLISTS'

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BROKER,
    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
    key_serializer=lambda k: k.encode('utf-8')  # Serialize the key as UTF-8
)

consumer = KafkaConsumer(
    ARTIST_TOPIC,
    bootstrap_servers=KAFKA_BROKER,
    value_deserializer=lambda m: json.loads(m.decode('utf-8'))
)

# In-memory data structures
playlists_memory = []
artist_uris_global = set()
artist_data_store = {}
lock = threading.Lock()

@app.route('/process_dataset', methods=['POST'])
def process_dataset():
    global playlists_memory, artist_uris_global

    # Step 1: Receive playlists
    data = request.json
    if not data or 'playlists' not in data:
        return jsonify({"error": "Invalid input, 'playlists' key is missing"}), 400

    playlists = data['playlists']

    artist_uris = set()
    processed_playlists = []        
    # Process playlists in batches to handle large payloads'
    for playlist in playlists:
        if playlist.get('num_followers', 0) < 10:
            print(f"Skipping playlist with less than 10 followers: {playlist.get('name', 'Unnamed')}".encode('ascii', errors='ignore').decode())
            continue

        print(f"Processing playlist: {playlist.get('name', 'Unnamed')}".encode('ascii', errors='ignore').decode())

        playlist['origin'] = "DATASET"  # Add origin field

        for track in playlist["tracks"]:
            artist_uri = track["artist_uri"]
            if artist_uri in artist_data_store:
                track["artist_data"] = artist_data_store[artist_uri]

            if artist_uri and artist_uri not in artist_uris_global:
                artist_uris.add(artist_uri)

        processed_playlists.append(playlist)

    # Safely append processed playlists to memory
    with lock:
        playlists_memory.extend(processed_playlists)

        
    # Update global artist URIs to avoid duplicates in future slices
    artist_uris_global.update(artist_uris)

    print(f"Total unique artist URIs: {len(artist_uris)}")

    # Publish unique artist URIs to Kafka
    for uri in artist_uris:
        value = uri.split(':')[-1]
        producer.send(ARTISTID_TOPIC, key=uri, value=value)  # Key and value both are artisturi
        producer.flush()  # Ensure immediate delivery
        print(f"Published artist URI to ARTISTID with key: {uri}, value: {value}")

    # Wait for playlists to be fully processed by the consumer
    while True:
        with lock:
            if not playlists_memory:
                break
        print(f"Waiting for playlists to process. Remaining in memory: {len(playlists_memory)}")
        time.sleep(1)

    return jsonify({"status": "OK"}), 200


@app.route('/user_playlist', methods=['POST'])
def user_playlist():
    playlist = request.json  # This is a dictionary
    playlist['origin'] = 'USER'
    
    # Send the playlist to Kafka
    producer.send(PLAYLISTS_TOPIC, key=str(playlist['pid']).encode('utf-8'), value=json.dumps(playlist).encode('utf-8'))
    producer.flush()
    
    return jsonify({"status": "ok"}), 200


def consume_artist_updates():
    global playlists_memory
    global artist_data_store

    for message in consumer:
        artist_data = message.value
        artist_uri = artist_data['uri']
        artist_data_store[artist_uri] = artist_data
        
        # Update playlists in memory with artist data
        with lock:
            for playlist in playlists_memory:
                genre_counts = {}
                for track in playlist['tracks']:
                    if track['artist_uri'] == artist_uri:
                        track['artist_data'] = artist_data
                    genres = track['artist_data']['genres']
                    for genre in genres:
                        if genre in genre_counts:
                            genre_counts[genre] += 1
                        else:
                            genre_counts[genre] = 1
                playlist['genre_counts'] = genre_counts

        # Check if all playlists are updated
        with lock:
            all_updated = all(
                all('artist_data' in track for track in playlist['tracks'])
                for playlist in playlists_memory
            )

            if all_updated:
                # Publish updated playlists to PLAYLISTS topic
                for playlist in playlists_memory:
                    producer.send(PLAYLISTS_TOPIC, key=str(playlist['pid']), value=json.dumps(playlist).encode('utf-8'))
                    producer.flush()
                    print(f"Published updated playlist: {playlist.get('name', 'Unnamed')}")
                playlists_memory = []


# Start the Kafka consumer thread
consumer_thread = threading.Thread(target=consume_artist_updates, daemon=True)
consumer_thread.start()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8888, debug=False)


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
    key_serializer=lambda k: k.encode('utf-8')
)

consumer = KafkaConsumer(
    ARTIST_TOPIC,
    bootstrap_servers=KAFKA_BROKER,
    auto_offset_reset='earliest',
    value_deserializer=lambda m: json.loads(m.decode('utf-8'))
)

# In-memory data structures
playlists_memory = []
artist_uris_global = set()
artist_data_store = {}
lock = threading.Lock()
slice_count = 0

@app.route('/process_dataset', methods=['POST'])
def process_dataset():
    global playlists_memory, artist_uris_global, slice_count

    slice_count += 1

    data = request.json
    if not data or 'playlists' not in data:
        return jsonify({"error": "Invalid input, 'playlists' key is missing"}), 400

    playlists = data['playlists']
    artist_uris = set()
    processed_playlists = []

    for playlist in playlists:
        if playlist.get('num_followers', 0) < 10:
            print(f"Skipping playlist with less than 10 followers: {playlist.get('name', 'Unnamed')}")
            continue

        print(f"Processing playlist: {playlist.get('name', 'Unnamed')}")
        playlist['origin'] = "DATASET"

        for track in playlist.get("tracks", []):
            artist_uri = track.get("artist_uri")
            if artist_uri in artist_data_store:
                track["artist_data"] = artist_data_store[artist_uri]

            if artist_uri and artist_uri not in artist_uris_global:
                artist_uris.add(artist_uri)

        processed_playlists.append(playlist)

    with lock:
        playlists_memory.extend(processed_playlists)

    artist_uris_global.update(artist_uris)

    print(f"Total unique artist URIs added: {len(artist_uris)}")

    for uri in artist_uris:
        try:
            value = uri.split(':')[-1]
            producer.send(ARTISTID_TOPIC, key=uri, value=value)
            producer.flush()
            print(f"Published artist URI to ARTISTID: {uri}")
        except Exception as e:
            print(f"Error sending artist URI {uri} to Kafka: {e}")

    # Wait for all artist data to be enriched with a timeout
    start_time = time.time()
    while True:
        elapsed_time = time.time() - start_time
        with lock:
            if len(artist_uris_global) == len(artist_data_store):
                print("All artist data has been enriched.")
                break

        #skip if no new artists
        if len(artist_uris) == 0:
            break
        
        time_break = 12  # Default timeout

        if slice_count >= 25:
            time_break = 10

        if slice_count >= 50:
            time_break = 8

        if slice_count >= 70:
            time_break = 6


        if elapsed_time > time_break:  # Timeout after less time as more data has been consumed
            print("Timeout exceeded. Should be in next batch.")
            
            break

        # I dont know why on God's green earth I cant just remove the uris that dont get data sent back, but some data 
        # is just missing and I hate everything 
        print(f"Waiting for artist data enrichment. Progress: {len(artist_data_store)}/{len(artist_uris_global)}")
        time.sleep(1)

    # Call the separate processing logic after enrichment
    process_playlists()
    while True:
        if playlists_memory == []:
            return jsonify({"status": "OK"}), 200
        time.sleep(1)

def process_playlists():
    global playlists_memory

    with lock:
        for playlist in playlists_memory:
            print(f"Processing playlist: {playlist.get('name', 'Unnamed')}")
            genre_counts = {}
            for track in playlist.get('tracks', []):
                artist_uri = track.get('artist_uri')
                if artist_uri in artist_data_store:
                    track['artist_data'] = artist_data_store[artist_uri]
                    genres = artist_data_store[artist_uri].get('genres', [])
                    for genre in genres:
                        genre_counts[genre] = genre_counts.get(genre, 0) + 1
            playlist['genre_counts'] = genre_counts
            print(f"Processed playlist: {playlist.get('name', 'Unnamed')}")

        print("Publishing all playlists to Kafka.")
        try:
            for playlist in playlists_memory:
                producer.send(PLAYLISTS_TOPIC, key=str(playlist['pid']), value=playlist)
            producer.flush()
            print("All playlists have been published.")
        except Exception as e:
            print(f"Error publishing playlists: {e}")

        # Clear the playlists_memory list after processing
        playlists_memory = []

@app.route('/user_playlist', methods=['POST'])
def user_playlist():
    playlist = request.json
    playlist['origin'] = 'USER'

    try:
        producer.send(PLAYLISTS_TOPIC, key=str(playlist['pid']), value=playlist)
        producer.flush()
    except Exception as e:
        print(f"Error sending user playlist to Kafka: {e}")
        return jsonify({"error": "Failed to send playlist"}), 500

    return jsonify({"status": "ok"}), 200

def consume_artist_updates():
    global playlists_memory
    global artist_data_store

    for message in consumer:
        try:
            artist_data = message.value
            artist_uri = artist_data['uri']

            with lock:
                artist_data_store[artist_uri] = artist_data

            print(f"Received and stored artist data: {artist_uri}. Total artists in memory: {len(artist_data_store)}")

        except Exception as e:
            print(f"Error processing artist update: {e}")

# Start the Kafka consumer thread
consumer_thread = threading.Thread(target=consume_artist_updates, daemon=True)
consumer_thread.start()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8888, debug=False)

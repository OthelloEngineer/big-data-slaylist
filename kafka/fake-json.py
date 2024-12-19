import json
import random
from faker import Faker
from uuid import uuid4

fake = Faker()

# Enum values for 'origin'
origins = ["USER", "DATASET"]

genre = ["rock", "jazz", "pop", "edm", "hip hop", "classical", "blues", "reggae", "country", "metal", "r&b", "indie", "soul", "funk", "folk", "punk", "latin", "techno", "trap", "gospel"]

# Helper function to generate the data
def generate_playlist(pid):
    playlist = {
        "name": fake.company(),
        "collabortive": "false",
         "pid": pid, # Unique playlist ID
         "modifired_at" : random.randint(10,1000000000),
         "num_tracks": random.randint(5, 1000),  # Random number of songs
         "num_albums": random.randint(1, 100),  # Random number of albums
         "num_followers": random.randint(10, 50000),  # Random number of followers
        "tracks": [fake.word() for _ in range(random.randint(5, 15))],  # Random number of songs (5-15),
        "num_edits": random.randint(1,100),
        "duration_ms": random.randint(10,1000000),
        "num_artists": random.randint(5, 20),  # Random number of artists
         "origin": random.choice(origins),  # Random origin value
        "genres": {random.choice(genre): random.randint(1, 5) for _ in range(random.randint(2, 5))},  # Random genres with ratings   
    }
    return playlist

# Generate 500 unique playlists
playlists = []
for i in range(1, 501):  # Generate 500 entries
    playlist = generate_playlist(i)
    playlists.append(playlist)

# Save the generated playlists to a JSON file
with open("playlists.json", "w") as f:
    json.dump(playlists, f, indent=4)

print("500 unique playlists have been generated and saved to 'playlists.json'.")

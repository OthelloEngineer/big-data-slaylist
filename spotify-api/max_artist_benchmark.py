import string
from dataclasses import dataclass
import random
from sys import getsizeof
import time
from typing import List


@dataclass
class Artist:
    genres: List[str]
    name: str
    popularity: int
    uri: str



if __name__ == "__main__":
    my_artists = []

    start_time = time.monotonic_ns()

    for i in range(298000):
        my_genre = [''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(5))]
        my_name = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(5))
        my_popul = random.randint(0, 100)
        uri = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(5))

        artist = Artist(my_genre, my_name, my_popul, uri)

        my_artists.append(artist)

    end_time = time.monotonic_ns()

    print(f"Time taken: {(end_time - start_time)/1_000_000_000}s")
    print(f"len: {len(my_artists)}")
    print(f" size, bytes: {getsizeof(my_artists)}")
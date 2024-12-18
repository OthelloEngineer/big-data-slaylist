from dataclasses import asdict, dataclass, field
from io import BytesIO
import os
from typing import List, Optional, Union
from enum import Enum


@dataclass
class Artist:
    artist_name: str
    artist_uri: str
    popularity: int
    genres: List[str] = field(default_factory=list)


@dataclass
class Song:
    track_uri: str
    track_name: str
    duration_ms: str
    artist: Optional[Artist] = None


class Origin(Enum):
    USER = "USER"
    DATASET = "DATASET"
    SPOTIFY_TOP = "SPOTIFY_TOP"


@dataclass
class Playlist:
    playlist_name: str
    followers: int
    num_artists: int
    num_songs: int
    num_albums: int
    origin: Origin
    pid: int
    songs: List[Song] = field(default_factory=list)


@dataclass
class ArtistRequest:
    artist_uri: str


@dataclass
class ArtistResponse:
    artist_uri: str
    artist: Artist


if __name__ == "__main__":
  from fastavro import writer, reader, validate
  from fastavro.schema import load_schema

  # Define the repository directory

  # Load schemas with dependency resolution
  artist_request_schema = load_schema("../protobuf/artist_request.avsc")
  artist_schema = load_schema("../protobuf/spotifyapi.Artist.avsc")
  artist_response_schema = load_schema("../protobuf/artist_response.avsc")

  # Create an ArtistRequest instance
  artist_request = asdict(ArtistRequest(artist_uri="spotify:artist:1"))
  print(f"is artist_request schema correct? {validate(artist_request, artist_request_schema)}")
  bin_io = BytesIO()
  writer(bin_io, artist_request_schema, [artist_request])
  avro_bytes = bin_io.getvalue()
  print(f"ArtistRequest: {artist_request}")

  # Read back the serialized data
  read_avro = list(reader(BytesIO(avro_bytes), artist_request_schema))
  print(f"Read Avro (ArtistRequest): {read_avro}")

  # Create an ArtistResponse instance
  artist = asdict(Artist(artist_name="artist", artist_uri="spotify:artist:1", popularity=100, genres=["pop"]))
  artist_response = asdict(ArtistResponse(artist_uri="spotify:artist:1", artist=artist))

  print(f"is artist_response schema correct? {validate(artist_response, artist_response_schema)}")

  bin_io = BytesIO()
  writer(bin_io, artist_response_schema, [artist_response])
  avro_bytes = bin_io.getvalue()
  print(f"ArtistResponse: {artist_response}")

  # Read back the serialized data
  read_avro = list(reader(BytesIO(avro_bytes), artist_response_schema))
  print(f"Read Avro (ArtistResponse): {read_avro}")
  mapped_artist = Artist(**read_avro[0]["artist"])
  mapped_artist_response = ArtistResponse(artist_uri=read_avro[0]["artist_uri"], artist=mapped_artist)
  print(f"ArtistResponse: {mapped_artist_response}")



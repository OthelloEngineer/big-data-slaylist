from google.protobuf.internal import containers as _containers
from google.protobuf.internal import enum_type_wrapper as _enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Iterable as _Iterable, Mapping as _Mapping, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class Song(_message.Message):
    __slots__ = ("track_uri", "track_name", "artist", "duration_ms")
    TRACK_URI_FIELD_NUMBER: _ClassVar[int]
    TRACK_NAME_FIELD_NUMBER: _ClassVar[int]
    ARTIST_FIELD_NUMBER: _ClassVar[int]
    DURATION_MS_FIELD_NUMBER: _ClassVar[int]
    track_uri: str
    track_name: str
    artist: Artist
    duration_ms: str
    def __init__(self, track_uri: _Optional[str] = ..., track_name: _Optional[str] = ..., artist: _Optional[_Union[Artist, _Mapping]] = ..., duration_ms: _Optional[str] = ...) -> None: ...

class Artist(_message.Message):
    __slots__ = ("artist_name", "artist_uri", "genres", "popularity")
    ARTIST_NAME_FIELD_NUMBER: _ClassVar[int]
    ARTIST_URI_FIELD_NUMBER: _ClassVar[int]
    GENRES_FIELD_NUMBER: _ClassVar[int]
    POPULARITY_FIELD_NUMBER: _ClassVar[int]
    artist_name: str
    artist_uri: str
    genres: _containers.RepeatedScalarFieldContainer[str]
    popularity: int
    def __init__(self, artist_name: _Optional[str] = ..., artist_uri: _Optional[str] = ..., genres: _Optional[_Iterable[str]] = ..., popularity: _Optional[int] = ...) -> None: ...

class Playlist(_message.Message):
    __slots__ = ("songs", "playlist_name", "followers", "num_artists", "num_songs", "num_albums", "origin", "pid")
    class Origin(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
        __slots__ = ()
        USER: _ClassVar[Playlist.Origin]
        DATASET: _ClassVar[Playlist.Origin]
        SPOTIFY_TOP: _ClassVar[Playlist.Origin]
    USER: Playlist.Origin
    DATASET: Playlist.Origin
    SPOTIFY_TOP: Playlist.Origin
    SONGS_FIELD_NUMBER: _ClassVar[int]
    PLAYLIST_NAME_FIELD_NUMBER: _ClassVar[int]
    FOLLOWERS_FIELD_NUMBER: _ClassVar[int]
    NUM_ARTISTS_FIELD_NUMBER: _ClassVar[int]
    NUM_SONGS_FIELD_NUMBER: _ClassVar[int]
    NUM_ALBUMS_FIELD_NUMBER: _ClassVar[int]
    ORIGIN_FIELD_NUMBER: _ClassVar[int]
    PID_FIELD_NUMBER: _ClassVar[int]
    songs: _containers.RepeatedCompositeFieldContainer[Song]
    playlist_name: str
    followers: int
    num_artists: int
    num_songs: int
    num_albums: int
    origin: Playlist.Origin
    pid: int
    def __init__(self, songs: _Optional[_Iterable[_Union[Song, _Mapping]]] = ..., playlist_name: _Optional[str] = ..., followers: _Optional[int] = ..., num_artists: _Optional[int] = ..., num_songs: _Optional[int] = ..., num_albums: _Optional[int] = ..., origin: _Optional[_Union[Playlist.Origin, str]] = ..., pid: _Optional[int] = ...) -> None: ...

class ArtistRequest(_message.Message):
    __slots__ = ("artist_uri",)
    ARTIST_URI_FIELD_NUMBER: _ClassVar[int]
    artist_uri: str
    def __init__(self, artist_uri: _Optional[str] = ...) -> None: ...

class ArtistResponse(_message.Message):
    __slots__ = ("artist_uri", "artist")
    ARTIST_URI_FIELD_NUMBER: _ClassVar[int]
    ARTIST_FIELD_NUMBER: _ClassVar[int]
    artist_uri: str
    artist: Artist
    def __init__(self, artist_uri: _Optional[str] = ..., artist: _Optional[_Union[Artist, _Mapping]] = ...) -> None: ...

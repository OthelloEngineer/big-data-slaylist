import time
from typing import List

from attr import dataclass


@dataclass
class ArtistRequest:
    id: str


@dataclass
class Token:
    token: str
    is_valid: bool
    started_at = time.monotonic_ns()

    @staticmethod
    def new_placeholder_token():
        return Token(token="placeholder", is_valid=False)


@dataclass
class ExternalUrls:
    spotify: str


@dataclass
class Followers:
    href: None
    total: int


@dataclass
class Image:
    url: str
    height: int
    width: int


@dataclass
class Artist:
    external_urls: ExternalUrls
    followers: Followers
    genres: List[str]
    href: str
    id: str
    images: List[Image]
    name: str
    popularity: int
    type: str
    uri: str

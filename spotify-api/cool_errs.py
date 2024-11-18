import enum

from attr import dataclass


class Errors(enum.Enum):
    INVALID_TOKEN = 1
    RATE_LIMIT = 2
    ARTIST_NOT_FOUND = 3


@dataclass
class Error:
    error: Errors
    message: str
    artist_id: str

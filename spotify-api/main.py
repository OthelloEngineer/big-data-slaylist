import logging
from contextlib import asynccontextmanager

import asyncio
import os
import time

import aiohttp
from fastapi import FastAPI

import cool_errs
import data

token: data.Token = data.Token.new_placeholder_token()


logger = logging.Logger("uvicorn.error")
logger.setLevel(logging.INFO)

if not logger.hasHandlers():
    console_handler = logging.StreamHandler()
    console_handler.setFormatter(logging.Formatter(
        "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    ))
    logger.addHandler(console_handler)

async def fetch_artist(artist_req: data.ArtistRequest, session: aiohttp.ClientSession) -> data.Artist | cool_errs.Error:
    global token
    url = f"https://api.spotify.com/v1/artists/{artist_req.id}"
    async with session.get(url, headers={"Authorization": f"Bearer {token.token}"}) as response:
        response_json = await response.json()
        if response.status == 404:
            logger.info(f"response: {response_json}")
            logger.info(f"404 artist not found: {artist_req.id}")
            return cool_errs.Error(cool_errs.Errors.ARTIST_NOT_FOUND, "Artist not found", artist_req.id)
        if response.status == 429:
            logger.info(f"response: {response_json}")
            logger.info("Rate limit")
            return cool_errs.Error(cool_errs.Errors.RATE_LIMIT, "Rate limit", artist_req.id)
        if response.status == 401:
            logger.info(f"response: {response_json}")
            logger.info("Invalid token")
            return cool_errs.Error(cool_errs.Errors.INVALID_TOKEN, "Invalid token", artist_req.id)
        return data.Artist(**response_json)


def get_artist_id() -> data.ArtistRequest:
    # kafka consumer goes here
    return data.ArtistRequest(id="0TnOYISbd1XYRBk9myaseg")


def produce_artist(artist: data.Artist):
    # kafka producer goes here
    logger.info(artist)


@asynccontextmanager
async def lifespan(app: FastAPI):
    asyncio.create_task(start_token_loop())
    yield


async def start_token_loop():
    global token
    loop_time = 2_000_000_000
    logger.info("Starting token loop")
    token_err_count = 0
    while True:
        if not token.is_valid:
            logger.info("Token is invalid")
            await asyncio.sleep(loop_time / 1_000_000_000)
            continue
        start_time = time.monotonic_ns()
        artist_id = get_artist_id()
        async with aiohttp.ClientSession() as session:
            artist = await fetch_artist(artist_id, session)
            if isinstance(artist, cool_errs.Error):
                if artist.error == cool_errs.Errors.INVALID_TOKEN:
                    token_err_count += 1
                    logger.info(f"Invalidating token: {token.token}")
                elif artist.error == cool_errs.Errors.RATE_LIMIT:
                    token_err_count += 1
                    logger.info(f"Rate limit: {token.token}")
                elif artist.error == cool_errs.Errors.ARTIST_NOT_FOUND:
                    logger.info(f"Artist not found: {artist_id}")
            else:
                logger.info(artist)
            if token_err_count > 5:
                token.is_valid = False
        while time.monotonic_ns() - start_time < loop_time:
            await asyncio.sleep(0.1)

app = FastAPI(docs_url=None, redoc_url=None, lifespan=lifespan)

@app.post("/new-token/{provided_token}")
def new_token(provided_token: str):
    global token
    logger.info("New token")

    while True:
        # check own token
        if not token.is_valid:
            token = data.Token(token=provided_token, is_valid=True)
            return "Token updated"

        # check other tokens using ordinal names
        pod_name = os.getenv("POD_NAME")
        stateful_set_name = pod_name[:pod_name.rfind("-")]
        pod_number = int(pod_name[pod_name.rfind("-") + 1:])
        try:
            # Try next pod in the stateful set
            msg = aiohttp.ClientSession(timeout=1).get(
                f"http://{stateful_set_name}-{str(pod_number+1)}:5000/new_token?token={provided_token}")
            if msg == "Token updated":
                return "Token was accepted :)"
            if msg == "Token was not accepted :(":
                return
        except:
            # If top pod timed out then try from the bottom (0)
            msg = aiohttp.ClientSession().get(
                f"http://{stateful_set_name}-0:5000/new_token?token={provided_token}")
            if msg == "Token updated":
                return "Token was accepted :)"
        return "Token was not accepted :("


if __name__ == '__main__':
    pod_name = "spotify-api-0"
    print(pod_name[:pod_name.rfind("-")])
    pod_number = int(pod_name[pod_name.rfind("-") + 1:])
    print(pod_number)
    #import uvicorn
#
 #   uvicorn.run(app, host="localhost", port=5000)

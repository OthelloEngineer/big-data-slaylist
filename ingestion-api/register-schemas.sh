#!/bin/bash

SCHEMA_REGISTRY_URL="http://schema-registry:8081"

echo "Registering schema: spotifyapi.Artist.avsc to subject: spotifyapi.Artist-value"
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
     --data "{\"schema\": $(jq -Rs . < /schemas/spotifyapi.Artist.avsc)}" \
     ${SCHEMA_REGISTRY_URL}/subjects/spotifyapi.Artist-value/versions

echo "Registering schema: spotifyapi.Song.avsc to subject: spotifyapi.Song-value"
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
     --data "{\"schema\": $(jq -Rs . < /schemas/spotifyapi.Song.avsc)}" \
     ${SCHEMA_REGISTRY_URL}/subjects/spotifyapi.Song-value/versions

echo "Registering schema: artist_request.avsc to subject: artist_request-value"
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
     --data "{\"schema\": $(jq -Rs . < /schemas/artist_request.avsc)}" \
     ${SCHEMA_REGISTRY_URL}/subjects/artist_request-value/versions

# Retrieve the ID of the Song schema
SONG_SCHEMA_ID=$(curl -s "${SCHEMA_REGISTRY_URL}/subjects/spotifyapi.Song-value/versions/latest" | jq -r .id)

echo "Registering schema: playlist.avsc to subject: playlist-value with Song reference"
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
     --data "{
       \"schema\": $(jq -Rs . < /schemas/playlist.avsc),
       \"references\": [
         {\"name\": \"Song\", \"subject\": \"spotifyapi.Song-value\", \"version\": 1}
       ]
     }" \
     ${SCHEMA_REGISTRY_URL}/subjects/playlist-value/versions

echo "Schema registration completed!"

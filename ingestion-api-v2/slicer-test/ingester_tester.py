import requests
import json

def post_json_file(file_path, url):
    with open(file_path, 'r') as file:
        data = json.load(file)
    response = requests.post(url, json=data)
    return response

# Define the URL
url = 'http://localhost:8888/ingest/dataset'

# Post the first JSON file
response1 = post_json_file('mpd.slice.0-999.json', url)
print(response1.status_code)
print(response1.json())

# If the first request is successful, post the second JSON file
if response1.status_code == 200 and response1.json().get("status") == "ok":
    response2 = post_json_file('mpd.slice.1000-1999.json', url)
    print(response2.status_code)
    print(response2.json())
import json
import requests
import os
import re

# Path to the data folder
data_folder = os.path.join(os.path.dirname(__file__), 'data')

# Define the URL
url = 'http://localhost:8888/process_dataset'

# Natural sorting function
def natural_sort_key(s):
    return [int(text) if text.isdigit() else text.lower() for text in re.split('(\d+)', s)]

# Get a naturally sorted list of JSON files in the data folder
json_files = sorted([f for f in os.listdir(data_folder) if f.endswith('.json')], key=natural_sort_key)

# Iterate through each file in the sorted list
for filename in json_files:
    file_path = os.path.join(data_folder, filename)
    
    # Read the contents of the JSON file
    with open(file_path, 'r') as file:
        file_contents = json.load(file)
    
    # Send the POST request
    response = requests.post(url, json=file_contents)
    
    # Print the response
    print(f'Posting {filename}: {response.status_code}')
    print(response.text)
    
    # Check if the response is OK before proceeding to the next file
    if response.status_code != 200:
        print(f'Failed to post {filename}, stopping.')
        break
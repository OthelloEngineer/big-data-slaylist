from flask import Flask, request, jsonify
import requests
import os
from querryCatalouge import get_catalouge
import json
app = Flask(__name__)

@app.route('/test')
def index():
    return 'API is running! \n'

@app.route('/catalouge', methods=['POST'])
def catalouge():
    data = request.json
    playlists = get_catalouge(data["genre1"],data["genre2"])
   
    return jsonify(playlists)


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000)
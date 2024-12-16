from flask import Flask

app = Flask(__name__)

@app.route("/test", methods = ['GET'])
def hello_world():
    return "API is Running \n"

@app.route("/catalouge", methods = ['POST'])
def catalouge()

if __name__ == '__main__':
    app.run()
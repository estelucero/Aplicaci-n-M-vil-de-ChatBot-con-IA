from flask import Flask, jsonify, request

from chatbot import answer_message
#from get_ip import get_local_ip

app = Flask(__name__)
count = 0

@app.route('/test', methods=['GET'])
def test():
  global count
  count += 1
  data = {
    "apiCallsCount": count,
    "answer" : "ok"
  }
  return jsonify(data)

# Contador para llevar registro de las llamadas al endpoint
# Define el endpoint "/message"
@app.route('/message', methods=['GET'])
def message():
  global count
  message = request.args.get("message")
  count += 1
  data = {
    "apiCallsCount": count,
    "answer" : answer_message(message)
  } 
  return jsonify(data)

if __name__ == '__main__':
    host ="0.0.0.0"
    user_input = input("Ingrese IP : ")
    if (user_input != ""):
      host = user_input

    user_input = input("Ingrese port (enter for default 5000): ")
    port = int(user_input) if user_input != "" else 5000

    app.run(host, port)
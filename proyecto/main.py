from chatbot import answer_message
    
print("Bienvenido a SAC-Fútbol ChatBot, ¿En que puedo ayudarle?")
# Ejecutamos el chat en bucle
while True:
    message = input("")
    res = answer_message(message)
    print (res)
    if 'nos vemos' in res:
        break
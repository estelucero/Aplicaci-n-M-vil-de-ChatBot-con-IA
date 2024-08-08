import random
import json
import pickle
import numpy as np
import tensorflow as tf

import nltk
from nltk.stem import WordNetLemmatizer

lemmatizer = WordNetLemmatizer()

intents = json.loads(open('intents.json').read())

words = []

classes = []
documents = []
ignoreLetters = ['?', '!', '.', ',']
#Iteramos el archivo intents
for intent in intents['intents']:
    #Iteramos las palabras de cada patron del archivo
    for pattern in intent['patterns']:

        #Tokenizamos o separamos cada palabra del padron
        wordList = nltk.word_tokenize(pattern)
        #Lo agregamos a la lista words
        words.extend(wordList)
        #Agregamos la tupla de la lista de palabras tokenizadas y el tag del patron de cada palabra a la lista de documentos
        documents.append((wordList, intent['tag']))
        #Agrega a la lista de clases el tag si no esta agregado ya
        if intent['tag'] not in classes:
            classes.append(intent['tag'])

#Lematiza las palabras que se encuentran en words por ende todos los patrones que no se ecuentren en palabras ignoradas
words = [lemmatizer.lemmatize(word) for word in words if word not in ignoreLetters]

#Ordena las tuplas generadas anteriormente
words = sorted(set(words))
#Ordena la lista de clases que tiene los nombres de los patrones
classes = sorted(set(classes))
#Serializa la lista de clases y palabras
pickle.dump(words, open('words.pkl', 'wb'))
pickle.dump(classes, open('classes.pkl', 'wb'))

training = []
outputEmpty = [0] * len(classes)
#Se itera los patrones con palabras junto con el nombre de su tag o clase
for document in documents:
    bag = []
    #Se guardan en este lista las palabras de los patrones
    wordPatterns = document[0]
    #Se lematizan las palabras de los patrones y generas una lista de las palabras del patron
    wordPatterns = [lemmatizer.lemmatize(word.lower()) for word in wordPatterns]
    #Se recorre las palabras de todos los patrones
    for word in words:
        #Se Verifica si la palabra en el patron esta en la lista y a partir de esto agrega un 1 a la lista bag, sino agrega un 0
        bag.append(1) if word in wordPatterns else bag.append(0)
    #Se crea una lista vacia
    outputRow = list(outputEmpty)
    #Coloca un 1 en la lista de outputRow en la posicion donde se encuentra el tag en la lista de clase
    outputRow[classes.index(document[1])] = 1

    #Concatena las dos listas de bag y outputRow
    training.append(bag + outputRow)
#Esto mezcla los vectores de entrenamiento
random.shuffle(training)

training = np.array(training)
#Se obtiene una matriz bidimensional con los valores de training pero solo de las palabras
trainX = training[:, :len(words)]
#Se obtiene una matriz bidimensional con los valores de training pero solo de las clases
trainY = training[:, len(words):]
#Configuracion del modelo de red neuronales
model = tf.keras.Sequential()
model.add(tf.keras.layers.Dense(128, input_shape=(len(trainX[0]),), activation = 'relu'))
model.add(tf.keras.layers.Dropout(0.5))
model.add(tf.keras.layers.Dense(64, activation = 'relu'))
model.add(tf.keras.layers.Dropout(0.5))
model.add(tf.keras.layers.Dense(len(trainY[0]), activation='softmax'))
#Configuraciones tasas de optimizacion
sgd = tf.keras.optimizers.SGD(learning_rate=0.01, momentum=0.9, nesterov=True)
model.compile(loss='categorical_crossentropy', optimizer=sgd, metrics=['accuracy'])
#Entrenamos modelo
hist =model.fit(trainX, trainY, epochs=200, batch_size=5, verbose=1)
#Guardamos el modelo
model.save('chatbot_model.h5',hist)
print('Done')
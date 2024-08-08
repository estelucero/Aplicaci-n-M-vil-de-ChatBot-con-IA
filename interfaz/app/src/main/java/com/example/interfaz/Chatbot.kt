package com.example.interfaz

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ChatbotService {
  @GET("/message")
  suspend fun getMessage(
    @Query("message") message: String
  ):RemoteResult

  @GET("/test")
  suspend fun test():RemoteResult
}
class RemoteResult(val apiCallsCount:Int, val answer:String)

object ChatbotServerConfig {
  const val IP_ADDRESS = "192.168.1.33"
  const val PORT = 5000
}

class Chatbot : AppCompatActivity() {
  private val TAG = "CHATBOT" // debug
  private lateinit var retrofit: Retrofit
  private lateinit var botService: ChatbotService

  private lateinit var btnSend: Button
  private lateinit var rvMessages: RecyclerView
  private lateinit var etMessage: EditText
  private lateinit var adapter: MessageAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.chat)



    btnSend = findViewById(R.id.btn_send)
    rvMessages = findViewById(R.id.rv_messages)
    etMessage = findViewById(R.id.et_message)

    adapter = MessageAdapter()
    rvMessages.adapter = adapter
    rvMessages.layoutManager = LinearLayoutManager(applicationContext)

    btnSend.setOnClickListener {
      sendMessage()
    }

    //Scroll back to correct position when user clicks on text view
    etMessage.setOnClickListener {
      lifecycleScope.launch {
        withContext(Dispatchers.Main) {
          rvMessages.scrollToPosition(adapter.itemCount - 1)
        }
      }
    }

    adapter.insertMessage(Message("Bienvenido a SAC-Fútbol Chatbot! ¿En qué lo puedo ayudar?", Sender.BOT))
    rvMessages.scrollToPosition(adapter.itemCount - 1)

    runBlocking{
      try {
        retrofit = Retrofit.Builder()
          .baseUrl("http://192.168.1.42:5000")
          .addConverterFactory(GsonConverterFactory.create()) // Add your converter
          .build()
        botService = retrofit.create(ChatbotService::class.java)
        val response = botService.test()
        Log.i(TAG, "Server funcionando correctamente")
      }catch (e:Exception){
        Log.e(TAG,"server error: " + e.message, e)
        Toast.makeText(applicationContext,"Error de conexión. Por favor, cierre la aplicación",Toast.LENGTH_LONG).show()
      }
    }

  }

  override fun onStart() {
    super.onStart()
    //In case there are messages, scroll to bottom when re-opening app
    lifecycleScope.launch {
      delay(100)
      withContext(Dispatchers.Main) {
        rvMessages.scrollToPosition(adapter.itemCount - 1)
      }
    }
  }

  private fun sendMessage() {
    val message = etMessage.text.toString()
    if (message.isNotEmpty()) {

      etMessage.setText("")

      adapter.insertMessage(Message(message, Sender.USER))
      rvMessages.scrollToPosition(adapter.itemCount - 1)

      botResponse(message)
    }
  }

  private fun botResponse(message: String) {
    lifecycleScope.launch {
      try {
        val serverResult = botService.getMessage(message)
        adapter.insertMessage(Message(serverResult.answer, Sender.BOT))
        rvMessages.scrollToPosition(adapter.itemCount - 1)
      } catch (e: Exception) {
        Log.e(TAG, e.message, e)
        Toast.makeText(applicationContext, "Ocurrio excepcion: " + e.message, Toast.LENGTH_LONG).show()
      }
    }
  }
}
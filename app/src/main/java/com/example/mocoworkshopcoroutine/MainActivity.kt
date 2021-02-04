package com.example.mocoworkshopcoroutine

import android.R.id
import android.annotation.SuppressLint
import android.app.Service
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.*
import android.widget.TextView
import android.widget.Button
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject


// HOW TO - Netzwerkabfragen
// 1. Internet permission hinzufügen (https://java2blog.com/add-internet-permission-in-androidmanifest-android-studio/)
// 2. Kotlin X library als dependency hinzufügen (In den Folien schauen wie)
// 3. Volley library als dependency hinzufügen (https://developer.android.com/training/volley)
// 4. Textfeld hinzufügen, worin stehen soll, ob man mit dem Internet verbunden ist
// 5. Internetverbindung überprüfen (Code von Ali im Google Drive)
// 6. Zwei Textfelder hinzufügen (URL und Response) und einen Button hinzufügen (Send). Diese müssen im Code ebenfalls refferenziert werden (Beispiel: val textResponse: TextView = findViewById(R.id.textViewResponse))
// 7. Tutorial von: https://www.youtube.com/watch?v=0Y8RTKC935I&ab_channel=DroidGraphy

// HOW TO - Coroutinen
// 1. Tutorial von: https://www.youtube.com/watch?v=F63mhZk-1-Y&ab_channel=CodingWithMitch
// 2. Kotlin X library als dependency hinzufügen (Falls nicht schon geschehen)

class MainActivity : AppCompatActivity() {


    // onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Define Textviews and Buttons
        val textResponse: TextView = findViewById(R.id.textViewResponse)
        val textURL: TextView = findViewById(R.id.textViewURL)
        val textConn: TextView = findViewById(R.id.textViewConn)
        val buttonSend: Button = findViewById(R.id.buttonSend)
        val buttonFakeRequest: Button = findViewById(R.id.buttonMessage)

        // überprüfung der Netzwerkverbindung
        if (isconnected()) {
            textConn.text = "Connected to the internet."
        }
        else {
            textConn.text = "Not connected to the internet."
        }


        // Coroutine example
        buttonFakeRequest.setOnClickListener {
            // launch coroutine
            // IO, Main, Default
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }

        // our URL
        val url = "https://jsonplaceholder.typicode.com/todos/1"

        // When button is clicked on
        buttonSend.setOnClickListener {

            if (isconnected()) { // Check if app is connected to the internet

                textConn.text = "Connected to the internet."

                textURL.text = url

                // Make a string request
                val stringRequest = StringRequest(
                        Request.Method.GET,
                        url,
                        { responseString ->
                            // Response String
                            val jsonObject = JSONObject(responseString)
                            textResponse.text = jsonObject.getString("title")
                        },
                        {
                            // Volley error, if any
                        }
                )

                // Create volley request queue and add our request
                Volley.newRequestQueue(this).add(stringRequest)
            } else textConn.text = "Not connected to the internet."
        }
    }

    // Function for connectivity
    private fun isconnected():Boolean {
        val connectivity = this.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

            val info = connectivity.activeNetworkInfo
            if (info != null) {
                if (info.state == NetworkInfo.State.CONNECTED){

                    return true
                }
            }
        return false
    }

    // Functions for Coroutines

    private fun setNewText(input: String) {
        val textResult: TextView = findViewById(R.id.textViewResult)
        val newText = textResult.text.toString() + "\n$input"
        textResult.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun fakeApiRequest() {
        val result1 = getResult1FromApi()
        println("debug: $result1")

        // Now set text on main thread
        setTextOnMainThread(result1)

        // Now result 2
        val result2 = getResult2FromApi()
        setTextOnMainThread(result2)
    }

    private suspend fun getResult1FromApi() : String {
        logThread("getResult1FromApi")
        delay(1000) // Will only delay coroutine, not thread!
        // Similar to Thread.sleep(1000) --- Coroutines != Thread
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        logThread("getResult2FromApi")
        delay(1000)
        return "Result #2"
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }

}
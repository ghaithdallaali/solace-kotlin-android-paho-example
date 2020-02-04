package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.mqtt.MqttClientHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"

        // Define these values in res/values/strings.xml
        const val TOPIC = "my/first/topic/name"
        const val MSG = "My string message payload"
    }

    val mqttClient by lazy {
        MqttClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setMqttCallBack()

        // initialize 'num msgs received' field in the view
        EditText1.setText("0")

        // pub fab button
        fab.setOnClickListener { view ->
            var snackbarMsg : String
            try {
                mqttClient.publish(TOPIC, MSG)
                snackbarMsg = "Published to topic '$TOPIC'!"
            } catch (ex: MqttException) {
                snackbarMsg = "Error publishing to topic: $TOPIC!"
            }
            Snackbar.make(view, snackbarMsg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // sub fab button
        fab2.setOnClickListener { view ->
            var snackbarMsg : String
            try {
                mqttClient.subscribe(TOPIC)
                snackbarMsg = "Subscribed to topic '$TOPIC'!"
            } catch (ex: MqttException) {
                snackbarMsg = "Error subscribing to topic: $TOPIC!"
            }
            Snackbar.make(view, snackbarMsg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        Timer("CheckMqttConnection", false).schedule(3000) {
            if (!mqttClient.isConnected()) {
                Snackbar.make(EditText1, "Failed to connect to: '$SOLACE_MQTT_HOST' within 3 seconds", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show()
            }
        }

    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connected to host '$SOLACE_MQTT_HOST'.")
            }
            override fun connectionLost(throwable: Throwable) {
                Log.w("Debug", "Connected to host '$SOLACE_MQTT_HOST' lost.")
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Debug", "Message received from host '$SOLACE_MQTT_HOST': $mqttMessage")
                EditText1.setText("${EditText1.text.toString().toInt() + 1}")

            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$SOLACE_MQTT_HOST'")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }

}

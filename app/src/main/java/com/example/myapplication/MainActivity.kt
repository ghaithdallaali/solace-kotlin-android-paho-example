package com.example.myapplication

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

import com.example.myapplication.mqtt.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"

        // Define these values in res/values/strings.xml
        const val TOPIC = "my/first/topic/name"
        const val MSG_PAYLOAD = "My string message payload"
    }

    val mqttClient by lazy {
        MqttClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Connect to your Solace Broker
        mqttClient.connect(SOLACE_MQTT_HOST)
        mqttClient.setCallBack(arrayOf(TOPIC), ::setData)

        fab.setOnClickListener { view ->
            mqttClient.publishMessage(TOPIC, MSG_PAYLOAD)
            Snackbar.make(view, "Message sent!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
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
        super.onDestroy()
        mqttClient.close()
    }

    private fun setData(topic: String, msg: MqttMessage) {
        when (topic) {
            TOPIC -> {
                "${String(msg.payload)}"
            }
        }
    }
}

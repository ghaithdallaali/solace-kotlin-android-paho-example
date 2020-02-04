package com.example.myapplication.mqtt

import android.content.Context
import android.util.Log
import com.example.myapplication.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions

class MqttClient(private val context: Context) {

    lateinit var client: MqttAndroidClient

    companion object {
        const val TAG = "MqttClient"
    }

    fun connect(broker: String) {
        client =
            MqttAndroidClient(context, broker,
                MqttClient.generateClientId())

        // set connection options before connecting
        val connectionOptions = MqttConnectOptions();
        connectionOptions.userName = SOLACE_CLIENT_USER_NAME
        connectionOptions.password = SOALCE_CLIENT_PASSWORD.toCharArray()
        connectionOptions.connectionTimeout = SOLACE_CONNECTION_TIMEOUT
        connectionOptions.keepAliveInterval = SOLACE_CONNECTION_KEEP_ALIVE_INTERVAL
        connectionOptions.isAutomaticReconnect = SOLACE_CONNECTION_RECONNECT
        connectionOptions.isCleanSession = SOLACE_CONNECTION_CLEAN_SESSION
        // connect to broker
        client.connect(connectionOptions)
    }

    fun setCallBack(topics: Array<String>? = null,
                    messageCallBack: ((topic: String, message: MqttMessage) -> Unit)? = null) {
        client.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                topics?.forEach {
                    subscribeTopic(it)
                }
                Log.d(TAG, "Connected to: $serverURI")
            }

            override fun connectionLost(cause: Throwable) {
                Log.d(TAG, "The Connection was lost.")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.d(TAG, "Incoming message from $topic: " + message.toString())
                messageCallBack?.invoke(topic, message)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })

    }

    fun publishMessage(topic: String, msg: String) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            client.publish(topic, message.payload, 0, false)
            Log.d(TAG, "$msg published to $topic")
        } catch (e: MqttException) {
            Log.d(TAG, "Error Publishing to $topic: " + e.message)
            e.printStackTrace()
        }

    }

    fun subscribeTopic(topic: String, qos: Int = 0) {
        client.subscribe(topic, qos).actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.d(TAG, "Subscribed to $topic")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.d(TAG, "Failed to subscribe to $topic")
                exception.printStackTrace()
            }
        }
    }

    fun close() {
        client.apply {
            unregisterResources()
            close()
        }
    }

    fun disconnect() {
        if (client.isConnected)
            client.disconnect()
    }
}
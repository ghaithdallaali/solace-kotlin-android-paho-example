# solace-kotlin-android-paho-example
A hello-world Android application written in Kotlin that uses Solace as an MQTT pub/sub message broker

## Creating a Solace Cloud Service
* Follow the "Get Started" steps [here](https://solace.com/products/event-broker/cloud/) to create a Solace Cloud account if you don't already have one
* Create a free Messaging Servie in a cloud provider and region of your choosing
* Once the Messaging Service is up, navigate to its "Connect" tab and find its MQTT Connection Details. These details will be used to connect your application(s) to the Solace PubSub+ Message broker 

![Alt text](/docs/solaceConnectionInfo.png)
<!-- .element height="50%" width="50%" -->

## Running the app
* Import the project into [Android Studio](https://developer.android.com/studio/index.html) after cloning it.
* Navigate to `app/src/main/java/com/example/myapplication/MessagingOptions.kt` and update the following constants based on your broker's connection details:
  * `SOLACE_MQTT_HOST`
  * `SOLACE_CLIENT_PASSWORD`
* Modify the `TOPIC` to publish/subscribe to, as well as the `MSG` payload under `MainActivity.kt`
* Build and run the sample app
  * The app has two buttons, one to have the app subscribe to a topic on the Solace broker, and the other is to have the app publish a message on the same topic to the broker. If the app was subscribed to the topic before a message was published, then the message will circle back to the app, and the counter in the center of the `MainActivity` will increment.

![Alt text](/docs/app.png?raw=true "Sample Application")
<!-- .element height="30%" width="30%" -->

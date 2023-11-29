package com.mqtt.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    //variables de la conexion MQTT
    private static String mqttHost = "tcp://lilybraid573.cloud.shiftr.io:1883";
    private static String IdUsuario = "AppAndroid";
    private static String Topico = "Mensaje";
    private static String User = "lilybraid573";
    private static String Pass = "NpNVVw29QNTgn1SA";
    //variable par imprimir los datos
    private TextView TextView;
    private EditText editTextMessage;
    private Button buttonSendMessage;
    //libreria MQTT
    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //enlazo las variables del xml
        TextView = findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.editTexMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        try {
            //creacion de un cliente en mqtt
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());
            //conexion con el servidor
            mqttClient.connect(options);
            //si se conceta se imprime un mensaje
            Toast.makeText(this, "Aplicacion Conectada al Servidor MQTT", Toast.LENGTH_SHORT).show();
            //manejo de ntrega de datos y perdida de conexion
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("Mqtt", "conexion Perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String playload = new String(message.getPayload());
                    runOnUiThread(() -> TextView.setText(playload));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("Mqtt", "Entrega Completa");

                }
            });

            //El cliente se suscribe al Topico
            mqttClient.subscribe(Topico);
            //Envio de mensaje al presionar el boton
            buttonSendMessage.setOnClickListener(v -> {
                String message = editTextMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    editTextMessage.getText().clear();
                } else {
                    Toast.makeText(this, "Ingrese un Mensaje", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Metodo para enviar Mensaje
    private void sendMessage(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttClient.publish(Topico, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // se Desconecta al cerra la aplicacion
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
package com.example.mqtttest;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.Toast;

//import org.eclipse.paho.android.service.MqttAndroidClient;
import com.jakewharton.threetenabp.AndroidThreeTen;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    private static final int RECONNECT_DELAY_MS = 5000;
    private Handler reconnectHandler = new Handler(Looper.getMainLooper());
    MqttAndroidClient client;
    MqttConnectOptions options = new MqttConnectOptions();
    //Khai báo giao diện
    TextView  txtnhietdo,txtdoamkk,txtdoamdat,txtuv,update;
    SwipeRefreshLayout swipeRefreshLayout;
    ToggleButton bomnuoc,thonggio,phunsuong,maiche,chedobomnuoc,chedothonggio,chedophunsuong,chedomaiche;
    EditText doammin,doammax,nhietdomin,nhietdomax,doamkkmin,doamkkmax,chisouvmin,chisouvmax;
    Double doamminvalue,doammaxvalue,nhietdominvalue,nhietdomaxvalue,doamkkminvalue,doamkkmaxvalue,chisouvminvalue,chisouvmaxvalue;

    final String bomnuocstatus = "control/bomnuoc/status";
    final String thonggiostatus = "control/thonggio/status";
    final String phunsuongstatus = "control/phunsuong/status";
    final String maichestatus = "control/maiche/status";
    
    final String doammintopic = "control/bomnuoc/doammin";
    final String doammaxtopic = "control/bomnuoc/doammax";
    final String nhietdomintopic = "control/thonggio/nhietdomin";
    final String nhietdomaxtopic = "control/thonggio/nhietdomax";
    final String doamkkmintopic = "control/phunsuong/doamkkmin";
    final String doamkkmaxtopic = "control/phunsuong/doamkkmax";
    final String chisouvmintopic = "control/maiche/chisouvmin";
    final String chisouvmaxtopic = "control/maiche/chisouvmax";

    final String cambiennhietdo = "cambien/nhietdo";
    final String cambiendoamkk = "cambien/doamkk";
    final String cambiendoamdat = "cambien/doamdat";
    final String cambienuv = "cambien/uv";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Ánh xạ Id
        txtnhietdo=findViewById(R.id.txtnhietdo);
        txtdoamkk=findViewById(R.id.txtdoamkk);
        txtdoamdat=findViewById(R.id.txtdoamdat);
        txtuv=findViewById(R.id.txtuv);

        bomnuoc=findViewById(R.id.bomnuoc);
        thonggio=findViewById(R.id.thongio);
        phunsuong=findViewById(R.id.phunsuong);
        maiche=findViewById(R.id.maiche);
        chedobomnuoc=findViewById(R.id.chedobomnuoc);
        chedothonggio=findViewById(R.id.chedothonggio);
        chedophunsuong=findViewById(R.id.chedophunsuong);
        chedomaiche=findViewById(R.id.chedomaiche);

        doammin=findViewById(R.id.doammin);
        doammax=findViewById(R.id.doammax);
        nhietdomin=findViewById(R.id.nhietdomin);
        nhietdomax=findViewById(R.id.nhietdomax);
        doamkkmin=findViewById(R.id.doamkkmin);
        doamkkmax=findViewById(R.id.doamkkmax);
        chisouvmin=findViewById(R.id.chisouvmin);
        chisouvmax=findViewById(R.id.chisouvmax);

        update=findViewById(R.id.update);

        update.setText("Đang cập nhật dữ liệu cảm biến...");

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Hành động khi người dùng kéo để làm mới
                refreshContent();
            }
        });
        //Xử lí
        /*
        btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String sendc = edt1.getText().toString();
            pub(sendc);
            }
        });
        */

        bomnuoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pub(bomnuocstatus,"1");

                } else {
                    pub(bomnuocstatus,"0");
                }
                if(chedobomnuoc.isChecked()) chedobomnuoc.setChecked(false);
            }
        });

        thonggio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pub(thonggiostatus,"1");
                } else {
                    pub(thonggiostatus,"0");
                }
                if(chedothonggio.isChecked()) chedothonggio.setChecked(false);
            }
        });

        phunsuong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pub(phunsuongstatus,"1");
                } else {
                    pub(phunsuongstatus,"0");
                }
                if(chedophunsuong.isChecked()) chedophunsuong.setChecked(false);
            }
        });

        maiche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pub(maichestatus,"1");
                } else {
                    pub(maichestatus,"0");
                }
                if(chedomaiche.isChecked()) chedomaiche.setChecked(false);
            }
        });

        chedobomnuoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked ) {
                    if(doammaxvalue!=null && doamminvalue!=null && doammaxvalue > doamminvalue) pub(bomnuocstatus,"2");
                    else{
                        chedobomnuoc.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(bomnuoc.isChecked())  pub(bomnuocstatus,"1");
                    else pub(bomnuocstatus,"0");;

                }

            }
        });

        chedothonggio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(nhietdomaxvalue!=null && nhietdominvalue!=null && nhietdomaxvalue > nhietdominvalue) pub(thonggiostatus,"2");
                    else{
                        chedothonggio.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(thonggio.isChecked())  pub(thonggiostatus,"1");
                    else pub(thonggiostatus,"0");;

                }

            }
        });

        chedophunsuong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(doamkkmaxvalue != null && doamkkminvalue != null && doamkkmaxvalue > doamkkminvalue) {
                        pub(phunsuongstatus,"2");
                    } else {
                        chedophunsuong.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if(phunsuong.isChecked())  pub(phunsuongstatus,"1");
                    else pub(phunsuongstatus,"0");;

                }

            }
        });

        chedomaiche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(chisouvmaxvalue != null && chisouvminvalue != null && chisouvmaxvalue > chisouvminvalue) {
                    pub(maichestatus,"2");
                } else {
                    chedomaiche.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
                } else {
                    if(maiche.isChecked())  pub(maichestatus,"1");
                    else pub(maichestatus,"0");;

                }

            }
        });


        doammin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = doammin.getText().toString();

                    if (value.isEmpty()) {
                        pub(doammintopic,"clear");
                        doamminvalue = null;
                        if(chedobomnuoc.isChecked()) {
                            chedobomnuoc.setChecked(false);
                            if(bomnuoc.isChecked())  pub(bomnuocstatus,"1");
                            else pub(bomnuocstatus,"0");;
                        }
                    }
                    else{
                       // DecimalFormat df = new DecimalFormat("#.##");
                        doamminvalue = round(Double.parseDouble(value),2);


                        if(doammaxvalue!=null && doammaxvalue<=doamminvalue){
                            doamminvalue = null;
                            doammin.setText("");
                            pub(doammintopic,"clear");
                            if(chedobomnuoc.isChecked()) {
                                chedobomnuoc.setChecked(false);
                                if(bomnuoc.isChecked())  pub(bomnuocstatus,"1");
                                else pub(bomnuocstatus,"0");;
                            }
                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        doammin.setText(doamminvalue+"");
                        pub(doammintopic,doamminvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });
        doammax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = doammax.getText().toString();
                    value= value.replace(",",".");
                    if (value.isEmpty()) {
                        pub(doammaxtopic,"clear");
                        doammaxvalue = null;
                        if(chedobomnuoc.isChecked()) {
                            chedobomnuoc.setChecked(false);
                            if(bomnuoc.isChecked())  pub(bomnuocstatus,"1");
                            else pub(bomnuocstatus,"0");;
                        }
                    }
                    else{

                       // DecimalFormat df = new DecimalFormat("#.##");
                        doammaxvalue =round(Double.parseDouble(value),2);
                        if(doamminvalue!=null && doammaxvalue<=doamminvalue ){
                            doammaxvalue = null;
                            doammax.setText("");
                            pub(doammaxtopic,"clear");
                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            if(chedobomnuoc.isChecked()) {
                                chedobomnuoc.setChecked(false);
                                if(bomnuoc.isChecked())  pub(bomnuocstatus,"1");
                                else pub(bomnuocstatus,"0");;
                            }
                            return;
                        }
                        doammax.setText(doammaxvalue+"");
                        pub(doammaxtopic,doammaxvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });

        nhietdomin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = nhietdomin.getText().toString();
                    value= value.replace(",",".");

                    if (value.isEmpty()) {
                        pub(nhietdomintopic,"clear");
                        nhietdominvalue = null;
                        if(chedothonggio.isChecked()) {
                            chedothonggio.setChecked(false);
                            if(thonggio.isChecked())  pub(thonggiostatus,"1");
                            else pub(thonggiostatus,"0");
                        }
                    }
                    else{
                        //DecimalFormat df = new DecimalFormat("#.##");
                        nhietdominvalue = round(Double.parseDouble(value),2);
                        if(nhietdomaxvalue!=null && nhietdomaxvalue<=nhietdominvalue){
                            nhietdominvalue = null;
                            nhietdomin.setText("");
                            pub(nhietdomintopic,"clear");
                            if(chedothonggio.isChecked()) {
                                chedothonggio.setChecked(false);
                                if(thonggio.isChecked())  pub(thonggiostatus,"1");
                                else pub(thonggiostatus,"0");
                            }
                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        nhietdomin.setText(nhietdominvalue+"");
                        pub(nhietdomintopic,nhietdominvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });
        nhietdomax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = nhietdomax.getText().toString();
                    value= value.replace(",",".");
                    if (value.isEmpty()) {
                        pub(nhietdomaxtopic,"clear");
                        nhietdomaxvalue = null;
                        if(chedothonggio.isChecked()) {
                            chedothonggio.setChecked(false);
                            if(thonggio.isChecked())  pub(thonggiostatus,"1");
                            else pub(thonggiostatus,"0");
                        }
                    }
                    else{
                        //DecimalFormat df = new DecimalFormat("#.##");
                        nhietdomaxvalue = round(Double.parseDouble(value),2);
                        if(nhietdominvalue!=null && nhietdomaxvalue<=nhietdominvalue ){
                            nhietdomaxvalue = null;
                            nhietdomax.setText("");
                            pub(nhietdomaxtopic,"clear");

                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            if(chedothonggio.isChecked()) {
                                chedothonggio.setChecked(false);
                                if(thonggio.isChecked())  pub(thonggiostatus,"1");
                                else pub(thonggiostatus,"0");
                            }
                            return;
                        }
                        nhietdomax.setText(nhietdomaxvalue+"");
                        pub(nhietdomaxtopic,nhietdomaxvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });

        doamkkmin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = doamkkmin.getText().toString();
                    value= value.replace(",",".");
                    if (value.isEmpty()) {
                        pub(doamkkmintopic,"clear");
                        doamkkminvalue = null;
                        if(chedophunsuong.isChecked()) {
                            chedophunsuong.setChecked(false);
                            if(phunsuong.isChecked())  pub(phunsuongstatus,"1");
                            else pub(phunsuongstatus,"0");
                        }
                    }
                    else{
                       // DecimalFormat df = new DecimalFormat("#.##");
                        doamkkminvalue = round(Double.parseDouble(value),2);
                        if(doamkkmaxvalue!=null && doamkkmaxvalue<=doamkkminvalue){
                            doamkkminvalue = null;
                            doamkkmin.setText("");
                            pub(doamkkmintopic,"clear");
                            if(chedophunsuong.isChecked()) {
                                chedophunsuong.setChecked(false);
                                if(phunsuong.isChecked())  pub(phunsuongstatus,"1");
                                else pub(phunsuongstatus,"0");
                            }
                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        doamkkmin.setText(doamkkminvalue+"");
                        pub(doamkkmintopic,doamkkminvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });
        doamkkmax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = doamkkmax.getText().toString();
                    value= value.replace(",",".");
                    if (value.isEmpty()) {
                        pub(doamkkmaxtopic,"clear");
                        doamkkmaxvalue = null;
                        if(chedophunsuong.isChecked()) {
                            chedophunsuong.setChecked(false);
                            if(phunsuong.isChecked())  pub(phunsuongstatus,"1");
                            else pub(phunsuongstatus,"0");
                        }
                    }
                    else{
                       // DecimalFormat df = new DecimalFormat("#.##");
                        doamkkmaxvalue = round(Double.parseDouble(value),2);
                        if(doamkkminvalue!=null && doamkkmaxvalue<=doamkkminvalue ){
                            doamkkmaxvalue = null;
                            doamkkmax.setText("");
                            pub(doamkkmaxtopic,"clear");
                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            if(chedophunsuong.isChecked()) {
                                chedophunsuong.setChecked(false);
                                if(phunsuong.isChecked())  pub(phunsuongstatus,"1");
                                else pub(phunsuongstatus,"0");
                            }
                            return;
                        }
                        doamkkmax.setText(doamkkmaxvalue+"");
                        pub(doamkkmaxtopic,doamkkmaxvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });

        chisouvmin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = chisouvmin.getText().toString();
                    value= value.replace(",",".");
                    if (value.isEmpty()) {
                        pub(chisouvmintopic,"clear");
                        chisouvminvalue = null;
                        if(chedomaiche.isChecked()) {
                            chedomaiche.setChecked(false);
                            if(maiche.isChecked())  pub(maichestatus,"1");
                            else pub(maichestatus,"0");
                        }
                    }
                    else{
                        //DecimalFormat df = new DecimalFormat("#.##");
                        chisouvminvalue = round(Double.parseDouble(value),2);
                        if(chisouvmaxvalue!=null && chisouvmaxvalue<=chisouvminvalue){
                            chisouvminvalue = null;
                            chisouvmin.setText("");
                            pub(chisouvmintopic,"clear");
                            if(chedomaiche.isChecked()) {
                                chedomaiche.setChecked(false);
                                if(maiche.isChecked())  pub(maichestatus,"1");
                                else pub(maichestatus,"0");
                            }
                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        chisouvmin.setText(chisouvminvalue+"");
                        pub(chisouvmintopic,chisouvminvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });
        chisouvmax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText đã mất focus, lưu giá trị
                    String value = chisouvmax.getText().toString();
                    value= value.replace(",",".");
                    if (value.isEmpty()) {
                        pub(chisouvmaxtopic,"clear");
                        chisouvmaxvalue = null;
                        if(chedomaiche.isChecked()) {
                            chedomaiche.setChecked(false);
                            if(maiche.isChecked())  pub(maichestatus,"1");
                            else pub(maichestatus,"0");
                        }
                    }
                    else{
                        //DecimalFormat df = new DecimalFormat("#.##");
                        chisouvmaxvalue = round(Double.parseDouble(value),2);
                        if(chisouvminvalue!=null && chisouvmaxvalue<=chisouvminvalue ){
                            chisouvmaxvalue = null;
                            chisouvmax.setText("");
                            pub(chisouvmaxtopic,"clear");
                            Toast.makeText(getApplicationContext(), "Thông số không hợp lệ!", Toast.LENGTH_SHORT).show();
                            if(chedomaiche.isChecked()) {
                                chedomaiche.setChecked(false);
                                if(maiche.isChecked())  pub(maichestatus,"1");
                                else pub(maichestatus,"0");
                            }
                            return;
                        }
                        chisouvmax.setText(chisouvmaxvalue+"");
                        pub(chisouvmaxtopic,chisouvmaxvalue+""); // Hàm để lưu giá trị (ví dụ: dùng Shared Preferences)
                    }
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        options.setUserName("ydlziysx:ydlziysx");
        options.setPassword("jtjD63G9r9UNSV7od5unLQWmZbxEl51b".toCharArray());
        options.setConnectionTimeout(5);
        String clientId = MqttClient.generateClientId();

        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://sparrow.rmq.cloudamqp.com:1883",
                        clientId, Ack.AUTO_ACK);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                //Log.d("mqtt", "Connection lost: ");
                //MqttClient.reconnect();
                Log.d("mqtt", "Connection lost: " + cause.getMessage());
                Toast.makeText(getApplicationContext(), "Mất kết nối!", Toast.LENGTH_LONG).show();
                reconnectHandler.postDelayed(MainActivity.this::connectmqtt, RECONNECT_DELAY_MS);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", "topic:"+ topic + " - message:" +message.toString());
                if(topic.equals(cambiennhietdo)){
                    txtnhietdo.setText(message.toString()+" °C");
                }
                if(topic.equals(cambiendoamkk)){
                    txtdoamkk.setText(message.toString()+" %");
                }
                if(topic.equals(cambiendoamdat)) {
                    txtdoamdat.setText(message.toString() + " %");
                }
                if(topic.equals(cambienuv)) {
                    txtuv.setText(message.toString() + " ");
                }
                LocalDateTime now = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    now = LocalDateTime.now();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    update.setText("Câp nhật lần cuối lúc: " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        connectmqtt();

    }
    public void connectmqtt(){
        //try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                    pub("heloc","xin chao");
                    Toast.makeText(getApplicationContext(), "Kết nối thành công", Toast.LENGTH_LONG).show();
                    //subscribe to the topic

                    sub(cambiennhietdo);
                    sub(cambiendoamkk);
                    sub(cambiendoamdat);
                    sub(cambienuv);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");
                    Toast.makeText(getApplicationContext(), "Kết nối thất bại!", Toast.LENGTH_LONG).show();

                }
            });
        //} catch (){//(MqttException e) {
            //e.printStackTrace();}
    }
    void pub(String topic,String content){
        String payload = content;
        MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());

      //  String payload = content;
      //  byte[] encodedPayload = new byte[0];
       // try {
        //    encodedPayload = payload.getBytes("UTF-8");

          //  MqttMessage message = new MqttMessage(encodedPayload);
        if (client.isConnected()) {
            client.publish(topic, message);
        }
        else{
            Toast.makeText(getApplicationContext(), "Không có kết nối!", Toast.LENGTH_LONG).show();
           // connectmqtt();
        }
      //  } catch (UnsupportedEncodingException (){//(MqttException e) {
            //e.printStackTrace();}
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    void sub(String topic){
        int qos = 1;
      //  try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("mqtt", "Subcribe to topic: "+ topic);
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
       // } catch (){//(MqttException e) {
            //e.printStackTrace() }


    }
    private void refreshContent() {

        connectmqtt();
        swipeRefreshLayout.setRefreshing(false);

    }

}
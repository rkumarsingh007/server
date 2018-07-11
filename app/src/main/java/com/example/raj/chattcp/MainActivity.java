package com.example.raj.chattcp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    public static Exception ex;
    public static EditText message;
    public static Button send;
    public static int sport=9216;
    public static Handler updateUIhandler;
    public static boolean server_status =true;
    public static ServerSocket ss=null;
    public static ArrayList<Data> messagesArray;
    public static MyAdapter myAdapter;
    public static ListView display;
    public static Socket s=null;
    public static serverinback server;
    public static Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.display);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        onButtonClick();
        messagesArray = new ArrayList<Data>();
        myAdapter= new MyAdapter(this,messagesArray);
        display.setAdapter(myAdapter);
        ex = new Exception();
        try {
            ss = new ServerSocket(sport);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
        t= new Thread(new serverinback());
        t.start();
    }
    void stop(){
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate(R.menu.menu , menu);
        return true;
    }
    public boolean onOptionsItemSelected (MenuItem item) {
        switch ( item.getItemId()) {
            case R.id.info_id: {
                Toast.makeText(getApplicationContext(), "info is clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.setting_id: {
                Intent intent = new Intent("com.example.raj.chattcp.server");
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onStop() {
        super.onStop();
        if(server_status) {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                t.interrupt();
            }
        }
    }
    public void onButtonClick(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(s==null){
                        alert("Oops","No user online to send message wait for the client to join");
                        message.setText(null);
                        return;
                    }
                    messagesArray.add(new Data(message.getText().toString(), true));
                    new ObjectOutputStream(s.getOutputStream()).writeObject(new Data(message.getText().toString(),true));
                    message.setText(null);
                }
                catch (Exception ex){
                    alert("Error" , ex.toString());
                }
                /*catch (UnknownHostException e) {
                    Toast.makeText(getApplicationContext(),"Error : "+ e.toString(),Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Error : "+ e.toString(),Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }
    public void alert (String title ,String body) {
        final AlertDialog.Builder Alert = new AlertDialog.Builder(this);
        Alert.setCancelable(true)
                .setTitle(title)
                .setMessage(body);
        Alert.setNegativeButton("Okey", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Alert.create().show();
    }

    class serverinback implements Runnable{
        @Override
        public void run() {
            while(server_status){
                try{
                    s = ss.accept();
                    Toast.makeText(getApplicationContext(),"connection request "+ s.getInetAddress().toString(),Toast.LENGTH_LONG);
                    //Communication commThread = new Communication(s);
                    new Thread(new Communication(s)).start();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Error "+ e.toString(),Toast.LENGTH_SHORT).show();
                    alert("Error", "Port busy "+ sport);
                }
            }
        }
    }
    class Communication implements Runnable{

        public Socket clientsocket ;
        public DataInputStream input;
        public Communication(Socket s){
            this.clientsocket = s;
            try {
                InputStream in = s.getInputStream();
                this.input = new DataInputStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                Data example = (Data) new ObjectInputStream( s.getInputStream()).readObject();
                String inputLine;
                inputLine = example.message;
                if(inputLine==null){
                    throw ex;
                }
                messagesArray.add(new Data(inputLine.toString(), false));
                if (inputLine.toString().equals("STOP")) {
                    server_status = false;
                }
                display.post(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                    }
                });
                Toast.makeText(getApplicationContext(),"new message "+inputLine.toString(),Toast.LENGTH_LONG);
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                alert("no message ", "Enter the message");
            }
        }
    }
}

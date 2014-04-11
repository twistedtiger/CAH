/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardsagainsthumanity;

import java.io.*;
import java.net.*;

/**
 *
 * @author thoma_000
 */
public class ComClientThread extends Thread{
    private Socket socket           =null;
    private ComClient client        =null;
    private DataInputStream input   =null;
    
    public ComClientThread(ComClient _client, Socket _socket){
        client = _client;
        socket = _socket;
        open();
        start();
    }
    
    public void open(){
        try{
            input = new DataInputStream(socket.getInputStream());
        }catch(IOException e){
            e.printStackTrace();
            client.stop();
        }
    }
    
    public void close(){
        try{
            if(input!=null) input.close();
        }catch(IOException e){
            System.out.println("Error closing the input stream" + e);
        }
    }
    
    public void run(){
        while(true){
            try{
                client.handle(input.readUTF());
            }catch(IOException e){
                client.stop();
            }
        }
    }
    
}

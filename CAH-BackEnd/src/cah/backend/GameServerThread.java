/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cah.backend;

import java.io.*;
import java.net.*;

/**
 *
 * @author thoma_000
 */
public class GameServerThread extends Thread{
    private GameServer      server  = null;
    private Socket          socket  = null;
    private int             ID      = -1;
    private String          IP      = "0.0.0.0";
    private DataInputStream input   = null;
    private DataOutputStream output = null;
    
    public GameServerThread(GameServer _server, Socket _socket){
        server = _server;
        socket = _socket;
        ID = socket.getPort();
        IP = socket.getInetAddress().toString().substring(1);
    }
    
    public void send(String msg){
        try{
            output.writeUTF(msg);
            output.flush();
        }catch(IOException e){
            server.remove(ID);
        }
    }
    
    public int getID(){return ID;}
    
    public String getIP(){return IP;}
    
    public void run(){
        System.out.println("Server Thread: "+ID+" running");
        while(true){
            try{
                server.handle(IP,ID, input.readUTF());
            }catch(IOException e){
                server.remove(ID);
                stop();
            }
        }
    }
    
    public void open() throws IOException{
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }
    
    public void close() throws IOException{
        if(socket!=null)socket.close();
        if(input!=null)input.close();
        if(output!=null)output.close();
    }
    
    
}

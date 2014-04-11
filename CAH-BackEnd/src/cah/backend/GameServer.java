/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cah.backend;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author thoma_000
 */
public class GameServer implements Runnable{
    private GameServerThread clients[] = new GameServerThread[50];
    private Socket          socket = null;
    private ServerSocket    server = null;
    private DataInputStream streamIn = null;
    private int clientCount = 0;
    private Thread thread = null;
    private GUI g = null;
    
    /*
    Make a server to host all info and deliver correctly instead of the database file
    */
    
    
    public GameServer(GUI gui,int port){
        g = gui;
        try {
            server = new ServerSocket(port);
            start();
        } catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void start(){
        if(thread==null){
            thread = new Thread((Runnable) this);
            thread.start();
        }
    }
    
    public void stop(){
        if(thread !=null){
            thread.stop();
            thread = null;
        }
    }
    
    public void open() throws IOException{
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }
    
    public void close() throws IOException{
        if(socket!=null) socket.close();
        if(streamIn !=null) streamIn.close();
    }
    
    private int findClient(int ID){
        for(int i=0;i<clientCount;i++){
            if(clients[i].getID() == ID){
                return i;
            }
        }
        return -1;
    }
    
    public void send(int ID, String msg){
        clients[findClient(ID)].send(msg);
    }
    
    public void handle(String IP, int ID, String input){
        if(input.equals("terminatedCommunication")){
            clients[findClient(ID)].send("terminatedCommunication");
            remove(ID);
        }else if(input.startsWith("CREATING_NEW_GAME=")){
            String s = input.toString().substring(18);
            //System.out.println("NEW GAME REQUEST WITH ID: "+s+"  From: "+IP);
            g.insertToConsole(ID, "NEW GAME REQUEST WITH ID: ",s,IP,0); //0 NEW GAME
        }else if(input.startsWith("JOINING_GAME=")){
            String s = input.toString().substring(13);
            //System.out.println("REQUEST TO JOIN EXISTING GAME: "+s+"  From: "+IP);
            g.insertToConsole(ID, "REQUEST TO JOIN EXISTING GAME: ",s,IP,1); //1 JOIN GAME
        }else if(input.startsWith("REMOVE_USER=")){
            String s = input.toString().substring(12);
            g.insertToConsole(ID, "REMOVING USER FROM GAME: ", s, IP, 2);
        }else if(input.startsWith("SELECTED_CARD=")){
            String s = input.toString().substring(14);
            System.out.println(s);
            String[] _s = s.split(":CardNum:");
            String n = _s[1];
            g.insertToConsole(ID, "PLAYER PLAYED CARD", n, _s[0], 3); //Returning s[0] (authcode) instead of IP to find HOST IP later to show cards after played
        }else{
            System.out.println(ID+": "+input);
            g.insertToConsole(ID,input,"",IP,-1);
            for(int i =0;i<clientCount;i++){
                clients[i].send(ID+ ": "+input);
            }
        }
    }
    
    public synchronized void remove(int ID){
        int pos = findClient(ID);
        if(pos>=0){
            GameServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if(pos <clientCount-1){
                for(int i=pos+1;i<clientCount;i++){
                clients[i-1] = clients[i];
                clientCount--;
                try{
                    toTerminate.close();
                }catch(IOException e){
                    toTerminate.stop();
                }
                }
            }
        }
    }

    private void addThread(Socket socket){
        if(clientCount < clients.length){
            clients[clientCount] = new GameServerThread(this,socket);
            try{
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            }catch(IOException e){
                System.out.println("Error creating thread: "+e);
            }
        }else{
            System.out.println("Client Refused: maximum threads" + clients.length + " reached.");
        }
    }

    @Override
    public void run() {
        System.out.println("Server started");
        while(thread!=null){
            try {
                addThread(server.accept());
            } catch (IOException ex) {
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
   
    
}

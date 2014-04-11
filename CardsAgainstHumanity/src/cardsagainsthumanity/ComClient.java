/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardsagainsthumanity;

import java.io.*;
import java.net.*;
import javax.swing.SwingWorker;

/**
 *
 * @author thoma_000
 */
public class ComClient implements Runnable{
    private Socket socket               =null;
    private Thread thread               =null;
    private DataInputStream input       =null;
    private DataOutputStream output     =null;
    private ComClientThread client      =null;
    
    public ComClient(String serverName, int serverPort) throws IOException{
        socket = new Socket(serverName, serverPort);
                start();
                System.out.println("Weird");
    }
    
    public void handle(String msg){
        if(msg.equals("terminatedCommunication")){
            System.out.println("Connection is terminated");
            stop();
        }else{
            System.out.println(msg);
        }
    }
    
    public void start() throws IOException{
        input = new DataInputStream(System.in);
        output = new DataOutputStream(socket.getOutputStream());
        if(thread==null){
            client = new ComClientThread(this,socket);
            thread = new Thread((Runnable) this);
            thread.start();
        }
    }
    
    public void stop(){
        if(thread!=null){
            thread.stop();
            thread = null;
        }
        try{
            if(input!=null) input.close();
            if(output!=null) output.close();
            if(socket!=null) socket.close();
        }catch(IOException ioe){
            client.close();
            client.stop();
        }
    }

    @Override
    public void run() {
        while(thread!=null){
            try{
                output.writeUTF(input.readLine());
                output.flush();
            }catch(IOException e){
                stop();
            }
        }
    }
    
    
}

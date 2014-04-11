/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cah.backend;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author thoma_000
 */
public class GUI extends JFrame implements ActionListener{
    SQLHandler sql = new SQLHandler();
    JPanel jp = new JPanel();
    JButton btn;
    JTextArea txt = new JTextArea("Card-Text");
    
    JTextArea consoleWindow = new JTextArea("Log: \n");
    GameServer server = null;
    
    public void init(){
        server = new GameServer(this,35353);
        this.getContentPane().add(jp);
        btn = new JButton("New Black");
        btn.addActionListener(this);
        btn.setName("db_b");
        jp.add(btn);
        
        btn = new JButton("New White");
        btn.addActionListener(this);
        btn.setName("db_w");
        jp.add(btn);

        
        txt.setColumns(60);
        txt.setName("Card-Text");
        jp.add(txt);
        
        consoleWindow.setColumns(60);
        consoleWindow.setRows(20);
        consoleWindow.setName("Console");
        jp.add(consoleWindow);

        this.setTitle("CAH - Helper");
        this.setSize(new Dimension(800,600));
        this.show();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
          if(e.toString().endsWith("db_b")){
              try {
                  //Create Black-card database
                  String cardText = txt.getText();
                  int inputs = 0;
                  for(int i=0;i<cardText.length();i++){
                      if(cardText.charAt(i) == '_' || cardText.charAt(i) == '?'){
                          inputs++;
                      }
                  }
                  System.out.println(cardText + " WITH " + inputs + " MISSING WORDS");
                  sql.createNewBlack(cardText, inputs);
                  //sql.doNothing(); //JUST FOR BUGTESTING WITHIN GUI
              } catch (ClassNotFoundException ex) {
                  Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
              } catch (SQLException ex) {
                  Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
              }
          }else if(e.toString().endsWith("db_w")){
              //Create White-card database
              String cardText = txt.getText();
              try {
                  sql.createNewWhite(cardText);
              } catch (ClassNotFoundException ex) {
                  Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
              } catch (SQLException ex) {
                  Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
              }
          }
    }
    
    public void insertToConsole(int ID,String msg, String inTo, String from, int toDo){
        String log = consoleWindow.getText();
        consoleWindow.setText(log+"\n"+msg+inTo+"  From: "+from);
        this.repaint();
        this.revalidate();
        server.send(ID, "Recieved: "+msg+inTo);
        
        switch(toDo){
            case 0: // STARTING NEW GAME!
        try {
            sql.startNewGame(from, ID, inTo);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
                break;
            case 1: // ADDING USER TO CURRENT GAME
                boolean joinStatus = false;
        try {
            joinStatus = sql.joinExistingGame(ID, from, inTo);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
                if(!joinStatus){
                    server.send(ID, "Could not find game, or the game is full (Max 6 players)");
                }
                break;
            case 2: try {
                //REMOVING USER FROM GAME
                sql.removeCreatedGame(ID, from);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
                break;
            case 3:
        try {
            int host = sql.updatePlayerCards(ID,inTo);
            if(host!=-1){
                server.send(ID, "TEST MESSAGE FOR ALL PLAYERS HAVE PLAYED A CARD!");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
                break;
        }
    }
}

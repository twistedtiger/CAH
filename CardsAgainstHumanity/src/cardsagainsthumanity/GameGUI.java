/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardsagainsthumanity;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author thoma_000
 */
public class GameGUI extends JFrame implements WindowListener{
    SQLHandler sql = new SQLHandler();
    Info info = new Info();
    JPanel jp = new JPanel(); //Main back panel
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    
    JPanel wcPanel;
    JPanel bcPanel;
    JPanel showCardsPanel;
    
    
    
    /*
    Game owner creates new game
    Game owner sends IP address to SQL base, and recieves a connection code back
    
    Friend inserts connection code into game window, get's owners IP (hidden), and connects to his/her game
    Game owner will recieve all cards, but only ID of white ones. Will automaticly convert black card IDs to plain text, and send to friends -
    with the ID of their new white card (since noone should have the same white card at the same time)
    Game owner will remember which cards are used, and only reuse them after at least 5 turns
    
    This makes the game owner able to "cheat" but not a huge problem in this game
    */
    
    
    public void init(){
        this.addWindowListener(this);
        menuBar = new JMenuBar();
        menu = new JMenu("New Game");
        menu.getAccessibleContext().setAccessibleDescription("Set up a new game, or join an exisiting one");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Create New Game");
        menuItem.getAccessibleContext().setAccessibleDescription("Create new game and invite your foes");
        menuItem.setName("new_game");
        menuItem.addMouseListener(menuListener);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Join Exisiting Game");
        menuItem.getAccessibleContext().setAccessibleDescription("Insert code from friends to join their game");
        menuItem.setName("join_game");
        menuItem.addMouseListener(menuListener);
        menu.add(menuItem);
        
        this.setJMenuBar(menuBar);
        
        
        
        
        
        
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(new Dimension(800,600));
        this.show();
    }
    
    public void endingApp(){
        
    }
    
    public void startNewGame(){
    /*
    OptionPane to display when creating a new game
    */
    Object[] opOptions = {"Start game", "Cancel"};
    //Send IP to server - recieve access code to game
    int n = JOptionPane.showOptionDialog(this, "Your game is created with signature: "+info.authCode, "Title", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opOptions, rootPane);
    switch(n){
        case 0:
    try {
        sql.startNewGame(info.hostIP, info.authCode);
    } catch (ClassNotFoundException ex) {
        Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
        Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
    }
            break;
        case 1:
            //Cancel
            break;
        }
    }
    
    public void joinExistingGame(){
        Object[] pos = null;
        String s = (String)JOptionPane.showInputDialog(null);
        try {
            //Look up authCode (string s) in DB and get IP address, connect to that IP
            String ip = sql.joinGame(s);
        } catch (SQLException ex) {
            Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getNewBlackCard(){
        
    }
    
    public void getNewWhiteCard(){
        
    }
    
    
    public MouseListener menuListener = new MouseListener(){

        @Override
        public void mouseClicked(MouseEvent e) {
            
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(e.toString().endsWith("new_game")){
                //Request code from server to give away to other players
                String hostIP = sql.getRemote();
                String auth = null;
                try {
                    auth = sql.generateAuthCode(System.currentTimeMillis() + hostIP);
                    System.out.println("AUTH: " + auth);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(auth!=null){
                //info.setAuth(auth);
                info.authCode = auth;
                info.setIP(hostIP);
                startNewGame();
                }
            }else if(e.toString().endsWith("join_game")){
                //Get a window to insert code from another player
                joinExistingGame();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
            
        }
        
    };

    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            //System.out.println("Cleaning up game from: " + info.hostIP);
            if(info.hostIP!=null){
            sql.removeCreatedGame(info.hostIP);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        
    }

    @Override
    public void windowActivated(WindowEvent e) {
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }
    
}

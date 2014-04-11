/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardsagainsthumanity;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

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
    JPanel ownCards;
    ComClient client = null;
    JTextArea _card0;
    JTextArea _card1;
    JTextArea _card2;
    JTextArea _card3;
    JTextArea _card4;
    
    public void init(){
        /*
        Setup
        */
        this.addWindowListener(this);
        addMenuBar();
        setupPanels();
        connectToServer();
        
        /*
        Game visuals
        */
        addOwnCards();
        
        updateCard(_card0, "[09.04.2014 17:08:24] Morten Strøm: My mom freaked out when she looked at my browser history and found _.com/_. PICK 2");
        
        //End with this to revalidate
        setupFrame();
    }
    
    public void updateVisual(){
        ownCards.revalidate();
        ownCards.repaint();
        
        wcPanel.revalidate();
        wcPanel.repaint();
        
        bcPanel.revalidate();
        bcPanel.repaint();
        
        this.revalidate();
        this.repaint();
    }
    
    public void setupFrame(){
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(new Dimension(800,600));
        this.show();
    }
    
    public void setupPanels(){
        jp.setLayout(new GridLayout(3,1));
        this.getContentPane().add(jp);
        wcPanel = new JPanel();
        bcPanel = new JPanel();
        ownCards = new JPanel();
        ownCards.setLayout(new FlowLayout());
        
        jp.add(wcPanel);
        jp.add(bcPanel);
        jp.add(ownCards);
    }
    
    public void connectToServer(){
        try {
            client = new ComClient("80.202.62.205",35353);
        } catch (IOException ex) {
            Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addMenuBar(){
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
    }
    
    public void addOwnCards(){
        _card0 = whiteCard("Card 0",1);
        _card0.setName("_card0");
        ownCards.add(_card0);
        
        _card1 = whiteCard("Card 1",2);
        _card1.setName("_card1");
        ownCards.add(_card1);
        
        _card2 = whiteCard("Card 2",3);
        _card2.setName("_card2");
        ownCards.add(_card2);
        
        _card3 = whiteCard("Card 3",4);
        _card3.setName("_card3");
        ownCards.add(_card3);
        
        _card4 = whiteCard("Card 4",5);
        _card4.setName("_card4");
        ownCards.add(_card4);
    }
    
    public JTextArea whiteCard(String text, int cardID){
        JTextArea txt = new JTextArea(text);
        txt.setToolTipText(Integer.toString(cardID));
        txt.setLineWrap(true);
        txt.setColumns(10);
        txt.setRows(12);
        txt.setEnabled(false);
        txt.setDisabledTextColor(Color.BLACK);
        txt.addMouseListener(cardSelectionListener);
        return txt;
    }
    
    public MouseListener cardSelectionListener = new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
            Component c = e.getComponent();
            if(c.getName().startsWith("_card")){
                JTextArea txt = (JTextArea) c;
                if(!txt.getText().contains("**Selected**")){
                txt.setDisabledTextColor(Color.GRAY);
                String getText = txt.getText();
                txt.setText("**Selected**\n\n"+getText);
                client.sendCardUpdate("SELECTED_CARD=", info.authCode, txt.getToolTipText());
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            
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
    
    public void updateCard(JTextArea card, String text){
        card.setText(text);
        updateVisual();
    }
    
    public void startNewGame(){
    Object[] opOptions = {"Start game", "Cancel"};
    int n = JOptionPane.showOptionDialog(this, "Your game is created with signature: "+info.authCode, "Title", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opOptions, rootPane);
    switch(n){
        case 0:
        client.sendGameRequest("CREATING_NEW_GAME=", info.authCode);
            break;
        case 1:
            //Cancel
            break;
        }
    }
    
    public void joinExistingGame(){
        String s = (String)JOptionPane.showInputDialog(null);
        if(s!=null){
        info.authCode = s;
        client.sendGameRequest("JOINING_GAME=", s);
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
            if(info.authCode!=null){
                client.sendGameRequest("REMOVE_USER=", info.authCode);
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

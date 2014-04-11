/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cah.backend;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author thoma_000
 */
public class SQLHandler {
    
    public int updatePlayerCards(int eID, String eText) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        String dbName = "C:\\CAH\\CAH-Black.db";
        String Jdbc = "jdbc:sqlite";
        String DbUrl = Jdbc + ":" + dbName;
        
        Connection con = DriverManager.getConnection(DbUrl);
        Statement st = con.createStatement();
        
        String findID = "SELECT * FROM players";
        ResultSet rs = st.executeQuery(findID);
        
        String oauth = null;
        String col = null;
        int rowNum=-1;
        while(rs.next()){
            if(rs.getString("id").equals(Integer.toString(eID))){
                rowNum = rs.getRow();
                oauth = rs.getString("authcode");
                if(rs.getString("c_1").equals(eText)){
                    col = "c_1";
                }else if(rs.getString("c_2").equals(eText)){
                    col = "c_2";
                }else if(rs.getString("c_3").equals(eText)){
                    col = "c_3";
                }else if(rs.getString("c_4").equals(eText)){
                    col = "c_4";
                }else if(rs.getString("c_5").equals(eText)){
                    col = "c_5";
                }
            }
        }
        
        String updatePCards = "UPDATE players SET '"+col+"'=-"+eText+" WHERE rowid="+rowNum;
        st.executeUpdate(updatePCards);
        
        //UPDATE players SET 'c_1'=-6 WHERE rowid=1
        
        rs.close();
        st.close();
        con.close();
        if(!oauth.equals(null)){
        return checkGameStatus(oauth);
        }
        return -1;
    }
    
    public int checkGameStatus(String oauth) throws ClassNotFoundException, SQLException{
        //Check if game is done
        Class.forName("org.sqlite.JDBC");
        String dbName = "C:\\CAH\\CAH-Black.db";
        String Jdbc = "jdbc:sqlite";
        String DbUrl = Jdbc + ":" + dbName;
        
        Connection con = DriverManager.getConnection(DbUrl);
        Statement st = con.createStatement();
        int hostID = -1;
        String findGame = "SELECT * FROM games";
        ResultSet gameSet = st.executeQuery(findGame);
        int currentPlayers = 0;
        while(gameSet.next()){
            if(gameSet.getString("authcode").equals(oauth)){
                if(!gameSet.getString("p_1").contains("-")){
                    currentPlayers++;
                    hostID = Integer.parseInt(gameSet.getString("p_1"));
                }
                if(!gameSet.getString("p_2").contains("-")){
                    currentPlayers++;
                }
                if(!gameSet.getString("p_3").contains("-")){
                    currentPlayers++;
                }
                if(!gameSet.getString("p_4").contains("-")){
                    currentPlayers++;
                }
                if(!gameSet.getString("p_5").contains("-")){
                    currentPlayers++;
                }
                if(!gameSet.getString("p_6").contains("-")){
                    currentPlayers++;
                }
            }
        }
        
        
        String findPlayers = "SELECT * FROM players";
        ResultSet rs = st.executeQuery(findPlayers);
        
        int cardsPlayed = 0;
        while(rs.next()){
            if(rs.getString("authcode").equals(oauth)){
                if(rs.getString("c_1").startsWith("-")){
                    cardsPlayed++;
                }
                if(rs.getString("c_2").startsWith("-")){
                    cardsPlayed++;
                }
                if(rs.getString("c_3").startsWith("-")){
                    cardsPlayed++;
                }
                if(rs.getString("c_4").startsWith("-")){
                    cardsPlayed++;
                }
                if(rs.getString("c_5").startsWith("-")){
                    cardsPlayed++;
                }
            }
        }
        if(cardsPlayed==currentPlayers){
            System.out.println("ALL PLAYERS HAVE PLAYED A CARD!");
                    return hostID;
                    //SHOW CARDS PLAYED
                    //GIVE PLAYERS NEW CARDS
        }
        return -1;
    }
    
    public void createNewBlack(String cardText, int cardInputs) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        String dbName = "C:\\CAH\\CAH-Black.db";
        String Jdbc = "jdbc:sqlite";
        String DbUrl = Jdbc + ":" + dbName;
                
        
        int timeout = 30;
        String makeTable = "CREATE TABLE IF NOT EXISTS black (id int NOT NULL, card text, inputs int)";
        String makeSelect = "SELECT card from black";
        String makeGetId = "SELECT id from black";
        
        
        Connection con = DriverManager.getConnection(DbUrl);
                
        Statement st = con.createStatement();        
        st.setQueryTimeout(timeout);
        st.executeUpdate(makeTable);
        
        
        ResultSet getId = st.executeQuery(makeGetId);
        int last = 0;
        while(getId.next()){
            String res = getId.getString("id");
            last = Integer.parseInt(res);
        }
        getId.close();
        last++;
        
        String makeInsert = "INSERT INTO black VALUES("+last+",'"+cardText+"',"+cardInputs+")";
        st.executeUpdate(makeInsert);
        ResultSet rs = st.executeQuery(makeSelect);
        
        while(rs.next()){
            String sResult = rs.getString("card");
            //System.out.println(sResult);
        }
        rs.close();
       
        st.close();
        con.close();
    }
    
   
    public void createNewWhite(String cardText) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        String dbName = "C:\\CAH\\CAH-Black.db";
        String Jdbc = "jdbc:sqlite";
        String DbUrl = Jdbc + ":" + dbName;
                
        
        int timeout = 30;
        String makeTable = "CREATE TABLE IF NOT EXISTS white (id int NOT NULL, card text)";
        String makeSelect = "SELECT card from white";
        String makeGetId = "SELECT id from white";
        
        
        Connection con = DriverManager.getConnection(DbUrl);
        
        Statement st = con.createStatement();        
        st.setQueryTimeout(timeout);
        st.executeUpdate(makeTable);
        
        
        ResultSet getId = st.executeQuery(makeGetId);
        int last = 0;
        while(getId.next()){
            String res = getId.getString("id");
            last = Integer.parseInt(res);
        }
        getId.close();
        last++;
        
        String makeInsert = "INSERT INTO white VALUES("+last+",'"+cardText+"')";
        st.executeUpdate(makeInsert);
        ResultSet rs = st.executeQuery(makeSelect);
        
        while(rs.next()){
            String sResult = rs.getString("card");
            //System.out.println(sResult);
        }
        rs.close();
       
        st.close();
        con.close();
    }
        
        
    public void startNewGame(String hostIp, int ID, String authCode) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        String dbName = "C:\\CAH\\CAH-Black.db";
        String Jdbc = "jdbc:sqlite";
        String DbUrl = Jdbc + ":" + dbName;
        int timeout = 120;
        String makeTable = "CREATE TABLE IF NOT EXISTS players (id int NOT NULL, ip text, authcode text, c_1 int NOT NULL, c_2 int NOT NULL, c_3 int NOT NULL, c_4 int NOT NULL, c_5 int NOT NULL)";
        String makeSelect = "SELECT authcode from players";
        
        
        Connection con = DriverManager.getConnection(DbUrl);
        Statement st = con.createStatement();
        st.setQueryTimeout(timeout);
        st.executeUpdate(makeTable);

        String makeInsert = "INSERT INTO players VALUES("+ID+",'"+hostIp+"','"+authCode+"', 1,2,3,4,5)";
        st.executeUpdate(makeInsert);
        ResultSet rs = st.executeQuery(makeSelect);
        
        while(rs.next()){
            String sResult = rs.getString("authCode");
            if(sResult.equals(authCode)){
                //Respond to client to he enters the game
                System.out.println("Game created with ID: "+authCode);
            }
        }
        rs.close();
        
        String makeTableGames = "CREATE TABLE IF NOT EXISTS games (id int NOT NULL, ip text, authcode text, p_1 int NOT NULL, p_2 int NOT NULL, p_3 int NOT NULL, p_4 int NOT NULL, p_5 int NOT NULL, p_6 int NULL)";
        String getGameId = "SELECT id FROM games";
        
        st.executeUpdate(makeTableGames);
        
        ResultSet gameId = st.executeQuery(getGameId);
        int l = 0;
        while(gameId.next()){
            String res = gameId.getString("id");
            //String resID = getId.getString("authcode");
            l = Integer.parseInt(res);
        }
        gameId.close();
        l++;
        
        
        String makeInsertGame = "INSERT INTO games VALUES("+l+",'"+hostIp+"','"+authCode+"',"+ID+", -1, -1, -1, -1, -1)";
        st.executeUpdate(makeInsertGame);
        
       
        st.close();
        con.close();
        
        
        
    }
         
        
    public boolean joinExistingGame(int eID, String eIP, String eAuth) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        String dbName = "C:\\CAH\\CAH-Black.db";
        String Jdbc = "jdbc:sqlite";
        String DbUrl = Jdbc + ":" + dbName;
        
        Connection con = DriverManager.getConnection(DbUrl);
        Statement st = con.createStatement();
        
        String col = null;
        int row = -1;
        String findGameByAuth = "SELECT * FROM games";
        ResultSet rs = st.executeQuery(findGameByAuth);
        while(rs.next()){
            if(rs.getString("authcode").equals(eAuth)){
                row = rs.getRow();
                if(rs.getString("p_2").equals("-1")){
                    //REPLACE -1 with your ID
                    col = "p_2";
                }else if(rs.getString("p_3").equals("-1")){
                    col = "p_3";
                }else if(rs.getString("p_4").equals("-1")){
                    col = "p_4";
                }else if(rs.getString("p_5").equals("-1")){
                    col = "p_5";
                }else{
                    st.close();
                    con.close();
                    return false;
                }
            }
        }
        rs.close();
        if(col!=null){
            String updatePlayersInGame = "UPDATE games SET '"+col+"'="+eID+" WHERE rowid="+row;
            st.executeUpdate(updatePlayersInGame);
            String makeInsert = "INSERT INTO players VALUES("+eID+",'"+eIP+"','"+eAuth+"', 1,2,3,4,5)";
            st.executeUpdate(makeInsert);
            st.close();
            con.close();
            return true;
        }
        
        st.close();
        con.close();
        return false;
    }
         
         
    public void removeCreatedGame(int ID, String hostip) throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
        String dbName = "C:\\CAH\\CAH-Black.db";
        String Jdbc = "jdbc:sqlite";
        String DbUrl = Jdbc + ":" + dbName;
        
        Connection con = DriverManager.getConnection(DbUrl);
        Statement st = con.createStatement();
                
        String remLine = "DELETE FROM players WHERE id ="+ID;
        int del = st.executeUpdate(remLine);
        if(del>=1){
            System.out.println("Player Deleted");
        }else{
            System.out.println("Problems with removing the players");
        }
        
        String unHookFromGame = "SELECT * FROM games";
        ResultSet rs = st.executeQuery(unHookFromGame);
        String newHost = "-1";
        int gameRow = -1;
        String gameCol = null;
        String _auth = null;
        String remPlayer = null;
        int remProw = -1;
        while(rs.next()){
            if(rs.getString("p_2").equals(Integer.toString(ID))){
                remPlayer = "p_2";
                remProw = rs.getRow();
            }
            if(rs.getString("p_3").equals(Integer.toString(ID))){
                remPlayer = "p_3";
                remProw = rs.getRow();
            }
            if(rs.getString("p_4").equals(Integer.toString(ID))){
                remPlayer = "p_4";
                remProw = rs.getRow();
            }
            if(rs.getString("p_5").equals(Integer.toString(ID))){
                remPlayer = "p_5";
                remProw = rs.getRow();
            }
            if(rs.getString("p_6").equals(Integer.toString(ID))){
                remPlayer = "p_6";
                remProw = rs.getRow();
            }
            if(rs.getString("p_1").equals(Integer.toString(ID))){
                gameRow = rs.getRow();
                _auth = rs.getString("authCode");
                //LOCATE NEW HOST IP from another player ID
                if(!rs.getString("p_2").equals("-1")){ //IF -1, move on!
                    //Set this person as new Host. Locate IP from player table and set as p_1
                    newHost = rs.getString("p_2");
                    gameCol = "p_2";
                }else if(!rs.getString("p_3").equals("-1")){
                    //Set this person as new Host. Locate IP from player table and set as p_1
                    newHost = rs.getString("p_3");
                    gameCol = "p_3";
                }else if(!rs.getString("p_4").equals("-1")){
                    //Set this person as new Host. Locate IP from player table and set as p_1
                    newHost = rs.getString("p_4");
                    gameCol = "p_4";
                }else if(!rs.getString("p_5").equals("-1")){
                    //Set this person as new Host. Locate IP from player table and set as p_1
                    newHost = rs.getString("p_5");
                    gameCol = "p_5";
                }else if(!rs.getString("p_6").equals("-1")){
                    //Set this person as new Host. Locate IP from player table and set as p_1
                    newHost = rs.getString("p_6");
                    gameCol = "p_6";
                }else{
                    //REMOVE THE ENTIRE GAME
                    System.out.println("NO MORE PLAYERS IN THIS GAME, DELETING: "+_auth);
                    String remGameLine = "DELETE FROM games WHERE authcode='"+_auth+"'";
                    int gameDel = st.executeUpdate(remGameLine);
                    if(gameDel==1){
                        System.out.println("Game Deleted Successfully!");
                    }else{
                        System.out.printf("Something went wrong with deleting games. %d has been deleted\n",gameDel);
                    }
                }
            }
        }
        rs.close();
        if(remPlayer!=null){
            System.out.println("TRYING TO REMOVE PLAYER FROM GAME");
            String updatePlayerLeft = "UPDATE games SET '"+remPlayer+"'=-1 WHERE rowid="+remProw;
            st.executeUpdate(updatePlayerLeft);
        }
        
        /*
        Time to locate new host, if any
        */
        if(!newHost.equals("-1")){
            System.out.println("SETTING UP NEW HOST: "+newHost);
            String newIP = null;
            String findNewIP = "SELECT * FROM players";
            ResultSet resNew = st.executeQuery(findNewIP);
            while(resNew.next()){
                System.out.println(resNew.getStatement());
                if(resNew.getString("id").equals(newHost)){
                    System.out.println(resNew.getString("id")+"  :  "+resNew.getString("ip"));
                    newIP = resNew.getString("ip");
                }
            }
            resNew.close();
            //UPDATE players SET 'c_1'=-6 WHERE rowid=1
            if(newIP!=null){
                System.out.println("TRYING TO TRANSFER GAME HOST");
               String updateGameHost = "UPDATE games SET 'p_1'="+newHost+" WHERE rowid="+gameRow;
               st.executeUpdate(updateGameHost);
               String removeOldPos = "UPDATE games SET '"+gameCol+"'=-1 WHERE rowid="+gameRow;
               st.executeUpdate(removeOldPos);
               String setNewIP = "UPDATE games SET 'ip'='"+newIP+"' WHERE rowid="+gameRow;
               st.executeUpdate(setNewIP);
               System.out.println("New Host has been assinged to game: "+_auth);
            }
            
        }
        
        st.close();
        con.close();
    }         
         
         
         
}

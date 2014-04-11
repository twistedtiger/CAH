/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardsagainsthumanity;

/**
 *
 * @author thoma_000
 */
public class Info {
    
    public static String dbIP = "80.202.62.205";
    public static int port = 35353;
    
    public String authCode;
    public String hostIP;
    
    public void setAuth(String auth){
        authCode = auth;
    }
    
    public void setIP(String ip){
        hostIP = ip;
    }
    
}

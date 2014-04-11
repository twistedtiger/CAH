/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cardsagainsthumanity;

import java.io.IOException;

/**
 *
 * @author thoma_000
 */
public class CardsAgainstHumans {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        ComClient client = new ComClient("80.202.62.205",35353);
        
        //GameGUI game = new GameGUI();
        //game.init();
    }
    
}

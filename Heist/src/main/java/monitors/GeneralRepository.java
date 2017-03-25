/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import States.MasterThiefStates;
import main.Constants;
/**
 *
 * @author Ricardo Filipe
 */
public class GeneralRepository {
    
    private int collectedCanvas;
    private int masterThiefState;
    private int[] thiefPositions;
    private int[] thiefStates; 
    
    public GeneralRepository(){
        collectedCanvas = 0;
        masterThiefState = MasterThiefStates.PLANNING_THE_HEIST;
        thiefPositions = new int[Constants.N_ORD_THIEVES];
        thiefStates = new int[Constants.N_ORD_THIEVES];
    }
    

}

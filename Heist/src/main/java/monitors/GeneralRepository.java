/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import States.MasterThiefStates;
import States.OrdinaryThiefState;
import com.sun.javafx.binding.Logging;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Constants;

/**
 * General Repository Instance.
 * @author Tiago Henriques nmec: 73046; Miguel Oliveira nmec: 72638
 */
public class GeneralRepository {
    
    private static PrintWriter pw;
    private final File log;
    private int masterThiefState;
    private int[] thiefPositions;
    private int[] thiefStates;
    private int[] thiefSpeed;
    private String[] thiefSituation;
    private int[] roomId;
    private Room[] rooms;
    private int[] memberId;
    private int[][] collectedCanvas;
    private HashMap<Integer,Thief> thiefMap;
    private int[][] partyElement;
    private int[][] pos;
    private int canvasCollected= 0;
    
    /**
     * Creates a new General Repository
     */
    public GeneralRepository(){
        masterThiefState = MasterThiefStates.PLANNING_THE_HEIST;
        thiefPositions = new int[Constants.N_ORD_THIEVES];
        thiefSpeed = new int[Constants.N_ORD_THIEVES];
        thiefStates = new int[Constants.N_ORD_THIEVES];
        thiefSituation = new String[Constants.N_ORD_THIEVES];
        roomId = new int[Constants.N_ASSAULT_PARTIES];
        rooms = new Room[Constants.N_ROOMS];
        memberId = new int[Constants.N_ASSAULT_PARTIES];
        collectedCanvas = new int[Constants.N_ASSAULT_PARTIES][Constants.ASSAULT_PARTY_SIZE];
        pos = new int[Constants.N_ASSAULT_PARTIES][Constants.ASSAULT_PARTY_SIZE];
        thiefMap = new HashMap<>();
        partyElement = new int[Constants.N_ASSAULT_PARTIES][Constants.ASSAULT_PARTY_SIZE];
        
        for(int i = 0; i < thiefStates.length;i++)
            thiefStates[i] = OrdinaryThiefState.OUTSIDE;
        
        for(int i = 0; i < thiefPositions.length; i++){
            thiefPositions[i] = 0;
        }
        
        for(int i = 0; i < thiefSituation.length; i++){
            thiefSituation[i] = "W";
        }
        
        for(int i = 0; i < roomId.length; i++){
            roomId[i] = 0;
        }
         
        for(int i = 0; i < memberId.length; i++){
            memberId[i] = 0;
        }
        
        for(int i = 0; i < collectedCanvas.length; i++){
            for(int j = 0; j <  Constants.ASSAULT_PARTY_SIZE;j++)
                collectedCanvas[i][j] = 0;
        }
        for(int i = 0; i < Constants.N_ASSAULT_PARTIES; i++){
            for(int j = 0; j <  Constants.ASSAULT_PARTY_SIZE;j++)
                pos[i][j] = 0;
        }
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            for (int j = 0; j < Constants.ASSAULT_PARTY_SIZE; j++) {
               partyElement[i][j] = -1; 
            }
            
        }

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmmss");
        String filename = "HeistMuseum" + date.format(today) + ".log";

        this.log = new File(filename);
        writeInit();

    }
    
    public synchronized void FirstLine(){
        Thief currentT;
        pw.printf("\n%4d",masterThiefState);
        for(int i=0; i< Constants.N_ORD_THIEVES; i++){
            currentT = thiefMap.get(i);
            pw.printf("    %4d %1c %2d", currentT.state,currentT.situation ,currentT.speed);
        }
    }
    
    public synchronized void SecondLine(){
        Thief currentT;
        pw.printf("   ");
        for(int i =0; i < Constants.N_ASSAULT_PARTIES; i++){
            pw.printf(" %3d ", roomId[i]+1);
            for(int j = 0; j < Constants.ASSAULT_PARTY_SIZE; j++){
                if(partyElement[i][j] == -1)
                {
                    pw.printf("           ");
                }
                else{
                    currentT = thiefMap.get(partyElement[i][j]);
                    pw.printf(" %2d %3d %2d ", currentT.id+1, currentT.position, currentT.canvas);
                }
            }
        }
        pw.printf(" ");
        for (Room room : rooms) {
            pw.printf("    %2d %2d", room.paitings_left, room.distance);
        }
        pw.println();
    }
    

    public synchronized void updateThiefState(int thiefId, int state ){
        Thief t = thiefMap.get(thiefId);
        t.state = state;
        printStatus();
        
    }
    
    public synchronized void updateMThiefState(int state ){
        masterThiefState = state;
        printStatus();
        
    }
    /*
     public synchronized void setThiefSpeed(int thiefId, int speed ){
        thiefSpeed[thiefId] = speed;
        FirstLine();
        
    }*/
    
    public synchronized void updateThiefSituation(int thiefId, char situation ){
        Thief t = thiefMap.get(thiefId);
        t.situation = situation;
        printStatus();
    }
    
    public synchronized void setRoomIdAP(int partyId,int room){
        roomId[partyId] = room;
        printStatus();
    }
    
    public synchronized void setCollectedCanvas(int toalCanvas){
        canvasCollected = toalCanvas;
    }
    
    public synchronized void setRoomAtributes(int roomId, int distance, int paitings) {
        rooms[roomId] = new Room(paitings, distance);
    }
    
    public synchronized void addThief(int thiefId, int speed){
        thiefMap.put(thiefId, new Thief(thiefId, speed));
    }
    
    public synchronized void setPartyElement(int partyId, int thiefId, int elemId){
        partyElement[partyId][elemId] = thiefId;
    }
    
    public synchronized void clearParty(int partyId){
        for (int i = 0; i < Constants.ASSAULT_PARTY_SIZE; i++) {
            partyElement[partyId][i] = -1;
        }
        printStatus();
    }
    
    public synchronized void updateThiefPosition(int thiefId, int position){
        Thief currentT = thiefMap.get(thiefId);
        currentT.position = position;
        printStatus();
    }
    
    public synchronized void updateThiefCylinder(int thiefId, boolean hasCanvas){
        Thief currentT = thiefMap.get(thiefId);
        currentT.canvas = hasCanvas ? 1:0;
        printStatus();
    }
    
    private synchronized void printStatus(){
        if(Constants.DEBUG)
            return;
        FirstLine();
        pw.println();
        SecondLine();
    }
    
    private synchronized void writeInit(){
        if(Constants.DEBUG)
            return;
        try{
            pw = new PrintWriter(log);
            pw.println("                               Heist to the Museum - Description of the internal state ");
            pw.println();
            
            String head = "MstT   ";
            for(int i=1; i<=6; i++){
                head += "Thief " + Integer.toString(i);
                head += "      ";
            }
            pw.println(head);
            
            head = "Stat    ";
            for(int i=1; i<=Constants.N_ORD_THIEVES; i++){
                head += "Stat S MD";
                head += "    ";
            }
            pw.println(head);
            
            head="";
            for(int i=1; i<=Constants.N_ASSAULT_PARTIES; i++){
                head += "                    Assault party " + Integer.toString(i);
                head += "   ";
            }
            head+= "                        Museum";
            pw.println(head);
            
            
            head= "           "; 
            for(int i=1; i<= Constants.ASSAULT_PARTY_SIZE; i++){
                head += "Elem " +Integer.toString(i);
                head += "     ";
            }
            
            head += "     ";
            for(int i=1; i<= Constants.ASSAULT_PARTY_SIZE; i++){
                head += "Elem " +Integer.toString(i);
                head += "     ";
            }
            
            head +="  ";
            for(int i=1; i<= Constants.N_ROOMS; i++){
                head += "Room " +Integer.toString(i);
                head += "   ";
            }
            
            pw.println(head);

            head="  ";
            for(int i=1; i<= Constants.N_ASSAULT_PARTIES; i++){
                head += "  RId";
                for(int j=1; j<=Constants.ASSAULT_PARTY_SIZE; j++){     
                    head += "  Id Pos Cv";
                }
            }
            head+="  ";
            for(int i=1; i<=Constants.N_ROOMS; i++){
                head += "    NP DT";
            }
            pw.println(head);
                   
                        
            pw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public synchronized void writeEnd(){
        if(Constants.DEBUG)
            return;
        pw.printf("My friends, tonight's effort produced %2d priceless paintings!", canvasCollected);
        pw.println("\nLegend:");
        pw.println("MstT Sta – state of the master thief");
        pw.println("Thief # Stat - state of the ordinary thief # (# - 1 .. 6)");
        pw.println("Thief # S – situation of the ordinary thief  # (# - 1 .. 6) either 'W' (waiting to join a party) or 'P' (in party) ");
        pw.println("Thief # MD – maximum displacement of the ordinary thief # (# - 1 .. 6) a random number between 2 and 6 ");
        pw.println("Assault party # RId – assault party # (# - 1,2) elem # (# - 1 .. 3) room identification (1 .. 5)");
        pw.println("Assault party # Elem # Id – assault party # (# - 1,2) elem # (# - 1 .. 3) member identification (1 .. 6) ");
        pw.println("Assault party # Elem # Pos – assault party # (# - 1,2) elem # (# - 1 .. 3) present position (0 .. DT RId) ");
        pw.println("Assault party # Elem # Cv – assault party # (# - 1,2) elem # (# - 1 .. 3) carrying a canvas (0,1)");
        pw.println("Museum Room # NP - room identification (1 .. 5) number of paintings presently hanging on the walls");
        pw.println("Museum Room # DT - room identification (1 .. 5) distance from outside gathering site, a random number between 15 and 30");
        pw.flush();
        pw.close();
    }

    public synchronized void setRoomCanvas(int id, int paitings) {
        rooms[id].paitings_left = paitings;
    }
    
    private class Room{
        private Room(int paitings, int distance){
            this.distance = distance;
            this.paitings_left = paitings;
            }
        private int paitings_left, distance;
    }
    
    private class Thief{
        private int id, position, canvas, state, speed;
        private char situation;
        
        private Thief(int id, int speed){
            this.id = id;
            this.position = 0;
            this.speed = speed;
            this.situation = 'W';
            this.canvas = 0;
            this.state = OrdinaryThiefState.OUTSIDE;
        }
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

import States.MasterThiefStates;
import States.OrdinaryThiefState;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
;
import java.util.HashMap;

/**
 * General Repository Instance.
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */


public class GeneralRepository implements ImtGeneralRepository, IotGeneralRepository, ImonitorsGeneralRepository {

    private static PrintWriter pw;
    private final File log;
    String filename;
    private int masterThiefState;
    private int[] roomId;
    private Room[] rooms;
    private HashMap<Integer, Thief> thiefMap;
    private int[][] partyElement;
    private int canvasCollected = 0;
    private int N_ASSAULT_PARTIES;
    private int N_ROOMS;
    private int ASSAULT_PARTY_SIZE;
    private int N_ORD_THIEVES;
    private boolean DEBUG = false;

    /**
     * Creates a new General Repository
     */
    public GeneralRepository(String logname, int nrooms, int assault_party_size, int n_ord_thieves) {
        N_ASSAULT_PARTIES = n_ord_thieves/assault_party_size;
        N_ROOMS = nrooms;
        ASSAULT_PARTY_SIZE = assault_party_size;
        N_ORD_THIEVES = n_ord_thieves;
        masterThiefState = MasterThiefStates.PLANNING_THE_HEIST;
        roomId = new int[N_ASSAULT_PARTIES];
        rooms = new Room[N_ROOMS];
        thiefMap = new HashMap<>();
        partyElement = new int[N_ASSAULT_PARTIES][ASSAULT_PARTY_SIZE];

        for (int i = 0; i < roomId.length; i++) {
            roomId[i] = -1;
        }

        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            for (int j = 0; j < ASSAULT_PARTY_SIZE; j++) {
                partyElement[i][j] = -1;
            }

        }
        int ts = (int) System.currentTimeMillis();
        filename = logname;

        this.log = new File(filename);
        InitializeLog();

    }

    private synchronized void FirstLine() {
        Thief currentT;
        pw.printf("\n%4d", masterThiefState);
        for (int i = 0; i < N_ORD_THIEVES; i++) {
            currentT = thiefMap.get(i);
            pw.printf("    %4d %1c %2d", currentT.state, currentT.situation, currentT.speed);
        }
    }

    private synchronized void SecondLine() {
        Thief currentT;
        pw.printf("   ");
        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            if (roomId[i] == -1) {
                pw.printf("     ");
            } else {
                pw.printf(" %3d ", roomId[i] + 1);
            }
            for (int j = 0; j < ASSAULT_PARTY_SIZE; j++) {
                if (partyElement[i][j] == -1) {
                    pw.printf("           ");
                } else {
                    currentT = thiefMap.get(partyElement[i][j]);
                    pw.printf(" %2d %3d %2d ", currentT.id + 1, currentT.position, currentT.canvas);
                }
            }
        }
        pw.printf(" ");
        for (Room room : rooms) {
            pw.printf("    %2d %2d", room.paitings_left, room.distance);
        }
        pw.println();
    }

    /**
     * Update the State of the Ordinary Thief and print the updated Status in
     * the log file.
     *
     * @param thiefId Id of the Ordinary Thief
     * @param state updated State
     */
    @Override
    public synchronized void updateThiefState(int thiefId, int state) {
        Thief t = thiefMap.get(thiefId);
        t.state = state;
        printStatus();

    }

    /**
     * Method to update state the Master Thief in the General Repository.
     *
     * @param state Current state the Master Thief.
     */
    @Override
    public synchronized void updateMThiefState(int state) {
        masterThiefState = state;
        printStatus();

    }

    /**
     * Method to update state the Thief's situation in the General Repository.
     *
     * @param thiefId Id of the Thief.
     * @param situation Current situation.
     */
    @Override
    public synchronized void updateThiefSituation(int thiefId, char situation) {
        Thief t = thiefMap.get(thiefId);
        t.situation = situation;
        printStatus();
    }

    /**
     * Method to set the target room to a specific Assault Party in the General
     * Repository.
     *
     * @param partyId Id of the Party.
     * @param room target room of the Assault Party.
     */
    @Override
    public synchronized void setRoomIdAP(int partyId, int room) {
        roomId[partyId] = room;
    }

    /**
     * Method to set the number of painting stolen in the entire heist in the
     * General Repository.
     *
     * @param toalCanvas Number of canvas "borrowed" from the Museum.
     */
    @Override
    public synchronized void setCollectedCanvas(int toalCanvas) {
        canvasCollected = toalCanvas;
    }

    /**
     * Method to set the Room attributes in the General Repository.
     *
     * @param roomId Id of the Room.
     * @param distance Distance from the outside to the Room.
     * @param paitings Number of paintings present on the room.
     */
    @Override
    public synchronized void setRoomAtributes(int roomId, int distance, int paitings) {
        rooms[roomId] = new Room(paitings, distance);
    }

    /**
     * Method to add the Ordinary Thief attributes in the General Repository.
     *
     * @param thiefId Id of the Thief.
     * @param speed Maximum displacement of the Thief.
     */
    @Override
    public synchronized void addThief(int thiefId, int speed) {
        System.out.println("Thief added");
        thiefMap.put(thiefId, new Thief(thiefId, speed));
    }

    /**
     * Method to put a Ordinary Thief a specified Assault Party in the General
     * Repository.
     *
     * @param partyId Id of the Assault Party.
     * @param thiefId Id of the Thief.
     * @param elemId Thief Id in the Assault Party.
     */
    @Override
    public synchronized void setPartyElement(int partyId, int thiefId, int elemId) {
        partyElement[partyId][elemId] = thiefId;
    }

    /**
     * Method to clear the elements of a Assault Party in the General
     * Repository.
     *
     * @param partyId Id of the Assault Party.
     */
    @Override
    public synchronized void clearParty(int partyId) {
        for (int i = 0; i < ASSAULT_PARTY_SIZE; i++) {
            partyElement[partyId][i] = -1;
            roomId[partyId] = -1;
        }
        printStatus();
    }

    /**
     * Method to update the position of a Ordinary Thief in the General
     * Repository.
     *
     * @param thiefId Id of the Thief thar Invoked the method.
     * @param position Current position of the Thief.
     */
    @Override
    public synchronized void updateThiefPosition(int thiefId, int position) {
        Thief currentT = thiefMap.get(thiefId);
        currentT.position = position;
        printStatus();
    }

    /**
     * Method to update the contents of a Ordinary Thief cylinder in the General
     * Repository.
     *
     * @param thiefId Id of the thief that invoked the method.
     * @param hasCanvas the state of the thief cylinder.
     */
    @Override
    public synchronized void updateThiefCylinder(int thiefId, boolean hasCanvas) {
        Thief currentT = thiefMap.get(thiefId);
        currentT.canvas = hasCanvas ? 1 : 0;
        printStatus();
    }

    private synchronized void printStatus() {
        if (DEBUG) {
            return;
        }
        FirstLine();
        pw.println();
        SecondLine();
    }

    private synchronized void InitializeLog() {
        if (DEBUG) {
            return;
        }
        try {
            pw = new PrintWriter(log);
            pw.println("                               Heist to the Museum - Description of the internal state ");
            pw.println();

            String head = "MstT   ";
            for (int i = 1; i <= 6; i++) {
                head += "Thief " + Integer.toString(i);
                head += "      ";
            }
            pw.println(head);

            head = "Stat    ";
            for (int i = 1; i <= N_ORD_THIEVES; i++) {
                head += "Stat S MD";
                head += "    ";
            }
            pw.println(head);

            head = "";
            for (int i = 1; i <= N_ASSAULT_PARTIES; i++) {
                head += "                    Assault party " + Integer.toString(i);
                head += "   ";
            }
            head += "                        Museum";
            pw.println(head);

            head = "           ";
            for (int i = 1; i <= ASSAULT_PARTY_SIZE; i++) {
                head += "Elem " + Integer.toString(i);
                head += "     ";
            }

            head += "     ";
            for (int i = 1; i <= ASSAULT_PARTY_SIZE; i++) {
                head += "Elem " + Integer.toString(i);
                head += "     ";
            }

            head += "  ";
            for (int i = 1; i <= N_ROOMS; i++) {
                head += "Room " + Integer.toString(i);
                head += "   ";
            }

            pw.println(head);

            head = "  ";
            for (int i = 1; i <= N_ASSAULT_PARTIES; i++) {
                head += "  RId";
                for (int j = 1; j <= ASSAULT_PARTY_SIZE; j++) {
                    head += "  Id Pos Cv";
                }
            }
            head += "  ";
            for (int i = 1; i <= N_ROOMS; i++) {
                head += "    NP DT";
            }
            pw.println(head);

            pw.flush();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Method to finalize the log.
     */
    @Override
    public synchronized void FinalizeLog() {
        if (DEBUG) {
            return;
        }
        pw.printf("My friends, tonight's effort produced %2d priceless paintings!", canvasCollected);
        pw.println("\nLegend:");
        pw.println("MstT Sta - state of the master thief");
        pw.println("Thief # Stat - state of the ordinary thief # (# - 1 .. 6)");
        pw.println("Thief # S - situation of the ordinary thief  # (# - 1 .. 6) either 'W' (waiting to join a party) or 'P' (in party) ");
        pw.println("Thief # MD - maximum displacement of the ordinary thief # (# - 1 .. 6) a random number between 2 and 6 ");
        pw.println("Assault party # RId - assault party # (# - 1,2) elem # (# - 1 .. 3) room identification (1 .. 5)");
        pw.println("Assault party # Elem # Id - assault party # (# - 1,2) elem # (# - 1 .. 3) member identification (1 .. 6) ");
        pw.println("Assault party # Elem # Pos - assault party # (# - 1,2) elem # (# - 1 .. 3) present position (0 .. DT RId) ");
        pw.println("Assault party # Elem # Cv - assault party # (# - 1,2) elem # (# - 1 .. 3) carrying a canvas (0,1)");
        pw.println("Museum Room # NP - room identification (1 .. 5) number of paintings presently hanging on the walls");
        pw.println("Museum Room # DT - room identification (1 .. 5) distance from outside gathering site, a random number between 15 and 30");
        pw.flush();
        pw.close();
    }

    /**
     * Method to set the number of paintings in the General Repository.
     *
     * @param id Id of the Room
     * @param paitings Number of paintings in the Room.
     */
    @Override
    public synchronized void setRoomCanvas(int id, int paitings) {
        rooms[id].paitings_left = paitings;
    }

    private class Room {

        private Room(int paitings, int distance) {
            this.distance = distance;
            this.paitings_left = paitings;
        }
        private int paitings_left, distance;
    }

    private class Thief {

        private int id, position, canvas, state, speed;
        private char situation;

        private Thief(int id, int speed) {
            this.id = id;
            this.position = 0;
            this.speed = speed;
            this.situation = 'W';
            this.canvas = 0;
            this.state = OrdinaryThiefState.OUTSIDE;
        }
    }
}

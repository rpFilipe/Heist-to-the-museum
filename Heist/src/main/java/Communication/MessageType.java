/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

/**
 *
 * @author ricardo
 */
public enum MessageType {

    /**
     * Message of type Acknowledge.
     */
    ACK,

    /**
     * Message of type SERVER_RESPONSE.
     */
    SERVER_RESPONSE,

    /**
     * Message of type CONFIGURATION_REQUEST_PORT.
     */
    CONFIGURATION_REQUEST_PORT,

    /**
     * Message of type CONFIGURATION_REQUEST_LOCATION.
     */
    CONFIGURATION_REQUEST_LOCATION,

    /**
     * Message of type TERMINATE.
     */
    TERMINATE,

    /**
     * Message of type roolACanvas.
     */
    RAC,

    /**
     * Message of type getRoomDistance.
     */
    GRD,

    /**
     * Message of type getTargetRoom.
     */
    GTR,

    /**
     * Message of type getPartyToDeploy.
     */
    GPTD,

    /**
     * Message of type takeARest.
     */
    TAR,

    /**
     * Message of type CollectCanvas.
     */
    CC,

    /**
     * Message of type isHeistCompleted.
     */
    IHC,

    /**
     * Message of type waitingNeeded.
     */
    WN,

    /**
     * Message of type handACanvas.
     */
    HAC,

    /**
     * Message of type startOperations.
     */
    SO,

    /**
     * Message of type prepareAssaultParty.
     */
    PAP,

    /**
     * Message of type sumUpResults.
     */
    SUR,

    /**
     * Message of type apraiseSit.
     */
    AS,

    /**
     * Message of type roolACanvas.
     */
    HC,

    /**
     * Message of type amINedded.
     */
    AIN,

    /**
     * Message of type getPartyId.
     */
    GPI,

    /**
     * Message of type prepateExcursion.
     */
    PE,

    /**
     * Message of type setRoom.
     */
    SR,

    /**
     * Message of type sendAssaultParty.
     */
    SAP,

    /**
     * Message of type CrawlIn.
     */
    CI,

    /**
     * Message of type CrawlOut.
     */
    CO,

    /**
     * Message of type joinParty.
     */
    JP,

    /**
     * Message of type reverseDirection.
     */
    RD,

    /**
     * Message of type finalizeLog.
     */
    FN,

    /**
     * Message of type addThief.
     */
    AT,

    /**
     * Message of type clearParty.
     */
    CP,

    /**
     * Message of type setCollectedCanvas.
     */
    SCC,

    /**
     * Message of type setPartyElement.
     */
    SPE,

    /**
     * Message of type setRoomAttributes.
     */
    SRA,

    /**
     * Message of type setRoomCanvas.
     */
    SRC,

    /**
     * Message of type setRoomIdAP.
     */
    SRIAP,

    /**
     * Message of type updateMasterThiefState.
     */
    UMTS,

    /**
     * Message of type updateThiefCylinder.
     */
    UTC,

    /**
     * Message of type updateThiefPosition.
     */
    UTP,

    /**
     * Message of type updateThiefState.
     */
    UTS,

    /**
     * Message of type updateThiefSituation.
     */
    UTST,

    /**
     * Message of type getN_ROOMS;
     */
    NR,

    /**
     * Message of type getN_ORD_THIEVES;
     */
    NOT,

    /**
     * Message of type getMAX_PAITING_PER_ROOM;
     */
    MAPPR,

    /**
     * Message of type getMIN_PAITING_PER_ROOM;
     */
    MIPPR,

    /**
     * Message of type getMAX_ROOM_DISTANCE;
     */
    MARD,

    /**
     * Message of type getMIN_ROOM_DISTANCE;
     */
    MIRD,

    /**
     * Message of type getASSAULT_PARTY_SIZE;
     */
    APS,

    /**
     * Message of type getMAX_DISPLACEMENT;
     */
    NAP,

    /**
     * Message of type getMAX_DISPLACEMENT;
     */
    MD,

    /**
     * Message of type getMAX_THIEF_SPEED;
     */
    MATS,

    /**
     * Message of type getMIN_THIEF_SPEED;
     */
    MITS,

    /**
     * Message of type getMAX_DISTANCE_BETWEEN_THIVES;
     */
    MDBT;
}

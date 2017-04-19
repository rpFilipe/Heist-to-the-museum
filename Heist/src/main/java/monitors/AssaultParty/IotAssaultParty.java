/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

/**
 *
 * @author Ricardo Filipe
 */
public interface IotAssaultParty {

    /**
     * Method to Thieves crawl from the Concentration Site to the Museum interior.
     * @param id of the Ordinary Thief that invoked the method.
     */
    public void crawlIn(int id);

    /**
     * Method to Thieves crawl from the Museum to the Concentration Site.
     * @param id of the Ordinary Thief that invoked the method.
     */
    public void crawlOut(int id);

    /**
     * Method to add a Ordinary Thief joins this Assault Party.
     * @param id of the Ordinary Thief that invoked the method.
     * @param speed maximum crawling distance per step.
     */
    public void joinParty(int id, int speed);

    /**
     * Method to change the direction in crawling.
     */
    public void reverseDirection();

    /**
     * Get the current target room assigned to this Assault Party.
     * @return Room id
     */
    public int getTargetRoom();
}

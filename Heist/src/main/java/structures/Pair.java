package structures;

import java.io.Serializable;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */

/**
 * Par de valores.
 * @param <T1> tipo 1º valor
 * @param <T2> tipo 2º valor
 */
public class Pair<T1, T2> implements Serializable {
    /** Primeiro valor */
    public final T1 first;
    /** Segundo valor */
    public final T2 second;
    
    /**
     * Constroi um par de valores.
     * @param first primeiro valor
     * @param second segundo valor
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}
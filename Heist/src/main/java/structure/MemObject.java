/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.memFIFO;

/**
 *
 * @author mota
 */
public abstract class MemObject {
    /**
   *  Tamanho da memória em número de posições
   *
   *    @serialField nMax
   */

   protected int nMax = 0;

  /**
   *  Área de armazenamento
   *
   *    @serialField mem
   */

   protected Object [] mem = null;

  /**
   *  Instanciação da memória.
   *
   *    @param nElem tamanho da memória (n. de elementos do array de armazenamento)
   */

   protected MemObject (int nElem)
   {
     mem = new Object [nElem];
     nMax = nElem;
   }

  /**
   *  Escrita de um valor (método virtual).
   *
   *    @param val valor a armazenar
   */

   protected abstract void write (Object val);

  /**
   *  Leitura de um valor (método virtual).
   *
   *    @return valor armazenado
   */

   protected abstract Object read ();
    
}

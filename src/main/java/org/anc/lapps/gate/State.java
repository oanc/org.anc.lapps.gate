package org.anc.lapps.gate;

/**
 * Holds state information about the underlying GATE subsystem.
 *
 * @author Keith Suderman
 */
public final class State
{
   /**
    * This serves as a flag and as an object to synchronize on when checking
    * to see if GATE needs to be initialized.
    */
   public static Boolean initialized = false;

   private State()
   {

   }
}

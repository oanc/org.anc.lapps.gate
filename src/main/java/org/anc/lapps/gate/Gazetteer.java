package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class Gazetteer extends SimpleGateService
{
   public Gazetteer()
   {
      super("gate.creole.gazetteer.DefaultGazetteer");
   }

   public long[] requires() {
      return new long[] {Types.TOKEN };
   }

   // TODO Determine what annotation types are returned by the Gazetteer.
   public long[] produces() {
      return new long[] { };
   }
}

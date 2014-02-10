package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class Coreferencer extends SimpleGateService
{
   public Coreferencer()
   {
      super();
      createResource("gate.creole.coref.Coreferencer");
   }

   public long[] produces()
   {
      return new long[] { Types.COREF };
   }

   public long[] requires()
   {
      return new long[] { Types.NAMED_ENTITES };
   }
}

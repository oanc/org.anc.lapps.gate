package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class Coreferencer extends PooledGateService
{
   public Coreferencer()
   {
      super();
      createResource("gate.creole.coref.Coreferencer");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.NAMED_ENTITES, Types.COREF };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE, Types.NAMED_ENTITES };
   }
}

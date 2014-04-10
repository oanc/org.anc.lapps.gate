package org.anc.lapps.gate;

import org.anc.lapps.gate.PooledGateService;
import org.anc.lapps.gate.SimpleGateService;
import org.lappsgrid.discriminator.Types;

public class Tagger extends PooledGateService
{
   public Tagger()
   {
      super();
      createResource("gate.creole.POSTagger");
   }

   public long[] requires()
   {
      return new long[] {
            Types.GATE,
            Types.TOKEN
      };
   }
   
   public long[] produces()
   {
      return new long[] {
            Types.GATE,
            Types.POS
         };      
   }
}

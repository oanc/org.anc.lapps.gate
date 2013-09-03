package org.anc.lapps.gate.tagger;

import org.anc.lapps.gate.PooledGateService;
import org.anc.lapps.gate.SimpleGateService;
import org.lappsgrid.discriminator.Types;

public class Tagger extends SimpleGateService
{

   public Tagger()
   {
      super("gate.creole.POSTagger");
   }

   public long[] requires()
   {
      return new long[] {
            Types.GATE,
            Types.SENTENCE,
            Types.TOKEN
      };
   }
   
   public long[] produces()
   {
      return new long[] {
            Types.GATE,
            Types.SENTENCE,
            Types.TOKEN,
            Types.POS
         };      
   }
}

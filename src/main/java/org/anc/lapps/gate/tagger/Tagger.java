package org.anc.lapps.gate.tagger;

import org.anc.lapps.gate.GateService;
import org.lappsgrid.discriminator.Types;

public class Tagger extends GateService
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

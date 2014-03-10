package org.anc.lapps.gate;

import org.anc.lapps.gate.PooledGateService;
import org.anc.lapps.gate.SimpleGateService;
import org.lappsgrid.discriminator.Types;

public class Tokenizer extends SimpleGateService
{
   public Tokenizer()
   {
      super();
      createResource("gate.creole.tokeniser.DefaultTokeniser");
   }

   @Override
   public long[] requires()
   {
      return new long[] {
        Types.TEXT
      };
   }

   @Override
   public long[] produces()
   {
      return new long[] {
            Types.GATE,
            Types.TOKEN
      };
   }
}

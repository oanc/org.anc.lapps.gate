package org.anc.lapps.gate.tokenizer;

import org.anc.lapps.gate.GateService;
import org.lappsgrid.discriminator.Types;

public class Tokenizer extends GateService
{
   public Tokenizer()
   {
      super("gate.creole.tokeniser.DefaultTokeniser");
   }

   @Override
   public long[] requires()
   {
      return new long[] {
        Types.GATE_DOCUMENT,
        Types.GATE_SENTENCE_ANNOTAION
      };
   }

   @Override
   public long[] produces()
   {
      return new long[] {
            Types.GATE_DOCUMENT,
            Types.GATE_SENTENCE_ANNOTAION,
            Types.GATE_TOKEN_ANNOTATION
      };
   }
}

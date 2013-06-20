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
            Types.GATE_DOCUMENT,
            Types.GATE_SENTENCE_ANNOTAION,
            Types.GATE_TOKEN_ANNOTATION
      };
   }
   
   public long[] produces()
   {
      return new long[] {
            Types.GATE_DOCUMENT,
            Types.GATE_SENTENCE_ANNOTAION,
            Types.GATE_TOKEN_ANNOTATION,
            Types.GATE_POS_ANNOTATION
         };      
   }
}

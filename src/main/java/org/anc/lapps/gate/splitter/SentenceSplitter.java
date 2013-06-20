package org.anc.lapps.gate.splitter;

import org.anc.lapps.gate.GateService;
import org.lappsgrid.discriminator.Types;

public class SentenceSplitter extends GateService
{
   public SentenceSplitter()
   {
      super("gate.creole.splitter.SentenceSplitter");
   }
   
   public long[] requires()
   {
      return new long[] { Types.GATE_DOCUMENT };
   }
   
   public long[] produces()
   {
      return new long[] { Types.GATE_DOCUMENT, Types.GATE_SENTENCE_ANNOTAION };
   }
}

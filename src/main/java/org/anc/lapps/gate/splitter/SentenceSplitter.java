package org.anc.lapps.gate.splitter;

import org.anc.lapps.gate.GateService;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentenceSplitter extends GateService
{
   protected static final Logger logger = LoggerFactory.getLogger(SentenceSplitter.class);
   public SentenceSplitter()
   {
      super("gate.creole.splitter.SentenceSplitter");
      logger.info("Sentence splitter created.");
   }
   
   public long[] requires()
   {
      logger.info("Called requires");
      return new long[] { Types.GATE };
   }
   
   public long[] produces()
   {
      logger.info("Called produces.");
      return new long[] { Types.GATE, Types.SENTENCE };
   }
}

package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentenceSplitter extends SimpleGateService
{
   protected static final Logger logger = LoggerFactory.getLogger(SentenceSplitter.class);
   public SentenceSplitter()
   {
      super();
      createResource("gate.creole.splitter.SentenceSplitter");
      logger.info("Sentence splitter created.");
   }
   
   public long[] requires()
   {
      logger.info("Called requires");
      return new long[] { Types.GATE, Types.TOKEN };
   }
   
   public long[] produces()
   {
      logger.info("Called produces.");
      return new long[] { Types.GATE, Types.TOKEN, Types.SENTENCE };
   }
}

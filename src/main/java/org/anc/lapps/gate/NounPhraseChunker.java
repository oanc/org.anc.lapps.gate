package org.anc.lapps.gate;

import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Keith Suderman
 */
public class NounPhraseChunker extends SimpleGateService
{
//   private Logger logger = LoggerFactory.getLogger(NounPhraseChunker.class);

   public NounPhraseChunker()
   {
//      logger.info("Creating the NounPhraseChunker.");
      super();
      createResource("mark.chunking.GATEWrapper");
   }

//   public Data configure(Data input)
//   {
//      return DataFactory.error("Unsupported operation.");
//   }
//
//   public Data execute(Data input)
//   {
//      return input;
//   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.POS, Types.NOUN_CHUNK };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.POS };
   }
}

package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Keith Suderman
 */
public class LingPipe extends ApplicationService
{
   public static final Logger logger = LoggerFactory.getLogger(Annie.class);
   public LingPipe()
   {
      super("lingpipe.gapp");
      logger.info("Loaded LingPipe GATE application.");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.SENTENCE, Types.POS,
              Types.NAMED_ENTITES, Types.LEMMA };
   }

}

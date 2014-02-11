package org.anc.lapps.gate;

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
}

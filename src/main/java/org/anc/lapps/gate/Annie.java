package org.anc.lapps.gate;

import gate.*;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import gate.util.persistence.PersistenceManager;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author Keith Suderman
 */
public class Annie extends ApplicationService
{
   public static final Logger logger = LoggerFactory.getLogger(Annie.class);
   public Annie()
   {
      super("annie.gapp");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.SENTENCE, Types.POS,
         Types.NAMED_ENTITES, Types.LEMMA };
   }
}

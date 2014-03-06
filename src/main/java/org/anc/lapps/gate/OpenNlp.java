package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class OpenNlp extends ApplicationService
{
   public OpenNlp()
   {
      super("opennlp.gapp");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.SENTENCE, Types.POS,
              Types.NAMED_ENTITES, Types.LEMMA };
   }

}

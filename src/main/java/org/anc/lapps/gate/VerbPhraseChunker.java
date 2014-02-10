package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class VerbPhraseChunker extends SimpleGateService
{
   public VerbPhraseChunker()
   {
      super();
      createResource("gate.creole.VPChunker");
   }

   public long[] produces()
   {
      return new long[] { Types.VERB_CHUNK };
   }

   public long[] requires()
   {
      return new long[] { Types.POS };
   }
}

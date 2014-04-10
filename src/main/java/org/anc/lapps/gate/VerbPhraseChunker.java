package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class VerbPhraseChunker extends PooledGateService
{
   public VerbPhraseChunker()
   {
      super();
      createResource("gate.creole.VPChunker");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.VERB_CHUNK };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.POS };
   }
}

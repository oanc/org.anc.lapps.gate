package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class NamedEntityRecognizer extends SimpleGateService
{
   public NamedEntityRecognizer()
   {
      super();
      createResource("gate.creole.ANNIETransducer");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.POS, Types.NAMED_ENTITES };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.POS };
   }
}

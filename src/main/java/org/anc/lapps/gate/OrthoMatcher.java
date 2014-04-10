package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class OrthoMatcher extends PooledGateService
{
   public OrthoMatcher()
   {
      super();
      createResource("gate.creole.orthomatcher.OrthoMatcher");
   }

   //TODO Determine what annotation types are returned by the OrthoMatcher.
   public long[] produces() {
      return new long[] { Types.GATE, Types.NAMED_ENTITES };
   }

   public long[] requires() {
      return new long[] { Types.GATE, Types.NAMED_ENTITES };
   }
}

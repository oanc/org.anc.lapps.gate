package org.anc.lapps.gate;

import org.lappsgrid.discriminator.Types;

/**
 * @author Keith Suderman
 */
public class OrthoMatcher extends SimpleGateService
{
   public OrthoMatcher()
   {
      super("gate.creole.orthomatcher.OrthoMatcher");
   }

   //TODO Determine what annotation types are returned by the OrthoMatcher.
   public long[] produces() {
      return new long[] { };
   }

   public long[] requires() {
      return new long[] { Types.NAMED_ENTITES };
   }
}

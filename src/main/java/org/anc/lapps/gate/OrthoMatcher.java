package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
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

   public Data execute(Data input)
   {
      Document document = null;
      try
      {
         document = doExecute(input);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the Coreferencer.", e);
      }
      if (document == null)
      {
         return DataFactory.error(BUSY);
      }
      String producer = this.getClass().getName() + "_" + Version.getVersion();
      FeatureMap features = Factory.newFeatureMap();
      features.put("type", "???");
      features.put("producer", producer);
      return DataFactory.gateDocument(document.toXml());
   }

}

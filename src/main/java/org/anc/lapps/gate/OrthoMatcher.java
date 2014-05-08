package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Metadata;

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
         return DataFactory.error("Unable to execute the OrthoMatcher.", e);
      }
      if (document == null)
      {
         return DataFactory.error(BUSY);
      }
      String producer = this.getClass().getName() + "_" + Version.getVersion();
      FeatureMap features = document.getFeatures();
      Integer step = (Integer) features.get("lapps:step");
      if (step == null) {
         step = 1;
      }
      features.put("lapps:step", step + 1);
      features.put("lapps:" + Annotations.NOMINAL_COREFERENCE, step + " " + producer + " coref:annie");
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

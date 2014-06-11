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
public class Gazetteer extends PooledGateService
{
   public Gazetteer()
   {
      super();
      createResource("gate.creole.gazetteer.DefaultGazetteer");
   }

   public long[] requires() {
      return new long[] { Types.GATE, Types.TOKEN };
   }

   // TODO Determine what annotation types are returned by the Gazetteer.
   public long[] produces() {
      return new long[] { Types.GATE, Types.LOOKUP };
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
         return DataFactory.error("Unable to execute the Gazetteer.", e);
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
      features.put("lapps:" + Annotations.LOOKUP, step + " " + producer + " lookup:gate");
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

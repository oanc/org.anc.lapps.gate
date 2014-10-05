package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
      description = "GATE Gazetteer",
      requires = "http://vocab.lappsgrid.org/Token",
      produces = "http://vocab.lappsgrid.org/Lookup"
)
public class Gazetteer extends SimpleGateService
{
   public Gazetteer()
   {
      super(Gazetteer.class);
      createResource("gate.creole.gazetteer.DefaultGazetteer");
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

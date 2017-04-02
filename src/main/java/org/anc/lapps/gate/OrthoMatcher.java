package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.vocabulary.Annotations;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
        description = "GATE OrthoMatcher",
        requires = {"http://vocab.lappsgrid.org/Person"},
        produces = {"\thttp://vocab.lappsgrid.org/NamedEntity#matches"}
)
public class OrthoMatcher extends SimpleGateService
{
   public OrthoMatcher()
   {
      super(OrthoMatcher.class);
      createResource("gate.creole.orthomatcher.OrthoMatcher");
   }

   public String execute(String input)
   {
      Document document = null;
      try
      {
         document = doExecute(input, Discriminators.Uri.COREF);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the OrthoMatcher.", e);
      }
      if (document == null)
      {
         return DataFactory.error("This was unexpected...");
      }
//      String producer = this.getClass().getName() + "_" + Version.getVersion();
//      FeatureMap features = document.getFeatures();
//      Integer step = (Integer) features.get("lapps:step");
//      if (step == null) {
//         step = 1;
//      }
//      features.put("lapps:step", step + 1);
//      features.put("lapps:coref", step + " " + producer + " coref:annie");
      String result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;

@ServiceMetadata(
		  description = "GATE Part of Speech Tagger",
        requires = {"http://vocab.lappsgrid.org/Token"},
        produces = {"http://vocab.lappsgrid.org/Token#pos"}
)
public class Tagger extends SimpleGateService
{
   public Tagger()
   {
      super(Tagger.class);
      createResource("gate.creole.POSTagger");
   }

   public String execute(String input)
   {
      Document document = null;
      try
      {
         document = doExecute(input);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the Tagger.", e);
      }
      if (document == null)
      {
         return DataFactory.error("This was unexpected...");
      }
      String producer = this.getClass().getName() + "_" + Version.getVersion();
      FeatureMap features = document.getFeatures();
      Integer step = (Integer) features.get("lapps:step");
      if (step == null) {
         step = 1;
      }
      features.put("lapps:step", step + 1);
      features.put("lapps:" + Annotations.PART_OF_SPEECH, step + " " + producer + " " + Contents.TagSets.GATE);
      String result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;

@ServiceMetadata(
        description = "ANNIE Tokeniser from GATE.",
        requires_format = {
					 "text",
					 "application/xml",
					 "gate"
		  },
        produces = "http://vocab.lappsgrid.org/Token"
)
public class Tokenizer extends SimpleGateService
{
   public Tokenizer()
   {
      super(Tokenizer.class);
      createResource("gate.creole.tokeniser.DefaultTokeniser");
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
         return DataFactory.error("Unable to execute the Tokenizer.", e);
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
      features.put("lapps:" + Annotations.TOKEN, step + " " + producer + " " + Contents.Tokenizations.ANNIE);

      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }
}

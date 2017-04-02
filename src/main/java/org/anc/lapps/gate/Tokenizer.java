package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;

import static org.lappsgrid.discriminator.Discriminators.*;

@ServiceMetadata(
        description = "ANNIE Tokeniser from GATE.",
        requires_format = {
					 "text",
					 "xml",
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

   public String execute(String input)
   {
      String producer = this.getClass().getName() + "_" + Version.getVersion();
      Document document = null;
      try
      {
         document = doExecute(input, Uri.TOKEN);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the Tokenizer.", e);
      }
      if (document == null)
      {
         return DataFactory.error(BUSY);
      }


      String result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }
}

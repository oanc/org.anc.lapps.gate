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
        description = "GATE Noun Phrase Chunker",
        requires = {"http://vocab.lappsgrid.org/Token","http://vocab.lappsgrid.org/Token#pos"},
        produces = {"http://vocab.lappsgrid.org/NounChunk"}
)
public class NounPhraseChunker extends SimpleGateService
{
   public NounPhraseChunker()
   {
      super(NounPhraseChunker.class);
      createResource("mark.chunking.GATEWrapper");
   }


   public String execute(String input)
   {
      Document document = null;
      try
      {
         document = doExecute(input, Discriminators.Uri.NCHUNK);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the NounPhraseChunker.", e);
      }
      if (document == null)
      {
         return DataFactory.error("This was unexpected.");
      }
//      String producer = this.getClass().getName() + "_" + Version.getVersion();
//      FeatureMap features = document.getFeatures();
//      Integer step = (Integer) features.get("lapps:step");
//      if (step == null) {
//         step = 1;
//      }
//      features.put("lapps:step", step + 1);
//      features.put("lapps:nchunk", step + " " + producer + " chunk:annie");
      String result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

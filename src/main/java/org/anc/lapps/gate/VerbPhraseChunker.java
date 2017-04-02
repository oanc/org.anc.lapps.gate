package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
        description = "GATE Verb Phrase Chunker",
        requires = {"http://vocab.lappsgrid.org/Token","http://vocab.lappsgrid.org/Token#pos"},
        produces = {"http://vocab.lappsgrid.org/VerbChunk"}
)
public class VerbPhraseChunker extends SimpleGateService
{
   public VerbPhraseChunker()
   {
      super(VerbPhraseChunker.class);
      createResource("gate.creole.VPChunker");
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
         return DataFactory.error("Unable to execute the Verb Phrase Chunker.", e);
      }
      if (document == null)
      {
         return DataFactory.error(BUSY);
      }
//      String producer = this.getClass().getName() + "_" + Version.getVersion();
//      FeatureMap features = document.getFeatures();
//      Integer step = (Integer) features.get("lapps:step");
//      if (step == null) {
//         step = 1;
//      }
//      features.put("lapps:step", step + 1);
//      features.put("lapps:vchunk", step + producer + " " + Contents.Chunks.VERBS);
      String result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

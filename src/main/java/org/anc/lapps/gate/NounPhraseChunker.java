package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
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
//   private Logger logger = LoggerFactory.getLogger(NounPhraseChunker.class);

   public NounPhraseChunker()
   {
//      logger.info("Creating the NounPhraseChunker.");
      super(NounPhraseChunker.class);
      createResource("mark.chunking.GATEWrapper");
   }

//   public Data configure(Data input)
//   {
//      return DataFactory.error("Unsupported operation.");
//   }
//
//   public Data execute(Data input)
//   {
//      return input;
//   }

//   public long[] produces()
//   {
//      return new long[] { Types.GATE, Types.NOUN_CHUNK };
//   }
//
//   public long[] requires()
//   {
//      return new long[] { Types.GATE, Types.TOKEN, Types.POS };
//   }

   public String execute(String input)
   {
      Document document = null;
      try
      {
         document = doExecute(input);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the NounPhraseChunker.", e);
      }
      if (document == null)
      {
         return DataFactory.error("This was unexpected.");
      }
      String producer = this.getClass().getName() + "_" + Version.getVersion();
      FeatureMap features = document.getFeatures();
      Integer step = (Integer) features.get("lapps:step");
      if (step == null) {
         step = 1;
      }
      features.put("lapps:step", step + 1);
      features.put("lapps:" + Annotations.NCHUNK, step + " " + producer + " chunk:annie");
      String result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

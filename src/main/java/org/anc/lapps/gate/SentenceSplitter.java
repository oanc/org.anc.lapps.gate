package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceMetadata(
        description = "GATE Sentence Splitter",
        requires = "token",
        produces = "sentence"
)
public class SentenceSplitter extends SimpleGateService
{
   protected static final Logger logger = LoggerFactory.getLogger(SentenceSplitter.class);
   public SentenceSplitter()
   {
      super(SentenceSplitter.class);
      createResource("gate.creole.splitter.SentenceSplitter");
      logger.info("Sentence splitter created.");
   }
   
   public String execute(String input)
   {
      Document document;
      try
      {
         document = doExecute(input);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the Sentence Splitter.", e);
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
      features.put("lapps:" + Annotations.SENTENCE, step + " " + producer + " " + Contents.Chunks.SENTENCES);
      String result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

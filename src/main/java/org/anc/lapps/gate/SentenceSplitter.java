package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;
import org.lappsgrid.vocabulary.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentenceSplitter extends PooledGateService
{
   protected static final Logger logger = LoggerFactory.getLogger(SentenceSplitter.class);
   public SentenceSplitter()
   {
      super();
      createResource("gate.creole.splitter.SentenceSplitter");
      logger.info("Sentence splitter created.");
   }
   
   public long[] requires()
   {
      logger.info("Called requires");
      return new long[] { Types.GATE, Types.TOKEN };
   }
   
   public long[] produces()
   {
      logger.info("Called produces.");
      return new long[] { Types.GATE, Types.SENTENCE };
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
         return DataFactory.error("Unable to execute the Sentence Splitter.", e);
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
      features.put("lapps:" + Annotations.SENTENCE, step + " " + producer + " chunk:annie");
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

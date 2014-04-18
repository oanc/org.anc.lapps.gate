package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Contents;
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
         return DataFactory.error("Unable to execute the Coreferencer.", e);
      }
      if (document == null)
      {
         return DataFactory.error(BUSY);
      }
      String producer = this.getClass().getName() + "_" + Version.getVersion();
      FeatureMap features = Factory.newFeatureMap();
      features.put("type", Contents.Chunks.SENTENCES);
      features.put("producer", producer);
      return DataFactory.gateDocument(document.toXml());
   }

}

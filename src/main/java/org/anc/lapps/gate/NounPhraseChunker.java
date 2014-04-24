package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;
import org.lappsgrid.vocabulary.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Keith Suderman
 */
public class NounPhraseChunker extends PooledGateService
{
//   private Logger logger = LoggerFactory.getLogger(NounPhraseChunker.class);

   public NounPhraseChunker()
   {
//      logger.info("Creating the NounPhraseChunker.");
      super();
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

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.NOUN_CHUNK };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.POS };
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
      features.put(Annotations.NCHUNK, producer + " chunk:annie");
//      features.put(Metadata.Contains.TYPE, "chunk:annie");
//      features.put(Metadata.Contains.PRODUCER, producer);
//      features.put("annotation", Annotations.NCHUNK);
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

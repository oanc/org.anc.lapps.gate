package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.anc.lapps.gate.PooledGateService;
import org.anc.lapps.gate.SimpleGateService;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;
import org.lappsgrid.vocabulary.Metadata;

import java.util.List;

public class Tagger extends PooledGateService
{
   public Tagger()
   {
      super();
      createResource("gate.creole.POSTagger");
   }

   public long[] requires()
   {
      return new long[] {
            Types.GATE,
            Types.TOKEN
      };
   }
   
   public long[] produces()
   {
      return new long[] {
            Types.GATE,
            Types.POS
         };      
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
      features.put(Annotations.PART_OF_SPEECH, producer + " " + Contents.TagSets.GATE);
//      features.put(Metadata.Contains.TYPE, Contents.TagSets.GATE);
//      features.put(Metadata.Contains.PRODUCER, producer);
//      features.put("annotation", Annotations.PART_OF_SPEECH);
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

}

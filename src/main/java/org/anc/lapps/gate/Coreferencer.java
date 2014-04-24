package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Metadata;

/**
 * @author Keith Suderman
 */
public class Coreferencer extends PooledGateService
{
   public Coreferencer()
   {
      super();
      createResource("gate.creole.coref.Coreferencer");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.COREF };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE, Types.NAMED_ENTITES };
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
      features.put(Annotations.PRONOMINAL_CORREFERNCE, producer + " coref:gate");
//      features.put(Metadata.Contains.TYPE, "coref:gate");
//      features.put(Metadata.Contains.PRODUCER, producer);
//      features.put("annotation", Annotations.COREFERENCE);
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }
}

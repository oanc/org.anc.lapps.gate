package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import org.anc.resource.ResourceLoader;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;

import java.io.IOException;
import java.util.*;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
        description = "Coreferencer from GATE",
        requires = { "person"},
        produces = { "http://vocab.lappsgrid.org/NamedEntity#matches" }
)
public class Coreferencer extends SimpleGateService
{
   public Coreferencer()
   {
      super(Coreferencer.class);
      createResource("gate.creole.coref.Coreferencer");
   }

//   public long[] produces()
//   {
//      return new long[] { Types.GATE, Types.COREF };
//   }
//
//   public long[] requires()
//   {
//      return new long[] { Types.GATE, Types.NAMED_ENTITES };
//   }

   @Override
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
         return DataFactory.error(UNEXPECTED);
      }
      String producer = this.getClass().getName() + "_" + Version.getVersion();
      FeatureMap features = document.getFeatures();
      Integer step = (Integer) features.get("lapps:step");
      if (step == null) {
         step = 1;
      }
      features.put("lapps:step", step + 1);
      features.put("lapps:" + Annotations.PRONOMINAL_CORREFERNCE, step + " " + producer + " coref:gate");
//      features.put(Metadata.Contains.TYPE, "coref:gate");
//      features.put(Metadata.Contains.PRODUCER, producer);
//      features.put("annotation", Annotations.COREFERENCE);
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }

//   public Data getMetadata()
//   {
//      if (metadata == null)
//      {
//         try
//         {
//            String json = ResourceLoader.loadString("metadata/" + Coreferencer.class.getName() + ".json");
//            metadata = DataFactory.meta(json);
//         }
//         catch (IOException e)
//         {
//            metadata = DataFactory.error("Unable to load metadata json.", e);
//         }
//      }
//      return metadata;
//   }
}

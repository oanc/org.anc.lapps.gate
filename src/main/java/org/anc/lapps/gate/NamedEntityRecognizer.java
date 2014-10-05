package org.anc.lapps.gate;

import gate.*;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.experimental.annotations.ServiceMetadata;
import org.lappsgrid.vocabulary.Annotations;

import java.util.Iterator;

/**
 * @author Keith Suderman
 */
@ServiceMetadata(
	description = "GATE Named Entity Recognizer",
	requires = {"http://vocab.lappsgrid.org/Token"},
	produces = {
			 "http://vocab.lappsgrid.org/Date",
			 "http://vocab.lappsgrid.org/Person",
			 "http://vocab.lappsgrid.org/Location",
			 "http://vocab.lappsgrid.org/Organization"}
)
public class NamedEntityRecognizer extends SimpleGateService
{
   public NamedEntityRecognizer()
   {
      super(NamedEntityRecognizer.class);
      createResource("gate.creole.ANNIETransducer");
   }

//   public long[] produces()
//   {
//      return new long[] { Types.GATE, Types.PERSON, Types.LOCATION, Types.ORGANIZATION };
//   }
//
//   public long[] requires()
//   {
//      return new long[] { Types.GATE, Types.TOKEN, Types.POS, Types.LOOKUP };
//   }

   public Data execute(Data input)
   {
      Document document = null;
      try
      {
         document = super.doExecute(input);
      }
      catch (Exception e)
      {
         return DataFactory.error("Unable to execute the Named Entity Recognizer.", e);
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
      AnnotationSet set = document.getAnnotations();
      Iterator<Annotation> iterator = set.iterator();
      boolean hasLocation = false;
      boolean hasPerson = false;
      boolean hasOrganization = false;
      // Counter so we know to stop searching once we've found all three
      // annotation types
      int found = 0;
      while (iterator.hasNext() && found < 3)
      {
         Annotation annotation = iterator.next();
         String type = annotation.getType();
         if ("Location".equals(type) && !hasLocation)
         {
            ++found;
            hasLocation = true;
         }
         else if ("Organization".equals(type) && !hasOrganization)
         {
            ++found;
            hasOrganization = true;
         }
         else if ("Person".equals(type) && !hasPerson)
         {
            ++found;
            hasPerson = true;
         }
      }
      features.put("lapps:step", step + 1);
      if (hasLocation)
      {
         features.put("lapps:" + Annotations.LOCATION, step + " " + producer + " ner:annie");
      }
      if (hasPerson)
      {
         features.put("lapps:" + Annotations.PERSON, step + " " + producer + " ner:annie");
      }
      if (hasOrganization)
      {
         features.put("lapps:" + Annotations.ORGANIZATION, step + " " + producer + " ner:annie");
      }
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }
}

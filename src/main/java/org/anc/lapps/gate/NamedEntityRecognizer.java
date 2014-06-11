package org.anc.lapps.gate;

import gate.*;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;
import org.lappsgrid.vocabulary.Metadata;

import java.util.Iterator;

/**
 * @author Keith Suderman
 */
public class NamedEntityRecognizer extends PooledGateService
{
   public NamedEntityRecognizer()
   {
      super();
      createResource("gate.creole.ANNIETransducer");
   }

   public long[] produces()
   {
      return new long[] { Types.GATE, Types.PERSON, Types.LOCATION, Types.ORGANIZATION };
   }

   public long[] requires()
   {
      return new long[] { Types.GATE, Types.TOKEN, Types.POS, Types.LOOKUP };
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
         return DataFactory.error("Unable to execute the Named Entity Recognizer.", e);
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
         features.put("lapps:" + Annotations.NE_LOCATION, step + " " + producer + " ner:annie");
      }
      if (hasPerson)
      {
         features.put("lapps:" + Annotations.NE_PERSON, step + " " + producer + " ner:annie");
      }
      if (hasOrganization)
      {
         features.put("lapps:" + Annotations.NE_ORG, step + " " + producer + " ner:annie");
      }
      Data result = DataFactory.gateDocument(document.toXml());
      Factory.deleteResource(document);
      return result;
   }
}

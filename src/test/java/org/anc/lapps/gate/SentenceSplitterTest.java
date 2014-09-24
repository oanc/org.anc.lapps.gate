package org.anc.lapps.gate;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Uri;
//import org.lappsgrid.metadata.ServiceMetadata;

/**
 * @author Keith Suderman
 */
@Ignore
public class SentenceSplitterTest
{
   private WebService service;

   public SentenceSplitterTest()
   {

   }

   @Before
   public void setup()
   {
      service = new SentenceSplitter();
   }

   @After
   public void tearDown()
   {
      service = null;
   }

   @Ignore
   public void testMetadata()
   {
      WebService service = new SentenceSplitter();
      Data data = service.getMetadata();
      assertTrue(Uri.META.equals(data.getDiscriminator()));
//      ServiceMetadata metadata = new ServiceMetadata(data.getPayload());
//      System.out.println(metadata.toPrettyJson());
   }
}

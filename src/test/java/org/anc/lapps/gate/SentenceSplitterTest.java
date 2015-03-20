package org.anc.lapps.gate;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.Uri;
//import org.lappsgrid.metadata.ServiceMetadata;

/**
 * @author Keith Suderman
 */
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

   @Test
   public void testMetadata()
   {
//      service = new SentenceSplitter();
      String json = service.getMetadata();
		Data<Map> data = Serializer.parse(json, Data.class);
		System.out.println(data.getDiscriminator());
      ServiceMetadata metadata = new ServiceMetadata(data.getPayload());
      assertEquals(metadata.getVersion(), Version.getVersion());
      assertEquals(metadata.getVendor(), "http://www.anc.org");
      assertEquals(metadata.getName(), SentenceSplitter.class.getCanonicalName());
//		System.out.println(data.getPayload());
		assertTrue(data.getPayload().toString(), Uri.META.equals(data.getDiscriminator()));
//      ServiceMetadata metadata = new ServiceMetadata(data.getPayload());
//      System.out.println(metadata.toPrettyJson());
   }
}

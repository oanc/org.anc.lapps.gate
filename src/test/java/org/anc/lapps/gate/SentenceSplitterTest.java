package org.anc.lapps.gate;

import org.junit.After;
import org.junit.Before;
import org.lappsgrid.api.WebService;

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


}

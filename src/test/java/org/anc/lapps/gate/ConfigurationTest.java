package org.anc.lapps.gate;

import org.junit.*;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Keith Suderman
 */

public class ConfigurationTest
{
   private Configuration k;

   @Before
   public void setup()
   {
      k = new Configuration();
   }

   @After
   public void teardown()
   {
      k = null;
   }

   @Ignore
   public void testSave() throws IOException
   {
//      config.save("org.anc.lapps.proc.gate/src/main/resources");
      k.save();
   }

   @Test
   public void testPaths()
   {
      check(k.GATE_HOME);
      check(k.SITE_CONFIG);
      check(k.USER_CONFIG);
      check(k.PLUGINS_HOME);
   }

   @Test
   public void testPoolSize()
   {
      assertTrue("Pool size is " + k.POOL_SIZE, k.POOL_SIZE == 1);
   }

   private void check(String path)
   {
      assertTrue(path + " not found.", new File(path).exists());
   }
}

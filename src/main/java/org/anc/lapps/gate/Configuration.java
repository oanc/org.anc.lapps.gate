package org.anc.lapps.gate;

import org.anc.constants.*;

import java.io.IOException;

/**
 * @author Keith Suderman
 */
public class Configuration extends Constants
{
   @Default("/Applications/Gate-7.0")
   public final String GATE_HOME = null;

//   @Default("/Applications/Gate-7.0/plugins")
//   public final String GATE_PLUGINS = null;
//
   @Default("/Users/suderman/.gate.xml")
   public final String SITE_CONFIG = null;

   @Default("4")
   public final int POOL_SIZE = 0;

   public Configuration()
   {
      super();
      super.init();
   }

   public static void main(String[] args)
   {
      try
      {
         new Configuration().save();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}

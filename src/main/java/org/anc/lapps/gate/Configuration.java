package org.anc.lapps.gate;

import org.anc.constants.*;

import java.io.IOException;

/**
 * @author Keith Suderman
 */
public class Configuration extends Constants
{
//   @Default("/Applications/Gate-7.0")
//   public final String GATE_HOME = null;

//   @Default("/Applications/Gate-7.0/plugins")
//   public final String GATE_PLUGINS = null;
//

   @Default("/usr/share/tomcat/service_grid/webapps/GateServices/WEB-INF")
   public final String GATE_HOME = null;

   @Default("/usr/share/tomcat/service_grid/webapps/GateServices/WEB-INF")
   public final String PLUGINS_HOME = null;

   @Default("/usr/share/tomcat/service_grid/webapps/GateServices/WEB-INF/gate.xml")
   public final String SITE_CONFIG = null;

   @Default("/usr/share/tomcat/service_grid/webapps/GateServices/WEB-INF/user-gate.xml")
   public final String USER_CONFIG = null;

   @Default("8")
   public final Integer POOL_SIZE = null;

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

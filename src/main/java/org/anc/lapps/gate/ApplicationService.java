package org.anc.lapps.gate;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.util.persistence.PersistenceManager;
import org.anc.lapps.serialization.Container;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author Keith Suderman
 */
public abstract class ApplicationService implements WebService
{
   public static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
   public static final Configuration K = new Configuration();

   protected CorpusController controller;
   protected Corpus corpus;
   protected Exception savedException;

   //private static Boolean initialized = false;

   public ApplicationService(String name)
   {
      // Lock here to prevent race conditions during initialization.
      synchronized (State.initialized)
      {
         if (!State.initialized)
         {
            State.initialized = true;  // We only try this once.
            try
            {
               logger.info("Configuring Gate.");
               File gateHome = new File(K.GATE_HOME);
               if (!gateHome.exists())
               {
                  logger.error("Gate home not found: {}", gateHome.getPath());
                  savedException = new FileNotFoundException(K.GATE_HOME);
                  return;
               }
               logger.info("Gate home: {}", K.GATE_HOME);
               File plugins = new File(K.PLUGINS_HOME);
               if (!plugins.exists())
               {
                  logger.error("Gate plugins not found: {}", plugins.getPath());
                  savedException = new FileNotFoundException(K.PLUGINS_HOME);
                  return;
               }
               logger.info("Plugins home: {}", K.PLUGINS_HOME);
               File siteConfig = new File(K.SITE_CONFIG);
               if (!siteConfig.exists())
               {
                  logger.error("Site config not found: {}", siteConfig.getPath());
                  savedException = new FileNotFoundException(K.SITE_CONFIG);
                  return;
               }
               logger.info("Site config: {}", K.SITE_CONFIG);
               File userConfig = new File(K.USER_CONFIG);
               if (!userConfig.exists())
               {
                  logger.error("User config not found: {}", userConfig.getPath());
                  savedException = new FileNotFoundException(K.USER_CONFIG);
                  return;
               }
               logger.info("User config: {}", K.USER_CONFIG);
               Gate.setGateHome(gateHome);
               Gate.setSiteConfigFile(siteConfig);
               Gate.setPluginsHome(plugins);
               Gate.setUserConfigFile(userConfig);

               try
               {
                  logger.info("Initializing GATE");
                  Gate.init();
               }
               catch (Exception e)
               {
                  logger.error("Error initializing GATE.", e);
                  savedException = e;
                  return;
               }

               File[] files = plugins.listFiles();
               for (File directory : files)
               {
                  if (directory.isDirectory())
                  {
                     logger.info("Registering plugin: {}", directory.getPath());
                     Gate.getCreoleRegister().registerDirectories(directory.toURI().toURL());
                  }
               }
            }
            catch (Exception e)
            {
               logger.error("Unable to configure GATE.", e);
               savedException = e;
               return;
            }

         }
      }

      try
      {
         corpus = Factory.newCorpus("Application Corpus");
      }
      catch (ResourceInstantiationException e)
      {
         logger.error("Unable to create corpus object", e);
         savedException = e;
         return;
      }

      try
      {
         logger.info("Loading application file: {}", name);
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         if (loader == null) {
            loader = ApplicationService.class.getClassLoader();
         }
         URL url = loader.getResource(name);
         controller = (CorpusController) PersistenceManager.loadObjectFromUrl(url);
         controller.setCorpus(corpus);
         logger.info("Application loaded.");
      }
      catch (Exception e)
      {
         logger.error("Unable to load ANNIE.", e);
         savedException = e;
      }
   }

   public long[] requires() {
      return new long[] { Types.TEXT };
   }

   public long[] produces() {
      return new long[] { Types.GATE };
   }

   public Data configure(Data input) {
      return DataFactory.error("Unsupported operation.");
   }

   public Data execute(Data input) {
      String text = null;
      if (input.getDiscriminator() == Types.TEXT)
      {
         text = input.getPayload();
      }
      else if (input.getDiscriminator() == Types.JSON_LD)
      {
         String json = input.getPayload();
         Container container = new Container(json);
         text = container.getText();
      }
      else
      {
         return DataFactory.error("Unsupported input type. Expected TEXT or JSON_LD");
      }

      try
      {
         Document document = Factory.newDocument(text);
         corpus.add(document);
         controller.execute();
         corpus.clear();
         return new Data(Types.GATE, document.toXml());
      }
      catch (Exception e)
      {
         return DataFactory.error(e.getMessage());
      }
   }


}

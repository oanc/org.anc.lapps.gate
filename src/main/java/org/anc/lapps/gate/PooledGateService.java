package org.anc.lapps.gate;

import org.slf4j.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;

import org.lappsgrid.api.*;
import org.lappsgrid.core.*;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;

public abstract class PooledGateService implements WebService
{
   public static final Logger logger = LoggerFactory.getLogger(PooledGateService.class);
   public static final Configuration K = new Configuration();

   protected BlockingQueue<AbstractLanguageAnalyser> pool; // = new ArrayBlockingQueue<AbstractLanguageAnalyser>(K.POOL_SIZE);
   protected Exception savedException;
   protected final String name;

   private static boolean initialized = false;

   public PooledGateService(String gateResourceName)
   {
      logger.info("GateService constructor for {}.", gateResourceName);
      this.name = gateResourceName;
      if (!initialized)
      {
         initialized = true;
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
            logger.info("Plugins home:s {}", K.PLUGINS_HOME);
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

//            Gate.getCreoleRegister().registerBuiltins();
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
            logger.warn(e.getMessage());
         }

      }

      try
      {
         logger.info("Creating resource {}", gateResourceName);
//         resource = (AbstractLanguageAnalyser) Factory.createResource(gateResourceName);
//         logger.info("Resource created.");
         logger.debug("Initializing worker pool. Size: " + K.POOL_SIZE);
         pool = new ArrayBlockingQueue<AbstractLanguageAnalyser>(K.POOL_SIZE);
         for (int i = 0; i < K.POOL_SIZE; ++i)
         {
            pool.add((AbstractLanguageAnalyser) Factory.createResource(gateResourceName));
         }
      }
      catch (Exception e)
      {
         logger.error("Unable to initialize worker pool.", e);
         savedException = e;
      }
   }
   
   @Override
   public Data configure(Data config)
   {
      return DataFactory.error("Unsupported operation.");
   }
   
   @Override
   public Data execute(Data input)
   {
      logger.debug("Executing {}", name);
      if (savedException != null)
      {
         logger.warn("Returning saved exception: " + savedException.getMessage());
         return new Data(Types.ERROR, savedException.getMessage());
      }
      
      Document doc;
      try
      {
         doc = getDocument(input);
      }
      catch (InternalException e)
      {
         logger.error("Internal exception.", e);
         return new Data(Types.ERROR, e.getMessage());
      }

      Data result = null;
      AbstractLanguageAnalyser resource = null;
      try
      {
         resource = pool.take();
         logger.info("Executing resource {}", name);
         resource.setDocument(doc);
         resource.execute();
         result = new Data(Types.GATE, doc.toXml());
      }
      catch (Exception e)
      {
         logger.error("Error running GATE resource {}", name, e);
         return new Data(Types.ERROR, e.getMessage());
      }
      finally
      {
         pool.add(resource);
         Factory.deleteResource(doc);
      }
      logger.info("Execution complete.");
      return result;
   }

   public void destroy()
   {
      for (AbstractLanguageAnalyser resource : pool)
      {
         Factory.deleteResource(resource);
      }
   }

   Document getDocument(Data input) throws InternalException
   {
      Document doc = null;
      try
      {
         long type = input.getDiscriminator();
         if (type == Types.TEXT)
         {
            logger.info("Creating document from text.");
            doc = Factory.newDocument(input.getPayload());
         }
         else if (type == Types.GATE)
         {
            logger.info("Creating document from GATE document.");
            doc = (Document) 
                  Factory.createResource("gate.corpora.DocumentImpl", 
                    Utils.featureMap(gate.Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, 
                  input.getPayload(), 
                  gate.Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, "text/xml")); 
         }
         else
         {
            String name = DiscriminatorRegistry.get(type);
            throw new InternalException("Unknown document type : " + name);
         }
      }
      catch (ResourceInstantiationException ex)
      {
         throw new InternalException("Unable to parse Gate document", ex);
      }
      return doc;
   }
}

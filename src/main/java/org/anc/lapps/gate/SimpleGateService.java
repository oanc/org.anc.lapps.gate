package org.anc.lapps.gate;

import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.InternalException;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.vocabulary.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Keith Suderman
 */
public abstract class SimpleGateService implements WebService
{
   public static final Logger logger = LoggerFactory.getLogger(SimpleGateService.class);
   public static final Configuration K = new Configuration();

   protected AbstractLanguageAnalyser resource;
   protected Exception savedException;
   protected final String name;

   private static Boolean initialized = false;

   public SimpleGateService(String gateResourceName)
   {
      logger.info("GateService constructor for {}.", gateResourceName);
      this.name = gateResourceName;
      synchronized (initialized) {
         if (!initialized)
         {
            initialized = true;  // We only try this once.
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
               logger.warn(e.getMessage());
            }

         }
      }

      try
      {
         logger.info("Creating resource {}", gateResourceName);
         resource = (AbstractLanguageAnalyser) Factory.createResource(gateResourceName);
         logger.info("Resource created.");
      }
      catch (Exception e)
      {
         logger.error("Unable to create Gate resource.", e);
         savedException = e;
      }
   }

   // TODO: Calculate the proper service ID based on the fully qualified
   // class name and version number
   public String getServiceId()
   {
      return this.getClass().getCanonicalName() + ":" + Version.getVersion();
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
         return new Data(Types.ERROR, getStackTrace(savedException));
      }

      Document doc;
      try
      {
         doc = getDocument(input);
      }
      catch (InternalException e)
      {
         logger.error("Internal exception.", e);
         return new Data(Types.ERROR, getStackTrace(e));
      }

      Data result = null;
//      AbstractLanguageAnalyser resource = null;
      try
      {
//         resource = pool.take();
         logger.info("Executing resource {}", name);
         resource.setDocument(doc);
         resource.execute();
         doc.getFeatures().put(Metadata.PRODUCED_BY, "GATE:" + name);
         result = new Data(Types.GATE, doc.toXml());
      }
      catch (Exception e)
      {
         logger.error("Error running GATE resource {}", name, e);
         return new Data(Types.ERROR, getStackTrace(e));
      }
      finally
      {
         Factory.deleteResource(doc);
      }
      logger.info("Execution complete.");
      return result;
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

   private String getStackTrace(Throwable t)
   {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      t.printStackTrace(printWriter);
      return stringWriter.toString();
   }
}

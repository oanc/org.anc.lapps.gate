package org.anc.lapps.gate;

import org.slf4j.*;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.Utils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

import org.lappsgrid.api.*;
import org.lappsgrid.core.*;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;

public abstract class GateService implements WebService
{
   public static final Logger logger = LoggerFactory.getLogger(GateService.class);
   public static final Configuration K = new Configuration();

//   public static final long TEXT = Types.TEXT;
//   public static final long DOCUMENT = Types.GATE;
//   public static final long SENTENCE = Types.SENTENCE;
//   public static final long TOKEN = Types.TOKEN;
//   public static final long POS = Types.POS;
//
//   public static final long OK = Types.OK;
//   public static final long ERROR = Types.ERROR;

//   protected BlockingQueue<AbstractLanguageAnalyser> pool; // = new ArrayBlockingQueue<AbstractLanguageAnalyser>(K.POOL_SIZE);
   protected AbstractLanguageAnalyser resource;
   protected Exception savedException;
   protected final String name;

   private static boolean initialized = false;

   public GateService(String gateResourceName)
   {
      logger.info("GateService constructor for {}.", gateResourceName);
      this.name = gateResourceName;
      if (!initialized)
      {
         initialized = true;
         try
         {
            File gateHome = new File(K.GATE_HOME);
            File plugins = new File(gateHome, "plugins");
            Gate.setSiteConfigFile(new File(K.SITE_CONFIG));
            Gate.setGateHome(gateHome);
            Gate.setPluginsHome(plugins);
         }
         catch (Exception e)
         {
   //         logger.error("Unable to configure GATE.", e);
            logger.warn(e.getMessage());
         }

         try
         {
            logger.info("Initializing GATE");
            Gate.init();
         }
         catch (Exception e)
         {
   //         logger.error("Error initializing GATE.", e);
            logger.warn(e.getMessage());
            savedException = e;
   //         return;
         }
      }

      try
      {
         logger.info("Creating resource {}", gateResourceName);
         resource = (AbstractLanguageAnalyser) Factory.createResource(gateResourceName);
         logger.info("Resource created.");
//         logger.debug("Initializing worker pool. Size: " + K.POOL_SIZE);
//         pool = new ArrayBlockingQueue<AbstractLanguageAnalyser>(K.POOL_SIZE);
//         for (int i = 0; i < K.POOL_SIZE; ++i)
//         {
//            pool.add((AbstractLanguageAnalyser) Factory.createResource(gateResourceName));
//         }
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
      logger.debug("Configuring {}", name);
      return DataFactory.ok();
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

//      AbstractLanguageAnalyser resource = null;
      try
      {
//         resource = pool.take();
         logger.info("Executing resource {}", name);
         resource.setDocument(doc);
         resource.execute();
      }
      catch (Exception e)
      {
         logger.error("Error running GATE resource {}", name, e);
         return new Data(Types.ERROR, e.getMessage());
      }
      finally
      {
//         pool.add(resource);
      }
      logger.info("Execution complete.");
      return new Data(Types.GATE, doc.toXml());
   }

//   public void destroy()
//   {
//      for (AbstractLanguageAnalyser resource : pool)
//      {
//         Factory.deleteResource(resource);
//      }
//   }

   protected Document getDocument(Data input) throws InternalException
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

package org.anc.lapps.gate;

import gate.*;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import org.anc.io.UTF8Reader;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.InternalException;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.experimental.annotations.CommonMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Keith Suderman
 */
@CommonMetadata(
        vendor = "http://www.anc.org",
        encoding = "UTF-8",
        language = "en",
        license = "apache2",
        format = "gate"
)
public abstract class SimpleGateService implements WebService
{
   public static final Logger logger = LoggerFactory.getLogger(SimpleGateService.class);
   public static final Configuration K = new Configuration();

   /**
    * The GATE processing resource that make up this service. For
    * example a service that runs a GATE PR followed by a Jape
    * transducer to tweak the output.
    */
   protected AbstractLanguageAnalyser[] resources = null;

   /**
    * Any exceptions thrown during initialization are saved and returned
    * to the user in a Data object whenever they call the {@link #execute(org.lappsgrid.api.Data) execute}
    * method.
    */
   protected Exception savedException;

   /**
    * A human readable name for this service.
    */
   protected String name;

   /**
    * The {@link org.lappsgrid.api.Data} object returned by the
    * {@link #getMetadata()} method.
    */
   protected Data metadata;

   /*
    * Index used when inserting resources into the resources array.
    */
   private int index = 0;

   // Error messages displayed to the user.
   /** An unexpected exception was caught. */
   protected static final String UNEXPECTED = "This is unexpected...";

   /** For compatibility with the {@link org.anc.lapps.gate.PooledGateService}. */
   protected static final String BUSY = "The service is busy. Please try again later.";

   public SimpleGateService(Class<? extends WebService> theClass)
   {
      this(theClass, 1);
   }

   public SimpleGateService(Class<? extends WebService> theClass, int size)
   {
      String jsonName = "metadata/" + theClass.getName() + ".json";
      try
      {
			logger.debug("Loading metadata from {}", jsonName);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null)
			{
				loader = SimpleGateService.class.getClassLoader();
			}
			InputStream stream = loader.getResourceAsStream(jsonName);
			if (stream == null)
			{
				logger.error("Unable to load metadata from {}", jsonName);
				metadata = DataFactory.error("Unable to load metadata from " + jsonName);
			}
			else
			{
				UTF8Reader reader = new UTF8Reader(stream);
				String json = reader.readString();
				metadata = DataFactory.meta(json);
				reader.close();
//				String json = ResourceLoader.loadString(jsonName);
			}
      }
      catch (IOException e)
      {
         metadata = DataFactory.error("Unable to load JSON metadata.");
      }

      resources = new AbstractLanguageAnalyser[size];

      synchronized (State.initialized) {
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
               logger.warn(e.getMessage());
            }

         }
      }
   }

   protected void createResource(String gateResourceName)
   {
      this.createResource(gateResourceName, Factory.newFeatureMap());
   }

   protected void createResource(String gateResourceName, FeatureMap map)
   {
      this.name = gateResourceName;
      if (savedException != null)
      {
         // Don't stomp on the save exception.
         return;
      }

      try
      {
         logger.info("Creating resources {}", gateResourceName);
         resources[index++] = (AbstractLanguageAnalyser) Factory.createResource(gateResourceName, map);
         logger.info("Resource created.");
      }
      catch (Exception e)
      {
         logger.error("Unable to create Gate resources.", e);
         savedException = e;
      }
   }

   public String getServiceId()
   {
      return this.getClass().getCanonicalName() + ":" + Version.getVersion();
   }

   public Data getMetadata()
   {
      return metadata;
   }

   @Override
   public Data configure(Data config)
   {
      return DataFactory.error("Unsupported operation.");
   }

   public Document doExecute(Data input) throws Exception
   {
      logger.debug("Executing {}", name);
      if (savedException != null)
      {
         logger.warn("Returning saved exception: " + savedException.getMessage());
//         return new Data(Uri.ERROR, getStackTrace(savedException));
         throw savedException;
      }

//      Document doc = null;
//      try
//      {
        Document doc = getDocument(input);
//      }
//      catch (InternalException e)
//      {
//         logger.error("Internal exception.", e);
//         return new Data(Uri.ERROR, getStackTrace(e));
//      }

//      Data result = null;
//      AbstractLanguageAnalyser resources = null;
//      try
//      {
//         resources = pool.take();
         for (AbstractLanguageAnalyser resource : resources)
         {
            logger.info("Executing resource {}", name);
            resource.setDocument(doc);
            resource.execute();
//            FeatureMap features = doc.getFeatures();
//            Object value = features.get(Metadata.PRODUCED_BY);
//            String producedBy = name + ":" + Version.getVersion();
//            if (value != null) {
//               producedBy = value.toString() + ", " + producedBy;
//            }
//            doc.getFeatures().put(Metadata.PRODUCED_BY, producedBy);
            resource.setDocument(null);
         }
//         String xml = doc.toXml();
//         result = new Data(Uri.GATE, xml);
//      }
//      catch (Exception e)
//      {
//         logger.error("Error running GATE resources {}", name, e);
//         return new Data(Uri.ERROR, getStackTrace(e));
//      }
//      finally
//      {
//         Factory.deleteResource(doc);
//      }
      logger.info("Execution complete.");
      return doc;
   }

   Document getDocument(Data input) throws InternalException
   {
      Document doc = null;
      try
      {
         String uri = input.getDiscriminator();
         long type = DiscriminatorRegistry.get(uri);
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

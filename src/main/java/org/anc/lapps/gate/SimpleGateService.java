package org.anc.lapps.gate;

import gate.*;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ResourceInstantiationException;
import org.anc.io.UTF8Reader;
import org.lappsgrid.api.InternalException;
import org.lappsgrid.api.WebService;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.annotations.CommonMetadata;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.vocabulary.Annotations;
import org.lappsgrid.vocabulary.Contents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 * @author Keith Suderman
 */
@CommonMetadata(
        vendor = "http://www.anc.org",
        encoding = "UTF-8",
        language = "en",
        license = "GATE Embedded is released under the `LGPL 3.0 <http://www.gnu.org/licenses/lgpl-3.0.html>`_ license.\n\nGATE Embedded may be downloaded from the `GATE website <https://gate.ac.uk/download/>`_.",
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
    * to the user whenever they call the {@link #execute execute}
    * method.
    */
   protected Exception savedException;

   /**
    * A human readable name for this service.
    */
   protected String name;

   /**
    * The JSON metadata for the service.
    */
   protected String metadata;

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
                ServiceMetadata metadata = Serializer.parse(json, ServiceMetadata.class);
				this.metadata = new Data<ServiceMetadata>(Uri.META, metadata).asJson();
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
               logger.debug("Gate home: {}", K.GATE_HOME);
               File plugins = new File(K.PLUGINS_HOME);
               if (!plugins.exists())
               {
                  logger.error("Gate plugins not found: {}", plugins.getPath());
                  savedException = new FileNotFoundException(K.PLUGINS_HOME);
                  return;
               }
               logger.debug("Plugins home: {}", K.PLUGINS_HOME);
               File siteConfig = new File(K.SITE_CONFIG);
               if (!siteConfig.exists())
               {
                  logger.error("Site config not found: {}", siteConfig.getPath());
                  savedException = new FileNotFoundException(K.SITE_CONFIG);
                  return;
               }
               logger.debug("Site config: {}", K.SITE_CONFIG);
               File userConfig = new File(K.USER_CONFIG);
               if (!userConfig.exists())
               {
                  logger.error("User config not found: {}", userConfig.getPath());
                  savedException = new FileNotFoundException(K.USER_CONFIG);
                  return;
               }
               logger.debug("User config: {}", K.USER_CONFIG);
               Gate.setGateHome(gateHome);
               Gate.setSiteConfigFile(siteConfig);
               Gate.setPluginsHome(plugins);
               Gate.setUserConfigFile(userConfig);

               try
               {
                  logger.debug("Initializing GATE");
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

   public String getMetadata()
   {
      return metadata;
   }

   public String getProducer() {
   		return this.getClass().getName() + "_" + Version.getVersion();
   }

   public Document doExecute(String input, FeatureMap features) throws Exception
   {
	   logger.debug("Executing {}", name);
	   if (savedException != null)
	   {
		   logger.warn("Returning saved exception: " + savedException.getMessage());
		   throw savedException;
	   }

	   Document doc = getDocument(input);
	   for (AbstractLanguageAnalyser resource : resources)
	   {
		   logger.info("Executing resource {}", name);
		   resource.setDocument(doc);
		   resource.setFeatures(features);
		   resource.execute();
		   resource.setDocument(null);
		   resource.setFeatures(null);
	   }
	   return doc;
   }

   public Document doExecute(String input, String annotationType) throws Exception
   {
      return doExecute(input, annotationType, null);
   }

   public Document doExecute(String input, String annotationType, FeatureMap features) throws Exception
   {
      Document doc = doExecute(input, features);
      features = doc.getFeatures();
      Integer step = (Integer) features.get("lapps:step");
      if (step == null) {
         step = 1;
      }
      features.put("lapps:step", step + 1);
      features.put("lapps:" + annotationType, step + " " + getProducer() + " gate");

      logger.info("Execution complete.");
      return doc;
   }

   Document getDocument(String input) throws InternalException
   {
		Data<String> data = Serializer.parse(input, Data.class);
      Document doc = null;
      try
      {
         String uri = data.getDiscriminator();
         if (uri.equals(Uri.TEXT))
         {
            logger.info("Creating document from text.");
            doc = Factory.newDocument(data.getPayload());
         }
         else if (uri.equals(Uri.LAPPS)) {
			DataContainer dc = Serializer.parse(input, DataContainer.class);
			Container container = dc.getPayload();
			 doc = Factory.newDocument(container.getText());
         }
         else if (uri.equals(Uri.GATE) || uri.equals(Uri.XML))
         {
            logger.info("Creating document from GATE/XML document.");
            doc = (Document)
                    Factory.createResource("gate.corpora.DocumentImpl",
                            Utils.featureMap(gate.Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME,
                                    data.getPayload(),
                                    gate.Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, "text/xml"));
         }
         else
         {
//            String name = DiscriminatorRegistry.get(type);
            throw new InternalException("Unknown document type : " + uri);
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

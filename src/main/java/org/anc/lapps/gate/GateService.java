package org.anc.lapps.gate;



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
   public static final long TEXT = Types.TEXT;
   public static final long DOCUMENT = Types.GATE;
   public static final long SENTENCE = Types.SENTENCE;
   public static final long TOKEN = Types.TOKEN;
   public static final long POS = Types.POS;
   
   public static final long OK = Types.OK;
   public static final long ERROR = Types.ERROR;
   
   protected AbstractLanguageAnalyser resource;
   protected Exception savedException;
   
   public GateService(String gateResourceName)
   {
      try
      {
         Gate.init();
         resource = (AbstractLanguageAnalyser) Factory.createResource(gateResourceName);
      }
      catch (Exception e)
      {
         savedException = e;
      }   
   }
   
   @Override
   public Data configure(Data config)
   {
      return DataFactory.ok();
   }
   
   @Override
   public Data execute(Data input)
   {
      if (savedException != null)
      {
         return new Data(ERROR, savedException.getMessage());
      }
      
      Document doc;
      try
      {
         doc = getDocument(input);
      }
      catch (InternalException e)
      {
         // TODO This error should be logged.
         return new Data(ERROR, e.getMessage());
      }
      
      resource.setDocument(doc);
      try
      {
         resource.execute();
      }
      catch (ExecutionException e)
      {
         // TODO This error should be logged.
         return new Data(ERROR, e.getMessage());
      }
      return new Data(DOCUMENT, doc.toXml());
   }

   protected Document getDocument(Data input) throws InternalException
   {
      Document doc = null;
      try
      {
         if (input.getDiscriminator() == TEXT)
         {
            doc = Factory.newDocument(input.getPayload());
         }
         else if (input.getDiscriminator() == DOCUMENT)
         {
            doc = (Document) 
                  Factory.createResource("gate.corpora.DocumentImpl", 
                    Utils.featureMap(gate.Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, 
                  input.getPayload(), 
                  gate.Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, "text/xml")); 
         }
      }
      catch (ResourceInstantiationException ex)
      {
         throw new InternalException("Unable to parse Gate document", ex);
      }
      return doc;
   }
}

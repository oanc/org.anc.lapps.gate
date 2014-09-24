package org.anc.lapps.gate;

import org.anc.io.UTF8Writer;
import org.junit.Test;
import org.lappsgrid.api.*;
import org.lappsgrid.client.DataSourceClient;
import org.lappsgrid.discriminator.Uri;

import javax.xml.rpc.ServiceException;
import java.io.*;


public class GateDriver
{
   
   public GateDriver()
   {
   }

   @Test
   public void run() throws ServiceException, InternalException, IOException
   {
      File destination = new File("/tmp/gate");
      if (!destination.exists())
      {
         if (!destination.mkdirs())
         {
            System.out.println("Unable to create destination directory.");
            return;
         }
      }

      SentenceSplitter splitter = new SentenceSplitter();
      Tokenizer tokenizer = new Tokenizer();
      Tagger tagger = new Tagger();

      String url = "http://localhost:8080/service_manager/invoker/lapps:MASC_TEXT";
      String username = "operator1";
      String password = "operator1";
      DataSourceClient masc = new DataSourceClient(url, username, password);
      Data keyData = masc.list();
      if (Uri.ERROR.equals(keyData.getDiscriminator()))
      {
         System.out.println("Unable to get index from data source.");
         return;
      }
      String[] keys = keyData.getPayload().split("\\s+");
      for (String key : keys)
      {
         System.out.println("Processing " + key);
         Data data = masc.get(key);
         System.out.println("   splitting");
         data = splitter.execute(data);
         System.out.println("   tokenizing");
         data = tokenizer.execute(data);
         System.out.println("   tagging");
         data = tagger.execute(data);
         File outputFile = new File(destination, key + ".xml");
         UTF8Writer writer = new UTF8Writer(outputFile);
         writer.write(data.getPayload());
         writer.close();
         System.out.println();
      }
//      DataSourceReader reader = new DataSourceReader();
//      
//      String text = reader.next();
//      System.out.println("The text is: " + text);
//      Data data = new Data(DiscriminatorRegistry.get("text"), text);
//      System.out.println("Running the splitter.");
//      splitter.execute(data);
//      System.out.println("Running the tokenizer.");
//      tokenizer.execute(data);
//      System.out.println("Running the tagger.");
//      tagger.execute(data);
//      System.out.println(data.getPayload());
   }
   
//   public static void main(String[] args)
//   {
//      try
//      {
//         new GateDriver().run();
//      }
//      catch (Exception e)
//      {
//         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//      }
//   }
}

package org.anc.lapps.gate;

import org.anc.io.UTF8Writer;
import org.junit.Test;
import org.lappsgrid.api.*;
import org.lappsgrid.client.DataSourceClient;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import static org.lappsgrid.discriminator.Discriminators.Uri;

import javax.xml.rpc.ServiceException;
import java.io.*;
import java.util.List;


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

//      String url = "http://localhost:8080/service_manager/invoker/lapps:MASC_TEXT";
		String url = "http://localhost:9080/MascDataSource/2.0.0-SNAPSHOT/services/MascTextSource";
      String username = "operator1";
      String password = "operator1";
      DataSourceClient masc = new DataSourceClient(url, null, null);
		masc.setToken("123abc");
//		String json = DataFactory.list();
//		json = masc.execute(json);
//		Data<String> data = Serializer.parse(json, Data.class);
//		if (Uri.ERROR.equals(data.getDiscriminator())) {
//			System.out.println("ERROR: " + data.getPayload());
//			return;
//		}
//		System.out.println("Response is a " + data.getDiscriminator());
//		if (true) return;

		List<String> keys = masc.list(1,2);
      if (keys == null || keys.size() == 0)
      {
         System.out.println("Unable to get index from data source.");
         return;
      }
//      String[] keys = keyData.getPayload().split("\\s+");
		String json =  null;
      for (String key : keys)
      {
         System.out.println("Processing " + key);
         json = masc.get(key);
         System.out.println("   splitting");
         json = splitter.execute(json);
         System.out.println("   tokenizing");
         json = tokenizer.execute(json);
         System.out.println("   tagging");
         json = tagger.execute(json);
         File outputFile = new File(destination, key + ".xml");
         UTF8Writer writer = new UTF8Writer(outputFile);
			Data data = Serializer.parse(json, Data.class);
         writer.write(data.getPayload().toString());
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

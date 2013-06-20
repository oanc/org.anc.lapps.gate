package org.anc.lapps.gate;

import org.anc.lapps.gate.splitter.SentenceSplitter;
import org.anc.lapps.gate.tagger.Tagger;
import org.anc.lapps.gate.tokenizer.Tokenizer;


public class GateDriver
{
   
   public GateDriver()
   {
   }

   public void run()
   {
      SentenceSplitter splitter = new SentenceSplitter();
      Tokenizer tokenizer = new Tokenizer();
      Tagger tagger = new Tagger();
      
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
   
   public static void main(String[] args)
   {
      new GateDriver().run();
   }
}

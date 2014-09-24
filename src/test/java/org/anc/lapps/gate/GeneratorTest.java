package org.anc.lapps.gate;

import org.junit.Test;
import org.lappsgrid.metadata.AnnotationType;
import org.lappsgrid.metadata.ContentType;
//
import static org.lappsgrid.metadata.AnnotationType.*;
import static org.lappsgrid.metadata.ContentType.*;

/**
 * @author Keith Suderman
 */
public class GeneratorTest
{
   public GeneratorTest()
   {

   }

   public void run()
   {
      System.out.println(ContentType.GATE.toString());
   }
   public static void main(String[] args)
   {
      new GeneratorTest().run();
   }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moneydance.modules.features.mdcsvimporter;

import com.moneydance.modules.features.mdcsvimporter.DateGuesser;
import com.moneydance.modules.features.mdcsvimporter.CSVReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miki
 */
public class DateGuesserTest
{
   public DateGuesserTest()
   {
   }

   @BeforeClass
   public static void setUpClass()
      throws Exception
   {
   }

   @AfterClass
   public static void tearDownClass()
      throws Exception
   {
   }

   @Before
   public void setUp()
   {
   }

   @After
   public void tearDown()
   {
   }

   /**
    * Test of class DateGuesser.
    */
   @Test
   public void testCheckDateString()
      throws IOException
   {
      Reader file = new InputStreamReader(
         DateGuesserTest.class.getResourceAsStream( "dateGuesser.csv" ) );
      CSVReader reader = new CSVReader( file );
      DateGuesser guesser = new DateGuesser();

      while ( reader.nextLine() ) {
         for ( String s = reader.nextField(); s != null; s = reader.nextField() ) {
            guesser.checkDateString( s );
         }
      }

      guesser.getBestFormatProbability();
   }
}
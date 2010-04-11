/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.moneydance.modules.features.mdcsvimporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miki
 */
public final class Settings
{
   private static File getFilename()
   {
      File moneydanceHome = new File( System.getProperty( "user.home" ), ".moneydance" );
      return new File( moneydanceHome, "mdcsvimporter.props" );
   }

   private static Properties load()
      throws IOException
   {
      Properties retVal = new Properties();
      InputStream is;
      try
      {
         is = new FileInputStream( getFilename() );
      }
      catch ( FileNotFoundException ex )
      {
         return retVal; // no file is normal condition to start with empty props object
      }
      try
      {
         retVal.load( is );
      }
      finally
      {
         is.close();
      }
      return retVal;
   }

   private static void save( Properties props )
      throws IOException
   {
      OutputStream os = new FileOutputStream( getFilename() );
      try
      {
         props.store( os, "MDCSVImporter - Moneydance CSV Importer" );
      }
      finally
      {
         os.close();
      }
   }

   public static String get( String name )
   {
      try
      {
         return load().getProperty( name );
      }
      catch ( IOException ex )
      {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
         return null;
      }
   }

   public static String get( String name, String defaultValue )
   {
      try
      {
         String retVal = load().getProperty( name );
         if ( retVal == null )
         {
            return defaultValue;
         }
         return retVal;
      }
      catch ( IOException ex )
      {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
         return defaultValue;
      }
   }

   public static void set( String name, String value )
   {
      try
      {
         Properties props = load();

         // skip if values match (I am sorry for not optimizing the condition, it is early morning...)
         String oldValue = props.getProperty( name );
         if ( (oldValue != null && oldValue.equals( value )) ||
            (value != null && value.equals( oldValue )) )
         {
            return;
         }

         props.setProperty( name, value );
         save( props );
      }
      catch ( IOException ex )
      {
         Logger.getLogger( Settings.class.getName() ).log( Level.SEVERE, null, ex );
      }
   }

   public static boolean getBoolean( String name )
   {
      return getBoolean( name, false );
   }

   public static boolean getBoolean( String name, boolean defaultValue )
   {
      String value = get( name );
      if ( value == null )
      {
         return defaultValue;
      }

      if ( value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "yes" ) ||
         value.equalsIgnoreCase( "1" ) )
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   public static void setBoolean( String name, boolean value )
   {
      set( name, value ? "true" : "false" );
   }

   public static void setYesNo( String name, boolean value )
   {
      set( name, value ? "yes" : "no" );
   }

   public static int getInteger( String name )
   {
      return getInteger( name, 0 );
   }

   public static int getInteger( String name, int defaultValue )
   {
      String value = get( name );
      if ( value == null )
      {
         return defaultValue;
      }

      return Integer.parseInt( value );
   }

   public static void setInteger( String name, int value )
   {
      set( name, Integer.toString( value ) );
   }
}

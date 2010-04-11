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

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author miki
 */
public class CSVData
{
   private String[][] data;
   private int currentLineIndex = -1;
   private int currentFieldIndex = -1;

   public CSVData( CSVReader reader )
      throws IOException
   {
      ArrayList<String> line = new ArrayList<String>();
      ArrayList<String[]> file = new ArrayList<String[]>();

      while ( reader.nextLine() )
      {
         for ( String s = reader.nextField(); s != null; s = reader.nextField() )
         {
            line.add( s );
         }

         String[] newLine = new String[ line.size() ];
         line.toArray( newLine );
         file.add( newLine );
         line.clear();
      }

      data = new String[file.size()][];
      file.toArray( data );
   }

   public void reset()
   {
      currentLineIndex = -1;
      currentFieldIndex = -1;
   }

   public boolean nextLine()
   {
      if ( currentLineIndex < data.length )
      {
         ++currentLineIndex;
         currentFieldIndex = -1;
      }

      return currentLineIndex < data.length;
   }

   public boolean nextField()
   {
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
         return false;
      }

      if ( currentFieldIndex < data[currentLineIndex].length )
      {
         ++currentFieldIndex;
      }

      return currentFieldIndex < data[currentLineIndex].length;
   }

   public String getField()
   {
      if ( currentLineIndex < 0 || currentLineIndex >= data.length )
      {
         return null;
      }
      if ( currentFieldIndex < 0 || currentFieldIndex >= data[currentLineIndex].length )
      {
         return null;
      }

      return data[currentLineIndex][currentFieldIndex];
   }
}

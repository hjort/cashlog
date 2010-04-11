/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
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

import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.apps.md.model.RootAccount;
import java.awt.Image;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author miki
 */
public class Main
   extends FeatureModule
{
   private static final int VERSION = 10;
   private static final String NAME = "CSV Importer";
   private static final String VENDOR = "Milutin Jovanović";
   private static final String URL = "http://code.google.com/p/mdcsvimporter/";
   private static final String DESCRIPTION =
      "Moneydance CSV Importer Plug-In version BETA " + Integer.toString( VERSION ) +
      ". To report problems or make suggestions please go to the web side below.\n\n" +
      "This software is distributed under GNU Lesser General Public License (see " +
      "http://www.gnu.org/licenses/ for details). If you continue, you acknowledge " +
      "accepting terms of this license.";
   private static Image image;

   {
      try
      {
         image = ImageIO.read(
            Main.class.getResourceAsStream( "import.png" ) );
      }
      catch ( IOException x )
      {
         // ignore error; nothing we can do about it
      }
   }

   public Main()
   {
   }

   @Override
   public void init()
   {
      FeatureModuleContext context = getContext();
      if ( context == null )
      {
         return;
      }

      context.registerFeature( this, "import", image, "Import CSV File" );
   }

   RootAccount getRootAccount()
   {
      FeatureModuleContext context = getContext();
      return context.getRootAccount();
   }

   JFrame getMoneydanceWindow()
   {
      // Using undocumented feature. This way our windows and dialogs can have a parent,
      // and behave more conformingly. Alternative is just returning null. Effects should
      // be minor visual inconsistencies.

      FeatureModuleContext context = getContext();
      com.moneydance.apps.md.controller.Main main =
         (com.moneydance.apps.md.controller.Main) context;
      if ( main == null )
      {
         return null;
      }
      com.moneydance.apps.md.view.gui.MoneydanceGUI gui =
         (com.moneydance.apps.md.view.gui.MoneydanceGUI) main.getUI();
      if ( gui == null )
      {
         return null;
      }
      return gui.getTopLevelFrame();
   }

   @Override
   public String getName()
   {
      return NAME;
   }

   @Override
   public int getBuild()
   {
      return VERSION;
   }

   @Override
   public String getDescription()
   {
      return DESCRIPTION;
   }

   @Override
   public void invoke( String uri )
   {
      StringTokenizer tokenizer = new StringTokenizer( uri, ":" );

      int count = tokenizer.countTokens();
      String url = count + " tokens(";
      while ( tokenizer.hasMoreTokens() )
      {
         url = url.concat( tokenizer.nextToken() );
      }
      url += ")";

      ImportDialog dialog = new ImportDialog( this );
      dialog.setLocationRelativeTo( null );
      dialog.setVisible( true );
   }

   @Override
   public String getVendorURL()
   {
      return URL;
   }

   @Override
   public String getVendor()
   {
      return VENDOR;
   }

   @Override
   public Image getIconImage()
   {
      return image;
   }
}

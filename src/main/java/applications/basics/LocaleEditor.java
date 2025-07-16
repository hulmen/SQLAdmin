/*
 * @(#)LocaleEditor.java 
 *
 * Copyright 1998 Kai Toedter
 */

package applications.basics;

import java.beans.*;
import java.util.*;

/**
 * Property editor for locales.
 *
 * @version 1.0 10/10/98
 * @author  Kai Toedter
 */
public class LocaleEditor extends java.beans.PropertyEditorSupport
{
   /**
    * Default LocaleEditor constructor.
    */
   public LocaleEditor()
   {
      locale  = Locale.getDefault();
      locales = Calendar.getInstance().getAvailableLocales();
      length  = locales.length;
      localeStrings = new String[length];
   }

   /**
    * Returns the locale Strings.
    */
   public String[] getTags()
   {
      for( int i=0; i<length; i++ )
	 localeStrings[i] = locales[i].getDisplayName();
      return localeStrings;
   }

   /**
    * Sets the locale Strings as text and invokes setValue( locale ).
    */
   public void setAsText( String text ) throws IllegalArgumentException
   {
      for( int i=0; i<length; i++ )
	 if( text.equals( locales[i].getDisplayName() ) )
	 {
	    locale = locales[i];
	    setValue( locale );
	    break;
	 }
   }

   /**
    * Returnss the locale String as text.
    */
   public String getAsText()
   {
      return locale.getDisplayName();
   }

   private Locale[] locales;
   private String[] localeStrings;
   private Locale   locale;
   private int      length;
}


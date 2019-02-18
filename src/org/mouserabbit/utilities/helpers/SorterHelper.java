/*--------------------------------------------------------------------------
     SorterHelper.java
     C	Apr 27 1999
     U	Nov 03 2003 .3

     Mar 06 2002    Integrated into CVS
     Nov 03 2003    Package name change
     Feb 18 2019    VScode integration
--------------------------------------------------------------------------*/
package org.mouserabbit.utilities.helpers;

import java.text.Collator;
import java.text.CollationKey;
import java.util.Locale;

/**
    This class defines a bunch of static methods for efficiently sorting
    arrays of Strings or other objects. It also defines two interfaces that
    provide two different ways of comparing objects to be sorted
*/
public class SorterHelper
{
    /**===============================================================================
         Comparer :
         This interface defines the compare() method used to compare two objects
         To sort objects of a given type, you must provide an appropriate
         comparer object with a compare() method that orders those objects as desired
    ===============================================================================*/
    public static interface Comparer {
         /**
              Compare objects and return a value that indicates their relative order
              a > b     return > 0
              a == b    return 0
              a < b     return < 0
         */
         public int compare(Object a, Object b);
    }
    /**===============================================================================
         Comparable :
         This is an alternative interface that can be used to order objects. if a
         class implements this Comparable interface, then any two instances of that
         class can be directly compared by invoking the compareTo() method.
    ===============================================================================*/
    public static interface Comparable
    {
         /**
              Compare objects and return a value that indicates their relative order
              this > other     return > 0
              this == other    return 0
              this < other     return < 0
         */
         public int compareTo(Object other);

    }
    /**===============================================================================
         ascii_comparer :
         This is an internal Comparer object. ( created with an anonymous class )
         that compares two ASCII strings.
         It is used in the sortAscii methods below.
    ===============================================================================*/
    private static Comparer ascii_comparer = new Comparer()
    {
         public int compare(Object a, Object b)
         {
              return ((String)a).compareTo((String)b);
         }
    };
    /**===============================================================================
         comparable_comparer :
         Another internal Comparer object. Used to compare two comparable objects
         It is used by the sort() method below that take comparable objects as
         arguments instead of arbitrary objects.
    ===============================================================================*/
    private static Comparer comparable_comparer = new Comparer()
    {
         public int compare(Object a, Object b)
         {
              return ((Comparable)a).compareTo(b);
         }
    };
    /** Sort an array of ASCII strings in ascending order */
    public static void sortAscii(String[] a)
    {
         // Use the ascii comparer object
         sort(a, null, 0, a.length-1, true, ascii_comparer);
    }
    /**===============================================================================
         Sort a portion of an array of ASCII strings in ascending
         or descending order
    ===============================================================================*/
    public static void sortAscii(String[] a, int from, int to, boolean up)
    {
         // Use the ascii comparer object
         sort(a, null, from, to, up, ascii_comparer);
    }
    /**===============================================================================
         Sort an array of ASCII strings in ascending ignoring case
    ===============================================================================*/
    public static void sortAsciiIgnoreCase(String[] a)
    {
         // Use the ascii comparer object
         sortAsciiIgnoreCase(a, 0, a.length-1,true);
    }
    /**===============================================================================
         Sort a portion of an array of ASCII strings ignoring case in ascending
         or descending order
    ===============================================================================*/
    public static void sortAsciiIgnoreCase(String[] a, int from, int to, boolean up)
    {
         if((a == null) || (a.length < 2)) return;
         // Create a second array of string that contains lowercase versions
         // of all the specified strings
         String b[] = new String[a.length];
         for(int i = 0; i < a.length; i++) b[i] = a[i].toLowerCase();
         // Sort that array and rearrange the original array
         // in excactly the same way
         // We use the ascii_comparer object
         sort(b,a,from,to,up,ascii_comparer);
    }
    /**===============================================================================
         Sort an array of ASCII strings into ascending order using the correct collation
         order for the default locale
    ===============================================================================*/
    public static void sort(String[] a)
    {
         sort(a,0,a.length-1,true, false, null);
    }
    /**===============================================================================
         Sort a portion of an array of ASCII strings using the correct collation
         order for the default locale.
         If up is true, sort ascending, else descending.
         If ignore case is true, ignore capitalization.
    ===============================================================================*/
    public static void sort(String[] a, int from, int to, boolean up, boolean ignorecase)
    {
         sort(a,from, to,up, ignorecase, null);
    }
    /**===============================================================================
         Sort a portion of an array of ASCII strings using the correct collation
         order for the specified locale.
         If up is true, sort ascending, else descending.
         If ignore case is true, ignore capitalization.
    ===============================================================================*/
    public static void sort(String[] a, int from, int to,
                             boolean up, boolean ignorecase, Locale locale)
    {
         // Don't sort if we don't have to
         if((a == null) || (a.length < 2)) return;
         // The java.text.Collator object does internationaled string compares
         // Create one for the specified or the default locale
         Collator c;
         if(locale == null) c = Collator.getInstance();
         else c = Collator.getInstance(locale);
         // Specify whether or not case should be taken into account.
         // Beware of problems with JDK 1.1 using the dfault american locale
         if(ignorecase) c.setStrength(Collator.SECONDARY);
         // use the collator object to create an array of collation key objects
         // that correspond to each of the strings. Comparing collation keys is
         // much quicker than the strings.
         CollationKey[] b = new CollationKey[a.length];
         for(int i = 0; i < a.length; ++i) b[i] = c.getCollationKey(a[i]);
         // Now define a comparer object to compare collation keys, using an
         // anonymous class
         Comparer comp = new Comparer()
         {
              public int compare(Object a, Object b)
              {
                   CollationKey xx = (CollationKey)a;
                   CollationKey yy = (CollationKey)b;
                   return xx.compareTo(yy);
              }
         };
         // Finally, sort the array of CollationKey objects, rearranging the
         // original array of strings in exactly the same way
         sort(b,a,from,to,up,comp);
    }
    /**===============================================================================
         Sort a portion of an array of Comparable objects into ascending or
         descending order.
    ===============================================================================*/
    public static void sort(Comparable[] a, int from, int to, boolean up)
    {
         sort(a, null, from, to, up, comparable_comparer);
    }
    /**===============================================================================
         Sort a portion of an array of Comparable objects into ascending or
         descending order.
         Rearrange array b in exactly the same way as array a.
    ===============================================================================*/
    public static void sort(Comparable[] a, Object[] b, int from, int to, boolean up)
    {
         sort(a, b, from, to, up, comparable_comparer);
    }
    /**===============================================================================
         Sort an array of arbitrary objects into ascending order using the
         comparison defined by the comparer object c
    ===============================================================================*/
    public static void sort(Object[] a, Comparer c)
    {
         sort(a, null, 0, a.length-1, true, c);
    }
    /**===============================================================================
         Sort a portion of an array of arbitrary objects into ascending or descending
         order using the comparison defined by the comparer object c
    ===============================================================================*/
    public static void sort(Object[] a, int from, int to, boolean up, Comparer c)
    {
         sort(a, null, from, to, up, c);
    }
    /**===============================================================================
         The main sort.
         It performs a quick sort on the elements of array in the start/end range
         specified. The up argument specifies whether to sort in asc or desc. The
         comparer argument c is used to perform comparisons between elements of
         the array. The elements of the array b are reordered in exactly the same way
         as the elements of array a are.
    ===============================================================================*/
    public static void sort(Object[] a, Object[] b,
                              int from, int to, boolean up, Comparer c)
    {
         // If there is nothing to sort, return
         if((a == null) || (a.length < 2)) return;
         // The basic quick sort routine. You should understand what the code does
         // but don't have to understand just why it is guaranteed to sort the array
         // Note the use of the compare() method of the Comparer object.
         int i = from, j = to;
         Object center = a[(from + to) /2];
         do
         {
              if(up) {  // Ascending
                   while((i < to) && (c.compare(center, a[i]) > 0)) i++;
                   while((j > from) && (c.compare(center, a[j]) < 0)) j--;
              }
              else {
                   while((i < to) && (c.compare(center, a[i]) < 0)) i++;
                   while((j > from) && (c.compare(center, a[j]) > 0)) j--;
              }
              if(i < j) {
                   Object tmp = a[i]; a[i] = a[j]; a[j] = tmp; // Swap
                   if(b != null) { tmp = b[i]; b[i] = b[j]; b[j] = tmp;}
              }
              if( i <= j) { i++; j--; }
         }while (i <= j);
         if(from < j) sort(a, b, from , j, up, c); // Recursively sort the rest
         if(i < to) sort(a, b, i , to , up, c);
    }
}




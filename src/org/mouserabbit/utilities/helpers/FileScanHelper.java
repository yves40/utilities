/*--------------------------------------------------------------------------------------------------
	FileScanHelper.java
	C	Jun 02 2000
	U	Nov 03 2003 .6

  Jun 02 2000    Initial.
  Jun 03 2000    Bug on mono file scan. Add directory support.
                 Directories: Only files are reported
  Jun 05 2000    Change a few comments.
  Jun 06 2000    Debug the directory scan with a full specification.
                 The list method build a string array containing only the relative
                 file names.
  Mar 06 2002    Integrated into CVS
  Nov 03 2003    Chnaged package name
  Feb 18 2019    VScode integration
---------------------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.helpers;

import java.io.*;


public class FileScanHelper  {

     protected           String         inputfilename = null;
     protected           File           filetype;
     protected           FilenameFilter filter;
     protected           String[]       files = null;
     protected           int            filecount;
     protected           int            currentindex = -1;
     protected           boolean        bTrace = false;
     //==========================================================================================
     // Constructor
     // inputfilename can be :
     //        > a directory specification. All files in the directory are returned, including
     //          sub directories
     //        > A filename
     //        > A filename with wildcards. (Directories returned)
     //==========================================================================================
     public FileScanHelper(String inputfilename) throws Exception {
          this.inputfilename = inputfilename;
          //=====================================================================================
          // Analyze the given file path. Does it contains wildcards ?
          //=====================================================================================
          File target = new File(new String(inputfilename));
          // Is the target a directory ?
          if(!target.isDirectory()){
               File tt = new File(target.getName()); // get the only or first file
               String abs = target.getAbsolutePath();
               int nIndex = abs.indexOf("*");    // Wildcard ??
               if(nIndex == -1){
                        files = new String[1];
                        files[0] = abs;
                        filecount = 1;
                        currentindex = 0;
               }
               else {
                    final String endwith = abs.substring(nIndex + 1);
                    abs = abs.substring(0, nIndex); // Get the directory path
                    // Is it really the final directory path ?
                    // YES if user specified dir/dir/dir/*.ldif
                    // NO if user specified dir/dir/dir/us*.ldif
                    if(abs.endsWith(System.getProperty("file.separator"))) {
                              File directorytoscan = new File(abs);
                              filter = new FilenameFilter()
                              {
                                   public boolean accept(File dir, String name)
                                   {
                                        if(name.endsWith(endwith)) return true;
                                        else return false;
                                   }
                              };
                              files = directorytoscan.list(filter);
                              FinalizeTheList(files, abs);
                    }
                    else {
                              // Isolate the final filename string
                              int nLast = abs.lastIndexOf(System.getProperty("file.separator"));
                              final String startwith = abs.substring(nLast + 1);
                              abs = abs.substring(0, nLast + 1);
                              File directorytoscan = new File(abs);
                              filter = new FilenameFilter()
                              {
                                   public boolean accept(File dir, String name)
                                   {
                                        if(name.endsWith(endwith) && name.startsWith(startwith)) return true;
                                        else return false;
                                   }
                              };
                              files = directorytoscan.list(filter);
                              FinalizeTheList(files, abs);
                    }
               }
          }
          else { // Here we are processing directly the whole directory content
               files = target.list();
               SorterHelper.sort(files);
               filecount = files.length;
               currentindex = 0;
          }
     }    // End constructor
     //==========================================================================================
     // Some internal methods
     //==========================================================================================
     //
     //   Prepare the file list for its final version, sort and add the full path information
     //
     private void FinalizeTheList(String[] thelist, String abs) {
          SorterHelper.sort(files);
          filecount = files.length;
          for(int nLoop = 0; nLoop < files.length; ++nLoop) {
               files[nLoop] = abs + files[nLoop];
          }
          currentindex = 0;
     }
     //==========================================================================================
     // Some Basic methods
     //==========================================================================================
     public int getCount() {
          return filecount;
     }
     public boolean HasMoreElements() {
          if(currentindex != -1 && currentindex < filecount)
               return true;
          else
               return false;
     }
     public File NextFile() {
          if(currentindex != -1 && currentindex < filecount)
               return new File(files[currentindex++]);
          else
               return null;
     }
}


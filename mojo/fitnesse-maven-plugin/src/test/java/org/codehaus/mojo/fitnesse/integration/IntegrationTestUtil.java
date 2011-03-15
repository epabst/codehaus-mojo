package org.codehaus.mojo.fitnesse.integration;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.codehaus.mojo.fitnesse.FileUtil;

/***************************************************************************
 * Copyright 2005 Philippe Kernevez All rights reserved.                   *
 * Please look at license.txt for more license detail.                     *
 **************************************************************************/
public class IntegrationTestUtil
{
  private static final String PATH ="target/test-classes/integration/";
  
    private static void checkReportFile(File pRoot, boolean pIndexFile, String pSuiteName, String pSuitePageName, String pExpectedResult )
    throws IOException
{ 
   //  Check that the Maven site has the FitNesse menu in its reports
      String tPath = pRoot.getAbsolutePath()+File.separator+"target"+File.separator+"site"+File.separator;
     File tCheckFile = new File( tPath+"project-reports.html" );
     Assert.assertTrue( "Reports list not created", tCheckFile.exists() );
     String tResult = FileUtil.getString( tCheckFile );
     if ( pIndexFile )
     {
         Assert.assertTrue( "FitNesse report hasn't be had to the Maven site",
     tResult.contains( "<a href=\"fitnesse/index.html\">Fitnesse report</a>" ) );
     File tIndexFile = new File(tPath+"fitnesse"+File.separator+"index.html" );
     Assert.assertTrue( "The index file hasn't be created", tIndexFile.exists() );
     }
     else
     {
         Assert.assertTrue( "FitNesse report hasn't be had to the Maven site",
     tResult.contains( "<a href=\"fitnesse/fitnesseResult_localhost_FitnesseIntegrationTest."
     + pSuitePageName + "\">Fitnesse report</a>" ) );
     }
     tCheckFile =
     new File( tPath+"fitnesse"+File.separator+"fitnesseResult_localhost_FitnesseIntegrationTest."
     + pSuitePageName );
     Assert.assertTrue( "This file doesn't exist " + tCheckFile.getAbsolutePath(), tCheckFile.exists() );
     tResult = FileUtil.getString( tCheckFile );
     Assert.assertTrue( "FitNesse result aren't valid in the final report", tResult.contains( pExpectedResult ) );
}


}

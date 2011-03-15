import org.codehaus.mojo.fitnesse.integration.IntegrationTestUtil;
import org.codehaus.mojo.fitnesse.integration.FitnesseServerManager;

FitnesseServerManager.stopServer( context.fitnessProcess )
IntegrationTestUtil.checkReportFile( basedir,   true, "multiproject", "SuiteCoverage.html", "3 right, 0 wrong, 0 ignored, 0 exceptions" );
IntegrationTestUtil.checkReportFile( basedir,   true, "multiproject", "SuiteCoverage3.html", "4 right, 0 wrong, 0 ignored, 0 exceptions" );
return true
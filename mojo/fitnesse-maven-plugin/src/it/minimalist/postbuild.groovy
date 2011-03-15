import org.codehaus.mojo.fitnesse.integration.IntegrationTestUtil;
import org.codehaus.mojo.fitnesse.integration.FitnesseServerManager;

FitnesseServerManager.stopServer( context.fitnessProcess )
IntegrationTestUtil.checkReportFile( basedir, false, "minimalist", "SuiteCoverage4.html", "4 right, 0 wrong, 0 ignored, 0 exceptions" );
return true
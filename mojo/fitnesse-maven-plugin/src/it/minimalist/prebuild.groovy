import org.codehaus.mojo.fitnesse.integration.IntegrationTestUtil;
import org.codehaus.mojo.fitnesse.integration.FitnesseServerManager;

def parent = basedir.getParentFile().getName()
def version = parent.substring(0, parent.indexOf("-"))
def port = parent.substring(parent.indexOf("-")+1,parent.size())
def tFitnessProcess= FitnesseServerManager.startServer(  basedir, version, port  )
context.fitnessProcess = tFitnessProcess

return tFitnessProcess!=null
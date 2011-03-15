for ( String filename in[ 
	"target/foo/WEB-INF/lib/classworlds-1.1-alpha-2.jar", 
	"target/foo/WEB-INF/lib/junit-3.8.1.jar", 
	"target/foo/WEB-INF/lib/maven-artifact-2.0.6.jar",
	"target/foo/WEB-INF/lib/maven-artifact-manager-2.0.6.jar",
  	"target/foo/WEB-INF/lib/maven-model-2.0.6.jar",
   	"target/foo/WEB-INF/lib/maven-plugin-api-2.0.6.jar",
   	"target/foo/WEB-INF/lib/maven-plugin-registry-2.0.6.jar",
   	"target/foo/WEB-INF/lib/maven-profile-2.0.6.jar",
   	"target/foo/WEB-INF/lib/maven-project-2.0.6.jar",
   	"target/foo/WEB-INF/lib/maven-repository-metadata-2.0.6.jar",
   	"target/foo/WEB-INF/lib/maven-settings-2.0.6.jar",
   	"target/foo/WEB-INF/lib/plexus-container-default-1.0-alpha-9-stable-1.jar",
   	"target/foo/WEB-INF/lib/plexus-utils-1.5.6.jar",
   	"target/foo/WEB-INF/lib/wagon-provider-api-1.0-beta-2.jar",
   	"target/foo/WEB-INF/lib/xml-resolver-1.2.jar"] ) {
	def file = new File( basedir, filename )
	assert !file.exists()
	return true
}

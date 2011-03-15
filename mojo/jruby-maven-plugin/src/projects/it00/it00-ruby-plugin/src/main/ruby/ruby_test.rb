require 'fileutils'

# This is a mojo descripton
# 
# @goal test
# @phase validate
# @requiresDependencyResolution false
class RubyTest < Mojo

  # @parameter type="org.apache.maven.project.MavenProject" expression="${project}"
  # @required true
  def proj(p);;end

  def execute
    info "The current project is: '#{$proj.artifact_id}'"
    info FileUtils.pwd
  end
end

run_mojo RubyTest

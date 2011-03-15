#require 'mojo_require'

class HelloMojo < Mojo
#  goal :test

#  string :msg
#  parameter "org.codehaus.plexus.component.factory.ComponentFactory", :factory, :expression=>"${org.codehaus.plexus.component.factory.ComponentFactory#jruby}"

  def execute
    puts $msg
    puts $item
    puts $factory.java_class
  end
end

run_mojo HelloMojo

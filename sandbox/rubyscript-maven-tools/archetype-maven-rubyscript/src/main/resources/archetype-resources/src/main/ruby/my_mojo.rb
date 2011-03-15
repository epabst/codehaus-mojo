class MyMojo < Mojo
  goal :mymojo
  description "This is a sample Ruby Mojo"

  string :param, :default=>"hello!", :description=>"This parameter may be filled out by configuration"

  # This method is executed mymojo is run
  def execute
    print "This is my param: '#{@param}'"
  end
end

run_mojo MyMojo

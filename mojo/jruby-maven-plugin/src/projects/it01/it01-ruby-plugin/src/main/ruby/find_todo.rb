class FindTodo2 < Mojo
  goal :todo2
  description "Finds the TODO comments for files in the requested directory 2"

  string :extension, :default=>"rb"
  string :delimiter, :default=>"//"
  file :base, :required=>true, :expression=>"${basedir}", :description=>"The project's base directory."

  # Does not recurse through the dir structure
  def execute
    todo_regexp = %r"#{$delimiter}\W*TODO\W+(.*?)$"
    info "base = #{$base}"
    info "extension = #{$extension}"
    Dir.foreach( $base ) { |filename|
      if filename =~ %r"\.#{$extension}" then
      	info FileUtils.pwd
        File.open( "#{$base}/#{filename}", "r" ) { |file|
          # iterate through the lines of the file
          count = 0
          file.each_line { |line|
            count += 1
            line.scan( todo_regexp ) {
              puts "#{filename}, line #{count}: #{$1}"
            }
          }
        }
      end
    }
  end
end

run_mojo FindTodo2

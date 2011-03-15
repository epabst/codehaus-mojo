# Executes ruby-script plugin integration tests
# *nix-centric
curr_dir = Dir.pwd.to_s.strip
mvn = "mvn"
mvn = "mvn.bat" unless system "mvn -version"

Dir.foreach( curr_dir ) do |it_dir|
	next unless it_dir =~ /^it/
	plugin = nil
	projs = Array.new
	Dir.foreach("#{curr_dir}/#{it_dir}") do |it|
		if it =~ /ruby\-plugin$/
			plugin = it
		elsif it =~ /^it\-project/
			projs.push it
		end
	end
	# fail if plugin.nil?
	passed = true
	plugin_exec_str = "#{mvn} install -f #{it_dir}/#{plugin}/pom.xml" unless plugin.nil?
	puts plugin_exec_str unless plugin.nil?
	passed = system plugin_exec_str unless plugin.nil?
	if passed then
		projs.each do |proj|
			goal = ""
			File.open("#{curr_dir}/#{it_dir}/#{proj}/goals.txt") { |goals|
				goals.each_line { |line|
					goal = ":#{line}".to_s.strip
				}
			}
			if plugin.nil? then
			proj_exec_str = "#{mvn} #{goal} -f #{curr_dir}/#{it_dir}/#{proj}/pom.xml"
			else
			proj_exec_str = "#{mvn} org.codehaus.mojo.ruby.it:#{plugin}#{goal} -f #{curr_dir}/#{it_dir}/#{proj}/pom.xml"
			end
			puts proj_exec_str
			passed = system proj_exec_str
			unless passed then
				puts "FAILED RUBY PLUGIN USE TEST! :("
				exit 1
			end
		end
	else
		puts "FAILED RUBY PLUGIN INSTALL! :("
		exit 1
	end
end

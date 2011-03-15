require 'rdoc/rdoc'

GOAL = 'goal'
CLASS_ARGS = [ 'configurator', 'phase', 'requiresDependencyResolution', 'aggregator' ] #'goal'
EXEC_ARGS  = [ 'phase', 'goal', 'lifecycle' ] #'execute'
DESC = 'description'
FIELDS = 'fields'
FIELD = 'field'
FIELDNAME = 'name'
FIELD_ARGS = [ 'required', 'readonly', 'deprecated' ]
PARAM_ARGS = [ 'alias', 'expression', 'default-value', 'type' ] #'parameter'

EXEC_ARGS_REGEX = EXEC_ARGS.join('|')
PARAM_ARGS_REGEX = PARAM_ARGS.join('|')
PARAM_EXPR_REGEX = '\"(\$\{.*?\}|.*?)\"'

# Extracts annotations from Ruby Mojo classes.
# @goal extract
# @phase install
# @requiresDependencyResolution false
# @execute phase=compile  lifecycle=id5
class Extractor < Mojo

  # @parameter default-value="/file.rb" expression="${someExpression}"
  # @required true
  # @parameter
  def file_name(fn)
    puts "Setting fileName as #{fn}" if $DEBUG
    @file_name = fn
  end

  def execute
    if $file_name.nil? then
      raise MojoError.new("No 'fileName' given.")
    end

    print "Executing the Scanner object.\n" if $DEBUG

    debug_tmp, $DEBUG = $DEBUG, false
    $VERBOSE = false
	o = Options.instance
	o.parse(['--quiet'], Hash.new)
	top_level = RDoc::TopLevel.new($file_name)
	content = File.open($file_name, "r") { |f| f.read }

	p = RDoc::ParserFactory.parser_for( top_level, $file_name, content, o, RDoc::Stats.new )
    top_level = p.scan
    $DEBUG = debug_tmp

	def setParam(cmnt,key,vals)
      cmnt.scan(%r"@#{key}\W+([a-zA-Z][a-zA-Z0-9]*)") { |str| vals[key] = $1 }
      cmnt.scan(%r"@#{key}\W*$") { |str| vals[key] = "true" } if vals[key].nil?
      cmnt.scan(%r"@execute\W+((#{EXEC_ARGS_REGEX})\W*\=\W*([a-zA-Z][a-zA-Z0-9]*))") { |str| vals[key] = $2 } if vals[key].nil?
	end
	def setExecuteParam(cmnt,vals)
	  cmnt.scan(%r"@execute\W+(#{EXEC_ARGS_REGEX})\W*\=\W*([a-zA-Z][a-zA-Z0-9]*)\W+(lifecycle)\W*\=\W*([a-zA-Z0-9]+)") { |str|
		vals['execute'] = execArg = Hash.new
	    execArg[$1.to_s], execArg['lifecycle'] = $2.to_s, $4.to_s
	  }
	  if vals['execute'].nil? then
		  cmnt.scan(%r"@execute\W+(#{EXEC_ARGS_REGEX})\W*\=\W*([a-zA-Z][a-zA-Z0-9]*)") { |str|
			vals['execute'] = execArg = Hash.new
			execArg[$1.to_s] = $2.to_s
		  }
	  end
	end
	def setParameterParam(cmnt,vals)
	  cmnt.scan(%r"@parameter\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}") { |str|
		vals['parameter'] = execArg = Hash.new
	    execArg[$1.to_s], execArg[$3.to_s], execArg[$5.to_s] = $2.to_s, $4.to_s, $6.to_s
	  }
	  if vals['parameter'].nil? then
	    cmnt.scan(%r"@parameter\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}") { |str|
		  vals['parameter'] = execArg = Hash.new
	      execArg[$1.to_s], execArg[$3.to_s] = $2.to_s, $4.to_s
	    }
	  end
	  if vals['parameter'].nil? then
	    cmnt.scan(%r"@parameter\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}") { |str|
		  vals['parameter'] = execArg = Hash.new
	      execArg[$1.to_s] = $2.to_s
	    }
	  end
	  if vals['parameter'].nil? then
	    cmnt.scan(%r"@parameter") { |str|
		  vals['parameter'] = "true"
	    }
	  end
	end

	def setComponentParam(cmnt,vals)
	  cmnt.scan(%r"@component\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}") { |str|
		vals['component'] = execArg = Hash.new
	    execArg[$1.to_s], execArg[$3.to_s], execArg[$5.to_s] = $2.to_s, $4.to_s, $6.to_s
	  }
	  if vals['component'].nil? then
	    cmnt.scan(%r"@component\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}") { |str|
		  vals['component'] = execArg = Hash.new
	      execArg[$1.to_s], execArg[$3.to_s] = $2.to_s, $4.to_s
	    }
	  end
	  if vals['component'].nil? then
	    cmnt.scan(%r"@component\W+(#{PARAM_ARGS_REGEX})\W*\=\W*#{PARAM_EXPR_REGEX}") { |str|
		  vals['component'] = execArg = Hash.new
	      execArg[$1.to_s] = $2.to_s
	    }
	  end
	  if vals['component'].nil? then
	    cmnt.scan(%r"@component") { |str|
		  vals['component'] = "true"
	    }
	  end
	end

	class_anno = Hash.new
	top_level.classes.each { |klass|
      cmnt = klass.comment
      next if cmnt.nil?
      setParam(cmnt, GOAL, class_anno)
      next if class_anno[GOAL].nil?

      # this is the mojo class
      CLASS_ARGS.each { |carg| setParam(cmnt, carg, class_anno) }
	  setExecuteParam(cmnt, class_anno)

	  cmnt.sub!(%r"(.*?)\@.*"m) { |a| $1.gsub!(%r"[\#\n\r]"m, "") unless $1.nil? }
      class_anno[DESC] = cmnt.nil? ? cmnt.strip : nil

      fields = Array.new;
      class_anno[FIELDS] = fields;
      klass.method_list.each { |m|
        mmnt = m.comment
        next if mmnt.nil?
        method_anno = Hash.new
        FIELD_ARGS.each { |marg| setParam(mmnt, marg, method_anno) }
		setParameterParam(mmnt, method_anno)
		setComponentParam(mmnt, method_anno)
        # We do not need to worry about fields with no annotations
        unless method_anno.empty? then
		  mmnt.sub!(%r"(.*?)\@.*"m) { |a| $1.gsub!(%r"[\#\n\r]"m, "") unless $1.nil? }
          method_anno[DESC] = mmnt.nil? ? mmnt.strip : nil
          name = m.name
          name.chop! if name =~ %r".*?="
          method_anno[FIELDNAME] = name
          fields.push method_anno
        end
      }
	}
    if class_anno[GOAL].nil?
      raise MojoError.new("No goal found in this script (@#{GOAL} is required annotation).")
    end

    class_anno
  end
end

run_mojo Extractor

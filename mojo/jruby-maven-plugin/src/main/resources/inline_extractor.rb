class Mojo
  def self.__add_annt( name )
    module_eval "@@__#{name.to_s}_s=nil"
    module_eval "def self.#{name.to_s}(sym); @@__#{name.to_s}_s=sym; end"
    module_eval "def self.__#{name.to_s}_s; @@__#{name.to_s}_s; end"
  end
  [:description,:goal,:phase,:execute,:requiresDependencyResolution].each{|s|__add_annt(s)}

  @@__parameter_s = Hash.new
  def self.parameter( type, sym, vals = {} ); vals["type"],@@__parameter_s[sym] = type,vals; end
  def self.__parameter_s; @@__parameter_s; end

  def self.string( sym, vals = {} ); self.parameter( "java.lang.String", sym, vals ); end
  def self.date( sym, vals = {} ); self.parameter( "java.util.Date", sym, vals ); end
  def self.file( sym, vals = {} ); self.parameter( "java.io.File", sym, vals ); end
end

def include_class( name ); end

def run_mojo( mojo )
  annotations = Hash.new

  annotations["goal"] = mojo.__goal_s.nil? ? mojo.name.to_s.downcase : mojo.__goal_s
  annotations["requiresDependencyResolution"] = mojo.__requiresDependencyResolution_s.to_s
  annotations["phase"] = mojo.__phase_s.to_s
  annotations["description"] = mojo.__description_s
  execute = Hash.new
  mojo.__execute_s.each { |k,v| execute[k.to_s] = v.to_s } unless mojo.__execute_s.nil?
  annotations["execute"] = execute

  parameters = Array.new
  mojo.__parameter_s.each { |name,vals|
    values = Hash.new
    values["name"] = name.to_s
    vals.each { |k,v| values[k.to_s] = v.to_s }
    parameters.push values
  } unless mojo.__parameter_s.nil?
  annotations["fields"] = parameters

  return annotations
end

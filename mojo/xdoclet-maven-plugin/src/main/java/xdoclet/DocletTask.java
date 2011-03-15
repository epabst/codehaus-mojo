package xdoclet;
/*
 * Copyright (c) 2001, 2002 The XDoclet team
 * All rights reserved.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import xjavadoc.ant.XJavadocTask;

import xdoclet.loader.ModuleFinder;
import xdoclet.loader.SubTaskDefinition;
import xdoclet.loader.XDocletModule;

import xdoclet.util.Translator;

/**
 * A base class for all Tasks. It can also be used directly, useful for the case where you want to execute a template
 * file but you don't want to bother writing a new task.
 *
 * @author          Ara Abrahamian (ara_e@email.com)
 * @author          <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles�y</a>
 * @created         June 19, 2001
 * @ant.element     name="xdoclet" display-name="XDoclet Standard Task"
 * @ant.attribute   name="encoding" description="Specify the source file encoding name, such as Windows-31J, EUC-JP,
 *      UTF-8. In default, system default encoding is used."
 * @ant.attribute   name="docencoding" description="Specify encoding name for template engine. The generated file
 *      encoding may be this value. In default, system default encoding is used."
 */
public class DocletTask extends XJavadocTask implements DynamicConfigurator
{
    // ant will replace the tag with the version property specified in build.xml
    public final static String XDOCLET_VERSION = "@VERSION@";

    /**
     * subtask class -> logical name (java.lang.String) Used to look up names. Lazily created.
     */
    private static Map subtaskNameMap;
    /**
     * logical name (java.lang.String) -> subtask (xdoclet.SubTask or a subclass of it). Lazily created.
     */
    private static Map subtaskMap;

    private List    packageSubstitutions = new ArrayList();

    private boolean isModulesRegistered = false;

    private File    destDir;
    private File    mergeDir;
    private String  excludedTags = null;
    private boolean force = false;
    private boolean verbose = false;
    private String  addedTags;
    private List    subTasks = new ArrayList();
    private List    configParams = new ArrayList();

    public DocletTask()
    {
        ModuleFinder.initClasspath(getClass());
    }

    public static String getSubTaskName(Class subTaskClass)
    {
        return (String) getSubtaskNameMap().get(subTaskClass);
    }

    static Map getConfigParamsAsMap(List configParams)
    {
        HashMap map = new HashMap();

        for (Iterator i = configParams.iterator(); i.hasNext(); ) {
            ConfigParameter cp = (ConfigParameter) i.next();

            map.put(cp.getName(), cp.getValue());
        }
        return map;
    }

    static void registerSubTaskName(SubTask subTask, String name)
    {
        getSubtaskNameMap().put(subTask.getClass(), name);
    }

    private static Map getSubtaskMap()
    {
        if (subtaskMap == null)
            subtaskMap = new HashMap();

        return subtaskMap;
    }

    private static Map getSubtaskNameMap()
    {
        if (subtaskNameMap == null)
            subtaskNameMap = new HashMap();

        return subtaskNameMap;
    }

    /**
     * Gets the PackageSubstitutions attribute of the EjbDocletTask object
     *
     * @return   The PackageSubstitutions value
     */
    public List getPackageSubstitutions()
    {
        return packageSubstitutions;
    }

    /**
     * Gets the ConfigParams attribute of the DocletTask object
     *
     * @return   The ConfigParams value
     */
    public List getConfigParams()
    {
        return configParams;
    }

    public Map getConfigParamsAsMap()
    {
        return getConfigParamsAsMap(getConfigParams());
    }

    /**
     * Gets the MergeDir attribute of the DocletTask object
     *
     * @return   The MergeDir value
     */
    public File getMergeDir()
    {
        return mergeDir;
    }

    /**
     * Gets the ExcludedTags attribute of the DocletTask object
     *
     * @return   The ExcludedTags value
     */
    public String getExcludedTags()
    {
        return excludedTags;
    }

    /**
     * Gets the DestDir attribute of the DocletTask object
     *
     * @return   The DestDir value
     */
    public File getDestDir()
    {
        return destDir;
    }

    /**
     * Gets the Force attribute of the DocletTask object.
     *
     * @return   The Force value
     */
    public boolean isForce()
    {
        return force;
    }

    /**
     * Gets the Verbose attribute of the DocletTask object.
     *
     * @return   The Verbose value
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    public String getAddedTags()
    {
        return addedTags;
    }

    /**
     * Sets the PackageSubstitutions attribute of the EjbDocletTask object
     *
     * @param packageSubstitutions  The new PackageSubstitutions value
     * @ant.ignore
     */
    public void setPackageSubstitutions(List packageSubstitutions)
    {
        this.packageSubstitutions = packageSubstitutions;
    }

    /**
     * @param name
     * @param value
     */
    public void setDynamicAttribute(String name, String value)
    {
        throw new BuildException(Translator.getString(XDocletMessages.class, XDocletMessages.ATTRIBUTE_NOT_SUPPORTED, new String[]{getTaskName(), name}));
    }

    /**
     * Sets the PackageNames attribute of the DocletTask object
     *
     * @param src    The new PackageNames value
     * @deprecated
     * @ant.ignore
     */
    public void setPackageNames(String src)
    {
        throw new BuildException(Translator.getString(XDocletMessages.class, XDocletMessages.OBSOLETE_TASK_ATTRIBUTE, new String[]{"packageNames"}));
    }

    /**
     * Sets the ExcludePackageNames attribute of the DocletTask object
     *
     * @param src    The new ExcludePackageNames value
     * @deprecated
     * @ant.ignore
     */
    public void setExcludePackageNames(String src)
    {
        throw new BuildException(Translator.getString(XDocletMessages.class, XDocletMessages.OBSOLETE_TASK_ATTRIBUTE, new String[]{"excludePackageNames"}));
    }

    /**
     * Specify tags that should not be automatically written to output files. The normal behaviour is to include all @
     * tags from the source file to the output files. This may cause trouble if you use cvs-like tag like $Revision: 1.5
     * $ that will be overwritten at each build and causes a difference for CVS even if the code himself is not changed.
     * Example: excludedtags="@ version" For excluded tags, ejbdoclet will generate an hardcoded tag. Example: @ version
     * XDOCLET 1.0
     *
     * @param tags   The new ExcludedTags value
     * @deprecated
     */
    public void setExcludedTags(String tags)
    {
        excludedTags = tags;
    }

    /**
     * Destination directory for output files
     *
     * @param dir          The new DestDir value
     * @ant.not-required   Only if it's not specified for a subtask.
     */
    public void setDestDir(File dir)
    {
        destDir = dir;
    }

    /**
     * Directory where subtasks will look for files to be merged with generated files.
     *
     * @param dir          The new MergeDir value
     * @ant.not-required   No, but should be set if you want to use the merge feature.
     */
    public void setMergeDir(File dir)
    {
        mergeDir = dir;
    }

    /**
     * Specify if the generation of files should be forced. In normal cases, the timestamp of generated file is checked
     * against the timestamps of the class (and its super classes) we generate from. When this timestamp checking should
     * be bypassed (for example after the installtion of a new xdoclet version) then the user should force the
     * regeneration. The easiest way is to run the Ant build file with a parameter "-Dxdoclet.force=true" and add the
     * option "force=${xdoclet.force}" to the doclet call.
     *
     * @param force  The new Force value
     */
    public void setForce(boolean force)
    {
        this.force = force;
    }

    /**
     * Sets the Verbose attribute of the DocletTask object.
     *
     * @param verbose  The new Verbose value
     */
    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    /**
     * Add some JavaDoc tags (or comments) to the generated classes. A special case @ xdoclet-generated. If this is
     * included, ejbdoclet will not consider the file if it is by error in the fileset of the ejbdoclet task.
     *
     * @param addedTags
     */
    public void setAddedTags(String addedTags)
    {
        this.addedTags = addedTags;
    }

    /**
     * Substitutes the package of the generated files.
     *
     * @param ps  The feature to be added to the Fileset attribute
     */
    public void addPackageSubstitution(xdoclet.tagshandler.PackageTagsHandler.PackageSubstitution ps)
    {
        packageSubstitutions.add(ps);
    }

    /**
     * Ant's &lt;fileset&gt; definition. To define the files to parse.
     *
     * @param set  a fileset to add
     */
    public void addFileset(FileSet set)
    {
        // does nothing apart from calling super - it's only here for the
        // javadocs, for the generated documentation.
        super.addFileset(set);
    }

    /**
     * @param name
     * @return
     * @exception BuildException
     */
    public Object createDynamicElement(String name) throws BuildException
    {
        if (!isModulesRegistered) {
            registerModules();
            isModulesRegistered = true;
        }

        SubTask subTask = (SubTask) getSubtaskMap().get(name);

        if (subTask == null) {
            throw new BuildException(Translator.getString(XDocletMessages.class, XDocletMessages.CREATE_TASK_ERROR, new String[]{name, getTaskName()}));
        }
        subTasks.add(subTask);
        return subTask;
    }

    /**
     * Generic subtask.
     *
     * @param subtask  The subtask to be added
     * @ant.ignore
     */
    public void addSubTask(SubTask subtask)
    {
        subTasks.add(subtask);
    }

    /**
     * Generic subtask for processing a user-supplied template.
     *
     * @param subtask             Describe the method parameter
     * @exception BuildException
     * @ant.ignore
     */
    public void addTemplate(TemplateSubTask subtask) throws BuildException
    {
        if (subtask.getSubTaskClassName() == null) {
            addSubTask(subtask);
        }
        else {
            try {
                Class subtaskClass = Class.forName(subtask.getSubTaskClassName());
                TemplateSubTask alias = (TemplateSubTask) subtaskClass.newInstance();

                // now copy from subtask to real alias
                alias.copyAttributesFrom(subtask);

                addSubTask(alias);
            }
            catch (ClassNotFoundException e) {
                throw new BuildException(Translator.getString(XDocletMessages.class,
                    XDocletMessages.CLASS_NOT_FOUND_EXCEPTION,
                    new String[]{subtask.getSubTaskClassName(), e.getMessage()}), e, location);
            }
            catch (InstantiationException e) {
                throw new BuildException(Translator.getString(XDocletMessages.class,
                    XDocletMessages.INSTANTIATION_EXCEPTION,
                    new String[]{subtask.getSubTaskClassName(), e.getMessage()}), e, location);
            }
            catch (IllegalAccessException e) {
                throw new BuildException(Translator.getString(XDocletMessages.class,
                    XDocletMessages.ILLEGAL_ACCESS_EXCEPTION,
                    new String[]{subtask.getSubTaskClassName(), e.getMessage()}), e, location);
            }
        }
    }

    /**
     * Generic subtask for processing a user-supplied template, to generate an XML document.
     *
     * @param subtask  Describe the method parameter
     * @ant.ignore
     */
    public void addXmlTemplate(XmlSubTask subtask)
    {
        addTemplate(subtask);
    }

    /**
     * Allows to set configuration parameters that will be included in the element as attribute value pair.
     *
     * @param configParam  Describe the method parameter
     */
    public void addConfigParam(ConfigParameter configParam)
    {
        configParams.add(configParam);
    }

    /**
     * Gets the SubTasks attribute of the DocletTask object
     *
     * @return   The SubTasks value
     */
    protected final List getSubTasks()
    {
        return subTasks;
    }

    /**
     * Gets the ConfigParams attribute of the DocletTask object
     *
     * @param subtasks  Describe what the parameter does
     * @return          The ConfigParams value
     */
    protected HashMap getConfigParams(List subtasks)
    {
        HashMap configs = new HashMap();

        // config params of task
        ConfigParamIntrospector.fillConfigParamsFor(this, configs);

        // config params of substask
        for (int i = 0; i < subtasks.size(); i++) {
            SubTask subtask = (SubTask) subtasks.get(i);

            if (subtask != null) {
                ConfigParamIntrospector.fillConfigParamsFor(subtask, configs);

                // user defined params of SubTask
                fillWithUserDefinedConfigParams(configs, subtask.getConfigParams(), subtask.getSubTaskName() + '.');
            }
        }

        // user defined params of DocletTask
        fillWithUserDefinedConfigParams(configs, getConfigParams(), "");

        return configs;
    }

    /**
     * @exception BuildException
     */
    protected void start() throws BuildException
    {
        try {
            new XDocletMain().start(getXJavaDoc());
        }
        catch (XDocletException e) {
            throw new BuildException(Translator.getString(XDocletMessages.class, XDocletMessages.XDOCLET_FAILED), e, location);
        }
        finally {
            DocletContext.setSingleInstance(null);
            // Fix for http://opensource.atlassian.com/projects/xdoclet/browse/XDT-879
            subtaskNameMap = null;
            subtaskMap = null;
            //destDir = null;
            //mergeDir = null;
            //subTasks = null;
            //configParams = null;

            ModuleFinder.resetFoundModules();
        }
    }

    /**
     * Called by superclass before start() is called
     *
     * @exception BuildException  Describe the exception
     */
    protected void validateOptions() throws BuildException
    {
        super.validateOptions();
        if (destDir == null) {
            throw new BuildException(Translator.getString(XDocletMessages.class, XDocletMessages.ATTRIBUTE_NOT_PRESENT_ERROR, new String[]{"destDir"}), location);
        }
        validateSubTasks();
    }

    /**
     * Throws BuildException if a specific class is not on the CP. Should be called from subclasses' validateOptions()
     * to verify that classpath is OK.
     *
     * @param className
     */
    protected void checkClass(String className)
    {
        try {
            Class.forName(className);
        }
        catch (Exception e) {
            throw new BuildException(Translator.getString(XDocletMessages.class, XDocletMessages.CHECK_CLASS_FAILED, new String[]{className, getTaskName()}));
        }
    }

    /**
     * Describe what the method does
     *
     * @exception BuildException  Describe the exception
     */
    protected void validateSubTasks() throws BuildException
    {
        DocletContext context = createContext();
        SubTask[] subtasks = context.getSubTasks();

        for (int i = 0; i < subtasks.length; i++) {
            SubTask subtask = subtasks[i];

            if (subtask != null) {
                log("validating subTask: " + subtask.getSubTaskName() + " class: " + subtask.getClass(), Project.MSG_DEBUG);
                try {
                    subtask.validateOptions();
                }
                catch (XDocletException ex) {
                    throw new BuildException(subtask.getSubTaskName() + ": " + ex.getMessage(), location);
                }
            }
        }
    }

    private void registerModules()
    {
        // Register subtasks that apply to us (they do if they in xdoclet.xml have declared us as parent)
        List modules = ModuleFinder.findModules();
        Iterator i = modules.iterator();

        while (i.hasNext()) {
            XDocletModule module = (XDocletModule) i.next();
            List subTaskDefinitions = module.getSubTaskDefinitions();
            Iterator j = subTaskDefinitions.iterator();

            while (j.hasNext()) {
                SubTaskDefinition subTaskDefinition = (SubTaskDefinition) j.next();

                try {
                    Class parentTaskClass = Class.forName(subTaskDefinition.parentTaskClass);

                    if (parentTaskClass.isAssignableFrom(getClass())) {
                        if (getSubtaskMap().containsKey(subTaskDefinition.name)) {
                            String conflictingSubTaskClassName = getSubtaskMap().get(subTaskDefinition.name).getClass().getName();

                            if (!subTaskDefinition.implementationClass.equals(conflictingSubTaskClassName)) {
                                // duplicate subtask definition, and it's not the same classname (which occurs
                                // if a module is twice or more on classpath - which is OK)
                                throw new BuildException(Translator.getString(XDocletMessages.class,
                                    XDocletMessages.AMBIGUOUS_SUBTASK_DEFINITION,
                                    new String[]{subTaskDefinition.name, conflictingSubTaskClassName,
                                    subTaskDefinition.implementationClass}));
                            }

                            //make sure a new subtask is not created when we can use an already created one. This happens
                            //when you run the task several times.
                            continue;
                        }

                        Class subTaskClass = Class.forName(subTaskDefinition.implementationClass);
                        SubTask subTask = (SubTask) subTaskClass.newInstance();

                        log("Registering SubTask " + subTaskDefinition.name + " (" + subTaskDefinition.implementationClass + ") to DocletTask " + getClass().getName(), Project.MSG_DEBUG);
                        getSubtaskMap().put(subTaskDefinition.name, subTask);
                        registerSubTaskName(subTask, subTaskDefinition.name);
                    }
                }
                catch (ClassNotFoundException e) {
                    throw new BuildException(Translator.getString(XDocletMessages.class,
                        XDocletMessages.DEPENDENT_CLASS_FOR_SUBTASK_NOT_FOUND,
                        new String[]{subTaskDefinition.parentTaskClass, ModuleFinder.getClasspath()}), e);
                }
                catch (InstantiationException e) {
                    throw new BuildException(Translator.getString(XDocletMessages.class,
                        XDocletMessages.INSTANTIATION_EXCEPTION,
                        new String[]{subTaskDefinition.implementationClass, e.getMessage()}), e);
                }
                catch (IllegalAccessException e) {
                    throw new BuildException(Translator.getString(XDocletMessages.class,
                        XDocletMessages.ILLEGAL_ACCESS_EXCEPTION,
                        new String[]{subTaskDefinition.implementationClass, e.getMessage()}), e);
                }
                catch (ClassCastException e) {
                    throw new BuildException(Translator.getString(XDocletMessages.class,
                        XDocletMessages.CLASS_CAST_EXCEPTION,
                        new String[]{subTaskDefinition.implementationClass, SubTask.class.getName()}), e);
                }
            }
        }
    }

    /**
     * Returns the singleton context object and creates it if not already created and registers it as the single
     * instance.
     *
     * @return   the singleton context object
     */
    private DocletContext createContext()
    {
        if (DocletContext.getInstance() != null) {
            return DocletContext.getInstance();
        }

        List subtasks = getSubTasks();
        HashMap configs = getConfigParams(subtasks);

        DocletContext context = new DocletContext(
            this.destDir.toString(),
            this.mergeDir != null ? this.mergeDir.toString() : null,
            this.excludedTags,
            (SubTask[]) subtasks.toArray(new SubTask[0]),
            project.getProperties(),
            configs,
            force,
            verbose,
            addedTags
            );

        // now register this single instance
        DocletContext.setSingleInstance(context);

        return context;
    }

    /**
     * Describe what the method does
     *
     * @param configs       Describe what the parameter does
     * @param configParams  Describe what the parameter does
     * @param prefix        Describe what the parameter does
     */
    private void fillWithUserDefinedConfigParams(HashMap configs, List configParams, String prefix)
    {
        // config params declared with <configParam name="nnn" value="val"/>
        for (int i = 0; i < configParams.size(); i++) {
            ConfigParameter configParam = (ConfigParameter) configParams.get(i);

            configs.put((prefix + configParam.getName()).toLowerCase(), configParam.getValue());
        }
    }

}

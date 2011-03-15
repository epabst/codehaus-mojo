package org.codehaus.mojo.repositorytools.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.codehaus.mojo.repositorytools.components.RepositoryToolsException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @plexus.component role="org.codehaus.mojo.repositorytools.validation.ArtifactValidator"
 *                   role-hint="imports"
 * @author tom
 * 
 */
public class ImportsValidator extends AbstractValidator
{

	private static final String CLASS_SUFFIX = ".class";

	/**
	 * @plexus.requirement
	 */
	private ArtifactResolver resolver;

	/**
	 * @plexus.requirement
	 */
	private ArtifactMetadataSource source;

	public List validateArtifact(Artifact artifact,
			List remoteRepositories,
			ArtifactRepository localRepository) throws RepositoryToolsException
	{
		List messages = new ArrayList();
		if (!artifact.getArtifactHandler().getLanguage().equals("java"))
		{
			messages.add(new ValidationMessage(ValidationMessage.INFO,
					"Not a java artifact"));
		}

		SyntheticRepository bcelRepo;
		String[] classes;
		try
		{
			ClassPath classpath = getClassPath(artifact, remoteRepositories,
					localRepository);

			bcelRepo = SyntheticRepository.getInstance(classpath);
			classes = findClasses(artifact.getFile());
		}
		catch (Exception e1)
		{
			throw new RepositoryToolsException("Error setting up classpath");
		}

		Set imported = new HashSet();

		for (int i = 0; i < classes.length; i++) {
			String klass = classes[i];
			JavaClass jc;
			try
			{
				jc = bcelRepo.loadClass(klass);
				bcelRepo.storeClass(jc);
				Set s = getImportedClasses(jc);
				imported.addAll(s);
			}
			catch (ClassNotFoundException e)
			{
				// this should not happen !
				messages.add(new ValidationMessage(ValidationMessage.ERROR,
						"Class " + klass + "could not be loaded."));
			}
		}

		for (Iterator iterator = imported.iterator(); iterator.hasNext();) {
			String i = (String) iterator.next();
			JavaClass classFile;
			try
			{
				classFile = bcelRepo.loadClass(i);
				bcelRepo.storeClass(classFile);
			}
			catch (ClassNotFoundException e)
			{
				messages.add(new ValidationMessage(ValidationMessage.WARNING,
						"Class " + i + " not found in any dependency."));
			}
		}

		// sort the messages so we get a nicer view on the packages
		Collections.sort(messages, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return ((ValidationMessage) o1).getMessage().compareTo(((ValidationMessage) o2).getMessage());
			}
		});
		return messages;
	}

	public ClassPath getClassPath(Artifact artifact,
			List remoteRepositories,
			ArtifactRepository localRepository)
			throws ProjectBuildingException, ArtifactResolutionException,
			ArtifactNotFoundException, InvalidDependencyVersionException
	{
		MavenProject pomProject = createProject(artifact, remoteRepositories,
				localRepository);
		resolver.resolve(artifact, remoteRepositories, localRepository);

		ArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE);
		Set artifacts = pomProject.createArtifacts(artifactFactory, null,
				filter);
		resolver.resolveTransitively(artifacts,
				artifact, localRepository, remoteRepositories, source, filter);

		StringBuffer result = new StringBuffer();

		// TODO portable way to get the boot classpath
		result.append(System.getProperty("sun.boot.class.path"));
		result.append(File.pathSeparatorChar);
		for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
			Artifact a = (Artifact) iterator.next();
			result.append(a.getFile());
			result.append(File.pathSeparatorChar);
		}
		result.append(artifact.getFile());

		return new ClassPath(result.toString());
	}

	private static String[] findClasses(File jarFile) throws IOException
	{
		List result = new ArrayList();
		JarFile jar = new JarFile(jarFile);
		Enumeration entries = jar.entries();
		while (entries.hasMoreElements())
		{
			JarEntry je = (JarEntry) entries.nextElement();
			String name = je.getName();
			if (name.endsWith(CLASS_SUFFIX))
			{
				name = name.substring(0, name.length() - CLASS_SUFFIX.length());
				name = name.replace('/', '.');
				result.add(name);
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	private Set getImportedClasses(JavaClass jc)
	{
		final Set imports = new HashSet();

		final ConstantPool pool = jc.getConstantPool();
		new DescendingVisitor(jc, new EmptyVisitor()
		{
			public void visitConstantClass(ConstantClass cc)
			{
				String s = pool.constantToString(cc);
				s = s.replace('/', '.');
				if (s.startsWith("["))
				{
					while (s.startsWith("["))
					{
						s = s.substring(1);
					}
					s = s.substring(1); // remove [L
				}

				if (s != null && !s.startsWith("java."))
				{
					imports.add(s);
				}
			}
		}).visit();

		return imports;
	}

	public String getDescription()
	{
		return "Validation of imports";
	}

	public boolean canValidate(Artifact artifact)
	{
		String classifier = artifact.getClassifier();
		if (!StringUtils.isEmpty(classifier) && classifier.indexOf("sources") >= 0) {
			return false;
		}
		return artifact.getArtifactHandler().getLanguage().equals("java")
				&& !artifact.getType().equals("pom");
	}

}

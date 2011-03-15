package org.codehaus.mojo.ship;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.StringUtils;

/**
 * Selects a specific project artifact.
 */
public final class Selector {
    private String classifier;
    private String type;

    public Selector(String type, String classifier) {
        this.type = type;
        this.classifier = classifier;
    }

    public Selector() {
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Selector selector = (Selector) o;

        if (classifier != null ? !classifier.equals(selector.classifier) : selector.classifier != null) {
            return false;
        }
        if (type != null ? !type.equals(selector.type) : selector.type != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "groupId:artifactId:version" + (type == null ? "" : ":" + type) + (classifier == null ? "" : ":" + classifier);
    }

    public boolean matches(Artifact artifact) {
        if (!StringUtils.isEmpty(type)) {
            if (!type.equals(artifact.getType()) && !type.equals(artifact.getArtifactHandler().getExtension()) && !type.equals(artifact.getArtifactHandler().getPackaging())) {
                return false;
            }
        }
        if (classifier == null) {
            return artifact.getClassifier() == null;
        } else {
            return classifier.equals(artifact.getClassifier());
        }
    }
}

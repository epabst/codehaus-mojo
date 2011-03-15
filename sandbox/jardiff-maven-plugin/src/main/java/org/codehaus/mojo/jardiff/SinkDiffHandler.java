package org.codehaus.mojo.jardiff;

import org.apache.maven.doxia.sink.Sink;
import org.osjava.jardiff.ClassInfo;
import org.osjava.jardiff.DiffException;
import org.osjava.jardiff.DiffHandler;
import org.osjava.jardiff.FieldInfo;
import org.osjava.jardiff.MethodInfo;
import org.objectweb.asm.Type;

public final class SinkDiffHandler implements DiffHandler {

	private final Sink sink;
	
	private int changeLevel;
    private String currentClass;

    public SinkDiffHandler( final Sink sink ) {
		this.sink = sink;
		this.changeLevel = 0;
	}
	
	public void startDiff(String from, String to) throws DiffException {
		sink.section1();
		sink.sectionTitle1();
		sink.text("API differences between " + from + " and " +  to);
		sink.sectionTitle1_();
	}

	public void contains(ClassInfo contains) throws DiffException {
	}

	public void startNewContents() throws DiffException {
	}
	public void endNewContents() throws DiffException {
	}

	public void startOldContents() throws DiffException {
	}
	public void endOldContents() throws DiffException {
	}
		
	
	// removed classes

	public void startRemoved() throws DiffException {
		
		if (changeLevel == 0) {
			sink.section2();
			sink.sectionTitle2();
			sink.text("Classes removed");
			sink.sectionTitle2_();
            sink.list();
		} else if (changeLevel == 1) {
			sink.bold();
			sink.text("Removed:");
			sink.bold_();
            sink.lineBreak();
            sink.list();
		}

		// class changed remove
	}
	public void classRemoved(ClassInfo removed) throws DiffException {
		sink.listItem();
        sink.text(toClassName(removed.getName()));
		sink.listItem_();
	}

    private String toClassName(String name) {
        return name.replace('/', '.');
    }

    public void endRemoved() throws DiffException {
		if (changeLevel == 0) {
            sink.list_();
			sink.section2_();
		} else if(changeLevel == 1){
            sink.list_();
        }
	}

	
	// added classes
	
	public void startAdded() throws DiffException {
		if (changeLevel == 0) {
			sink.section2();
			sink.sectionTitle2();
			sink.text("Classes added");
			sink.sectionTitle2_();
			sink.list();
		} else if (changeLevel == 1) {
			sink.bold();
			sink.text("Added:");
			sink.bold_();
            sink.lineBreak();
            sink.list();
		}
	}
	public void classAdded(ClassInfo added) throws DiffException {
		sink.listItem();
        sink.text(toClassName(added.getName()));
        sink.listItem_();
	}
	public void endAdded() throws DiffException {
		if (changeLevel == 0) {
            sink.list_();
			sink.section2_();
		} else if (changeLevel == 1) {
            sink.list_();
        }
	}

	
	// changed classes

	public void startChanged() throws DiffException {
		
		if (changeLevel == 0) {
			sink.section2();
			sink.sectionTitle2();
			sink.text("Classes changed");
			sink.sectionTitle2_();
		}
        if (changeLevel == 1) {
			sink.bold();
			sink.text("Changed:");
			sink.bold_();
            sink.lineBreak();
            sink.list();
		}

		changeLevel++;
	}

	public void startClassChanged(String changed) throws DiffException {
		sink.section3();
		sink.sectionTitle3();
		sink.text(currentClass = toClassName(changed));
		sink.sectionTitle3_();
	}
	public void classChanged(ClassInfo from, ClassInfo to) throws DiffException {
		sink.listItem();
        sink.text("Class changed ");
        writeClassInfo(from);
        sink.bold();
        sink.text(" -> ");
        sink.bold_();
        writeClassInfo(to);
		sink.listItem_();
	}

	public void methodRemoved(MethodInfo removed) throws DiffException {
		sink.listItem();
        sink.text("Method removed: ");
        writeMethodInfo(removed);
        sink.listItem_();
	}

    public void methodChanged(MethodInfo from, MethodInfo to) throws DiffException {
		sink.listItem();
        sink.text("Method changed: ");
        writeMethodInfo(from);
        sink.bold();
        sink.text(" -> ");
        sink.bold_();
        writeMethodInfo(to);

		sink.listItem_();
	}

    public void methodAdded(MethodInfo added) throws DiffException {
		sink.listItem();
        sink.text("Method added: ");
        writeMethodInfo(added);
		sink.listItem_();
	}

    public void fieldRemoved(FieldInfo removed) throws DiffException {
		sink.listItem();
        sink.text("Field removed: ");
        writeFieldInfo(removed);
		sink.listItem_();
	}



    public void fieldChanged(FieldInfo from, FieldInfo to) throws DiffException {
		sink.listItem();
        sink.text("Field changed: ");
        writeFieldInfo(from);
        sink.bold();
        sink.text(" -> ");
        sink.bold_();
        writeFieldInfo(to);

		sink.listItem_();
	}

    public void fieldAdded(FieldInfo added) throws DiffException {
		sink.listItem();
        sink.text("Field added: ");
        writeFieldInfo(added);
		sink.listItem_();
	}

    public void endClassChanged() throws DiffException {
        sink.section3_();
	}

    public void endChanged() throws DiffException {
		changeLevel--;
		if (changeLevel == 0) {
			sink.section2_();
		}
	}

    // end

    public void endDiff() throws DiffException {
		sink.section1_();
	}

    private void writeMethodInfo(MethodInfo info) {
        if(info.isDeprecated()) {
            sink.italic();
            sink.text("deprecated ");
            sink.italic_();
        }
        sink.text(info.getAccessType() +" ");

        if(info.isFinal()) {
            sink.text("final ");
        }
        if(info.isStatic()) {
            sink.text("static ");
        }
        if(info.isSynchronized()) {
            sink.text("synchronized ");
        }
        if(info.isAbstract()) {
            sink.text("abstract ");
        }

        if(info.getDesc() != null && !info.getName().equals("<init>")) {
            Type returnType = Type.getReturnType(info.getDesc());
            writeType(returnType);
            sink.text(" ");
        }

        if(info.getName().equals("<init>")) {
            sink.text(currentClass);
        } else {
            sink.text(info.getName());
        }


        if(info.getDesc() != null) {

            Type[] types = Type.getArgumentTypes(info.getDesc());

            
            sink.text("(");

            for(int t = 0; t < types.length; t++) {

                Type origType = types[t];
                writeType(origType);



                if(t < types.length-1) {
                    sink.text(", ");
                }
            }
            sink.text(") ");
        }


        if(info.getExceptions() != null && info.getExceptions().length > 0) {
            sink.text("throws ");
            for(int i = 0; i < info.getExceptions().length; i++) {
                sink.text(toClassName(info.getExceptions()[i]));
                if(i < info.getExceptions().length-1) {
                    sink.text(", ");
                }
            }
        }
    }

    private void writeType(Type origType) {
        Type type = origType;

        int i = origType.getSort();

        if (i == Type.ARRAY) {
            type = origType.getElementType();
            i = type.getSort();
        }
        switch (i) {
            case Type.BOOLEAN:
                sink.text("boolean");
                break;
            case Type.BYTE:
                sink.text("byte");
                break;
            case Type.CHAR:
                sink.text("char");
                break;
            case Type.DOUBLE:
                sink.text("double");
                break;
            case Type.FLOAT:
                sink.text("float");
                break;
            case Type.INT:
                sink.text("int");
                break;
            case Type.LONG:
                sink.text("long");
                break;
            case Type.OBJECT:
                sink.text(origType.getClassName());
                break;
            case Type.SHORT:
                sink.text("short");
                break;
            case Type.VOID:
                sink.text("void");
                break;
        }
        if (origType.getSort() == Type.ARRAY) {
            for(int d = 0; d < origType.getDimensions(); d++) {
                sink.text("[]");
            }
        }
    }

    private void writeClassInfo(ClassInfo info) {

        if(info.isDeprecated()) {
            sink.italic();
            sink.text("deprecated ");
            sink.italic_();
        }
        sink.text(info.getAccessType() +" ");


        if(info.isAbstract()) {
            sink.text("abstract ");
        }
        if(info.isStatic()) {
            sink.text("static ");
        }
        if(info.isFinal()) {
            sink.text("final ");
        }
        if(info.getName().equals("<init>")) {
            sink.text("<init> ");
        }
        sink.text(toClassName(info.getName()) +" ");

        if(info.getSupername() != null) {
            sink.text("extends " + toClassName(info.getSupername()) +" ");
        }
        if(info.getInterfaces() != null && info.getInterfaces().length > 0) {
            sink.text("implements ");
            for(int i = 0; i < info.getInterfaces().length; i++) {
                sink.text(toClassName(info.getInterfaces()[i]));
                if(i < info.getInterfaces().length-1) {
                    sink.text(", ");
                }
            }
        }

    }

    private void writeFieldInfo(FieldInfo info) {
         if(info.isDeprecated()) {
            sink.italic();
            sink.text("deprecated ");
            sink.italic_();
        }
        sink.text(info.getAccessType() +" ");


        if(info.isFinal()) {
            sink.text("final ");
        }
        if(info.isStatic()) {
            sink.text("static ");
        }
        if(info.isTransient()) {
            sink.text("transient ");
        }
        if(info.isVolatile()) {
            sink.text("volatile ");
        }

        if(info.getName().equals("<init>")) {
            sink.text("<init> ");
        }

        if(info.getDesc() != null) {

            Type type = Type.getType(info.getDesc());
            
            writeType(type);

        }

        sink.text(" " +info.getName() +" ");

        if(info.getValue() != null) {
            sink.text("=" +(info.getValue() instanceof String ? "\"" + info.getValue() +"\"" : info.getValue().toString()));
        }

    }
}

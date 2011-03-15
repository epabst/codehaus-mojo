package org.codehaus.mojo.jlint;

public class Constants
{
    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public static final String ROOT_START_TAG = "<BugCollection version=\"3.1\" threshold=\"Low\" effort=\"Max\" >";

    public static final String ROOT_END_TAG = "</BugCollection>";

    public static final String FILE_START_TAG = "<file classname=";

    public static final String FILE_END_TAG = "</file>";

    public static final String BUG_START_TAG = "    <BugInstance ";

    public static final String BUGATTR_TYPE = " type =";

    public static final String BUGATTR_PRIORITY = " priority =";

    public static final String BUGATTR_CATEGORY = " category =";

    public static final String BUGATTR_MESSAGE = " message =";

    public static final String BUGATTR_LINENO = " lineNumber =";

    public static final String BUG_END_TAG = "/>";

    public static final String JLINT_CMD = "jlint -done ";

    public static final String JLINT_CLASSES_DIR = "classes";

    public static final String JLINT_CFG_FILENAME = "/config/Jlint_msg.cfg";

    public static final String JLINT_XML_OUTPUT_FILENAME = "jlint-violations.xml";

    public static final String JLINT_TXT_OUTPUT_FILENAME = "jlint-violations.txt";

    public static final String JLINT_ERROR_FILENAME = "jlint.err";

    public static final String FIELD_SEPARATOR = ";";

    public static final String CONFIG_FILENAME = "jlint-config.xml";

    public static final String XMLCONFIGFILE_RULE_NODE = "Rule";

    // private constructor to prevent instantiation
    private Constants()
    {
    }
}

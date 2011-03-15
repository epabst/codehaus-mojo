package org.codehaus.mojo.repositorytools.validation;

public class ValidationMessage
{
	public static final int ERROR = 3;

	public static final int WARNING = 2;

	public static final int INFO = 1;

	private String message;

	private int severity;

	public ValidationMessage(int severity, String message)
	{
		if (severity < 1 || severity > 3) {
			throw new IllegalArgumentException("Illegal severity: " + severity);
		}
		this.severity = severity;
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public int getSeverity()
	{
		return severity;
	}

	public String toString()
	{
		switch (severity)
		{
		case 1:
			return "INFO: " + message;
		case 2:
			return "WARNING: " + message;
		case 3:
			return "ERROR: " + message;
		default:
			throw new IllegalStateException();
		}

	}
}

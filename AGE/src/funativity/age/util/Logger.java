package funativity.age.util;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

/**
 * Logger class to use a uniform tag, and report in a uniform way the source of
 * the log.
 * 
 * @author riedla
 * 
 */
public class Logger
{
	private static String LOG_TAG = "DEBUG";

	/**
	 * Set the tag that log messages use. All messages that are reported after
	 * this point will use this tag (until it is changed again)
	 * 
	 * @param newTag
	 *            Tag to use in log messages
	 */
	public static void setLogTag(String newTag)
	{
		LOG_TAG = newTag;
	}

	/**
	 * Print on the info stream. Gets the source of the calling method to add to
	 * the message.
	 * 
	 * @param msg
	 *            message about error
	 */
	public static void i(String msg)
	{
		i(getSource(), msg);
	}

	/**
	 * Print on the info stream. Gets the source of the calling method to add to
	 * the message. Will show information about exception in report.
	 * 
	 * @param msg
	 *            message about error
	 * @param e
	 *            exception to report
	 */
	public static void i(String msg, Throwable e)
	{
		i(getSource(), msg, e);
	}

	/**
	 * Print on the warn stream. Gets the source of the calling method to add to
	 * the message.
	 * 
	 * @param msg
	 *            message about error
	 */
	public static void w(String msg)
	{
		w(getSource(), msg);
	}

	/**
	 * Print on the warn stream. Gets the source of the calling method to add to
	 * the message. Will show information about exception in report.
	 * 
	 * @param msg
	 *            message about error
	 * @param e
	 *            exception to report
	 */
	public static void w(String msg, Throwable e)
	{
		w(getSource(), msg, e);
	}

	/**
	 * Print on the debug stream. Gets the source of the calling method to add
	 * to the message.
	 * 
	 * @param msg
	 *            message about error
	 */
	public static void d(String msg)
	{
		d(getSource(), msg);
	}

	/**
	 * Print on the debug stream. Gets the source of the calling method to add
	 * to the message. Will show information about exception in report.
	 * 
	 * @param msg
	 *            message about error
	 * @param e
	 *            exception to report
	 */
	public static void d(String msg, Throwable e)
	{
		d(getSource(), msg, e);
	}

	/**
	 * Print on the error stream. Gets the source of the calling method to add
	 * to the message.
	 * 
	 * @param msg
	 *            message about error
	 */
	public static void e(String msg)
	{
		e(getSource(), msg);
	}

	/**
	 * Print on the error stream. Gets the source of the calling method to add
	 * to the message. Will show information about exception in report.
	 * 
	 * @param msg
	 *            message about error
	 * @param e
	 *            exception to report
	 */
	public static void e(String msg, Throwable e)
	{
		e(getSource(), msg, e);
	}

	/**
	 * Check to see if there is any opengl errors. if there is print them to
	 * error log. If there is no error, nothing will be printed. This should be
	 * called after nearly all sections that use a lot of OpenGL calls
	 * (primarily initialization code)
	 */
	public static void checkOGLError()
	{
		// get opengl error number
		final int error = GLES20.glGetError();

		// if there was an error
		if (error != 0)
		{
			// get the log message for that error
			final String errorMsg = GLU.gluErrorString(error);

			// report issue
			e(getSource(), "OpenGL Error: " + error + "   " + errorMsg);
		}
	}

	/**
	 * Check to see if there is a problem with a shader. Will report any issues
	 * found
	 * 
	 * @param msg
	 *            message to add to report
	 * @param shaderID
	 *            id of shader being checked
	 */
	public static void checkShaderError(String msg, int shaderID)
	{
		String error = GLES20.glGetShaderInfoLog(shaderID);
		if (error != null && !error.isEmpty())
			e(getSource(), msg + " - " + error);
	}

	/**
	 * Check to see if there is any problems with a shader program. Will report
	 * any issues found.
	 * 
	 * @param msg
	 *            message to add to report
	 * @param programID
	 *            id of shader program being checked
	 */
	public static void checkShaderProgramError(String msg, int programID)
	{
		String error = GLES20.glGetProgramInfoLog(programID);
		if (error != null && !error.isEmpty())
			e(getSource(), msg + " - " + error);
	}

	/**
	 * Print to info stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 */
	private static void i(String source, String message)
	{
		Log.i(LOG_TAG, source + " - " + message);
	}

	/**
	 * Print to info stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 * @param e
	 *            Exception to report
	 */
	private static void i(String source, String message, Throwable e)
	{
		Log.i(LOG_TAG, source + " - " + message, e);
	}

	/**
	 * Print to warn stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 */
	private static void w(String source, String message)
	{
		Log.w(LOG_TAG, source + " - " + message);
	}

	/**
	 * Print to warn stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 * @param e
	 *            Exception to report
	 */
	private static void w(String source, String message, Throwable e)
	{
		Log.w(LOG_TAG, source + " - " + message, e);
	}

	/**
	 * Print to debug stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 */
	private static void d(String source, String message)
	{
		Log.d(LOG_TAG, source + " - " + message);
	}

	/**
	 * Print to debug stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 * @param e
	 *            Exception to report
	 */
	private static void d(String source, String message, Throwable e)
	{
		Log.d(LOG_TAG, source + " - " + message, e);
	}

	/**
	 * Print to error stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 */
	private static void e(String source, String message)
	{
		Log.e(LOG_TAG, source + " - " + message);
	}

	/**
	 * Print to error stream using the static LOG_TAG tag.
	 * 
	 * @param source
	 *            of parents caller
	 * @param message
	 *            message of error
	 * @param e
	 *            Exception to report
	 */
	private static void e(String source, String message, Throwable e)
	{
		Log.e(LOG_TAG, source + " - " + message, e);
	}

	/**
	 * Get the source of the calling method. will return a string that has the
	 * class and method name of the source that called the method that called
	 * this method. This should only be used inside of this class by log
	 * methods. Those methods will then have their calling source.
	 * 
	 * @return source of caller
	 */
	private static String getSource()
	{
		return getCallingClass() + ":" + getCallingMethod();
	}

	private static String getCallingClass()
	{
		return new Throwable().fillInStackTrace().getStackTrace()[3]
				.getClassName();
	}

	private static String getCallingMethod()
	{
		return new Throwable().fillInStackTrace().getStackTrace()[3]
				.getMethodName();
	}
}

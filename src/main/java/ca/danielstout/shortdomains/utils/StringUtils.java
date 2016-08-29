package ca.danielstout.shortdomains.utils;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtils
{

	private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

	public static String substringAfterMatch(String original, String match)
	{
		return original.substring(original.lastIndexOf(match) + match.length());
	}

	/**
	 * Uses reflection to generate a toString for an object. Simpler and faster than the
	 * implementation in the Apache Commons.
	 * @param obj - The object to generate a toString for
	 * @return A toString
	 */
	public static String makeToString(Object obj)
	{
		Class<?> clazz = obj.getClass();
		String name = clazz.getSimpleName();
		StringBuilder builder = new StringBuilder(name + ": {");
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			Field field = fields[i];
			try
			{
				String fieldName = field.getName();
				field.setAccessible(true);
				Object value = field.get(obj);
				String valueRep = null;
				if (value == null)
				{
					valueRep = "null";
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					valueRep = "\"" + value + "\"";
				}
				else
				{
					valueRep = value.toString();
				}
				builder.append(fieldName + ":" + valueRep);
				if (i < fields.length - 1) builder.append(", ");
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				log.error("Error generating toString", e);
			}
		}

		return builder.append("}").toString();
	}

	/**
	 * Splits a string into blocks of a given width. Adapted from
	 * http://stackoverflow.com/a/12297231
	 * @param str The string to split
	 * @param interval The width of the individual blocks
	 * @return The array of blocks (The last block may not be complete)
	 */
	public static String[] splitStringEvery(String str, int interval)
	{
		if (str.isEmpty())
		{
			return new String[0];
		}

		int arrayLength = (int) Math.ceil(((str.length() / (double) interval)));
		String[] result = new String[arrayLength];
		int j = 0;
		int lastIndex = result.length - 1;
		for (int i = 0; i < lastIndex; i++)
		{
			result[i] = str.substring(j, j + interval);
			j += interval;
		}
		result[lastIndex] = str.substring(j);
		return result;
	}
}

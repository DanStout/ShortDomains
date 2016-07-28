package ca.danielstout.shortdomains.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils
{
	public static String readFile(String path)
	{
		try
		{
			return new String(Files.readAllBytes(Paths.get(path)));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

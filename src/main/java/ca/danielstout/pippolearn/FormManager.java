package ca.danielstout.pippolearn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.pippo.core.route.RouteContext;

public class FormManager
{
	private static final String KEY = "form_errors";
	private static final String OBJS = "ses_objs";

	private static final Logger log = LoggerFactory.getLogger(FormManager.class);
	private RouteContext ctx;

	public FormManager(RouteContext context)
	{
		ctx = context;
	}

	public void moveFlashToLocal()
	{
		Map<String, List<String>> stored = ctx.removeSession(KEY);
		if (stored == null) stored = new HashMap<>();
		ctx.setLocal(KEY, stored);

		Set<Object> objs = ctx.removeSession(OBJS);
		if (objs == null) objs = new HashSet<>();
		for (Object obj : objs)
		{
			setFieldsToLocals(ctx, obj);
		}
	}

	public <T> void flashObject(T obj)
	{
		Set<Object> objs = ctx.getSession(OBJS);
		if (objs == null)
		{
			objs = new HashSet<>();
			ctx.setSession(OBJS, objs);
		}

		objs.add(obj);
	}

	private <T> void setFieldsToLocals(RouteContext ctx, T obj)
	{
		for (Field fld : obj.getClass().getDeclaredFields())
		{
			if (!fld.isAccessible()) fld.setAccessible(true);

			String fldName = fld.getName();

			try
			{
				String value = fld.get(obj).toString();
				if (value != null)
				{
					ctx.setLocal(fldName, value);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				log.debug("Failed to get value for field {}", fldName);
			}
		}
	}

	public void addError(String field, String error)
	{
		Map<String, List<String>> errs = ctx.getSession(KEY);
		if (errs == null)
		{
			errs = new HashMap<>();
			ctx.setSession(KEY, errs);
		}

		List<String> storedErrs = errs.get(field);
		if (storedErrs == null)
		{
			storedErrs = new ArrayList<>();
			errs.put(field, storedErrs);
		}
		storedErrs.add(error);
	}
}

package ca.danielstout.shortdomains;

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
	private static final String KEY_FORM_ERRORS = "form_errors";
	// TODO: Better value
	private static final String KEY_FLASHED_OBJS_BY_FIELD = "ses_objs";
	private static final String KEY_FLASHED_OBJS = "objs";

	private static final Logger log = LoggerFactory.getLogger(FormManager.class);
	private RouteContext ctx;

	public FormManager(RouteContext context)
	{
		ctx = context;
	}

	public void moveFlashToLocal()
	{
		Map<String, List<String>> stored = ctx.removeSession(KEY_FORM_ERRORS);
		if (stored == null) stored = new HashMap<>();
		ctx.setLocal(KEY_FORM_ERRORS, stored);

		Set<Object> objs = ctx.removeSession(KEY_FLASHED_OBJS_BY_FIELD);
		if (objs != null)
		{
			for (Object obj : objs)
			{
				setFieldsToLocals(ctx, obj);
			}
		}

		Map<String, Object> storedObjs = ctx.removeSession(KEY_FLASHED_OBJS);
		if (storedObjs != null)
		{
			for (Map.Entry<String, Object> entry : storedObjs.entrySet())
			{
				ctx.setLocal(entry.getKey(), entry.getValue());
			}
		}

	}

	public void flashObject(String name, Object obj)
	{
		Map<String, Object> stored = ctx.getSession(KEY_FLASHED_OBJS);
		if (stored == null)
		{
			stored = new HashMap<>();
			ctx.setSession(KEY_FLASHED_OBJS, stored);
		}
		stored.put(name, obj);
	}

	/**
	 * Flashes the fields of an object
	 * @param obj
	 */
	public <T> void flashObjectFields(T obj)
	{
		Set<Object> objs = ctx.getSession(KEY_FLASHED_OBJS_BY_FIELD);
		if (objs == null)
		{
			objs = new HashSet<>();
			ctx.setSession(KEY_FLASHED_OBJS_BY_FIELD, objs);
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
		Map<String, List<String>> errs = ctx.getSession(KEY_FORM_ERRORS);
		if (errs == null)
		{
			errs = new HashMap<>();
			ctx.setSession(KEY_FORM_ERRORS, errs);
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

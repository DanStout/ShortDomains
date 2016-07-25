package ca.danielstout.pippolearn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import ro.pippo.core.Application;
import ro.pippo.pebble.PebbleTemplateEngine;

public class CustomPebbleTemplateEngine extends PebbleTemplateEngine
{
	private Function csrfFunction = new Function()
	{
		@Override
		public List<String> getArgumentNames()
		{
			return null;
		}

		@Override
		public Object execute(Map<String, Object> args)
		{
			// PebbleTemplate self = (PebbleTemplate) args.get("_self"); // Allows you
			// to access current template
			Object token = getVariable(args, "csrfToken");
			String msg = String.format("<input type=\"hidden\" name=\"_csrf_token\" value=\"%s\">",
				token);
			return new SafeString(msg);
		}
	};

	private Function helptextFunction = new Function()
	{

		@Override
		public List<String> getArgumentNames()
		{
			List<String> list = new ArrayList<>();
			list.add("fieldname");
			list.add("default");
			return list;
		}

		/**
		 * Expected call syntax:
		 * {% helptext("fieldname", "optional default text") %}
		 * Examples:
		 * {% helptext("password", "At least 8 characters") %}
		 * {% helptext("email") %}
		 * @param args
		 * @return
		 */
		@Override
		public Object execute(Map<String, Object> args)
		{
			String fieldName = (String) args.get("fieldname");
			String def = (String) args.get("default");

			Map<String, List<String>> formErrors = getVariable(args, "form_errors");

			List<String> errors = formErrors.get(fieldName);

			if (def == null && errors == null) return null;

			StringBuilder builder = new StringBuilder();
			builder.append("<span class=\"help-block\">");

			if (errors == null)
			{
				builder
					.append("<div>")
					.append(def)
					.append("</div>");
			}
			else
			{
				for (String err : errors)
				{
					builder
						.append("<div>")
						.append(err)
						.append("</div>");
				}
			}
			builder.append("</span>");

			return new SafeString(builder.toString());
		}
	};

	@SuppressWarnings("unchecked")
	private <T> T getVariable(Map<String, Object> context, String name)
	{
		EvaluationContext ctx = (EvaluationContext) context.get("_context");
		return (T) ctx.getScopeChain().get(name);
	}

	private Extension customExtension = new Extension()
	{
		@Override
		public Map<String, Filter> getFilters()
		{
			return null;
		}

		@Override
		public Map<String, Test> getTests()
		{
			return null;
		}

		@Override
		public Map<String, Function> getFunctions()
		{
			Map<String, Function> map = new HashMap<>();
			map.put("csrf", csrfFunction);
			map.put("helptext", helptextFunction);
			return map;
		}

		@Override
		public List<TokenParser> getTokenParsers()
		{
			return null;
		}

		@Override
		public List<BinaryOperator> getBinaryOperators()
		{
			return null;
		}

		@Override
		public List<UnaryOperator> getUnaryOperators()
		{
			return null;
		}

		@Override
		public Map<String, Object> getGlobalVariables()
		{
			return null;
		}

		@Override
		public List<NodeVisitorFactory> getNodeVisitors()
		{
			return null;
		}
	};

	@Override
	protected void init(Application application, Builder builder)
	{
		if (application.getPippoSettings().isDev())
		{
			builder.cacheActive(false);
		}

		builder.extension(customExtension);
	}
}

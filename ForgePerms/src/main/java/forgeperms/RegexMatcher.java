package forgeperms;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Regex based permission matcher. Based on the PermissionsEx RegExpMatcher.
 * @author Joe Goett
 */
public class RegexMatcher {
	public static final String RAW_REGEX_CHAR = "$";
	private static final LoadingCache<String, Pattern> patternCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Pattern>(){
		@Override
		public Pattern load(String permission) throws Exception {
			return createPattern(permission);
		}
	});
	
	public static boolean matches(String expression, String permission) {
		try {
			Pattern pattern = patternCache.get(expression);
			return pattern.matcher(permission).matches();
		} catch(ExecutionException ex) {
			return false;
		}
	}
	
	private static Pattern createPattern(String expression) {
		try {
			return Pattern.compile(prepareRegex(expression), Pattern.CASE_INSENSITIVE);
	    } catch (PatternSyntaxException e) {
	    	return Pattern.compile(Pattern.quote(expression), Pattern.CASE_INSENSITIVE);
	    }
	}
	
	private static String prepareRegex(String expression) {
		if (expression.startsWith("-")) {
			expression = expression.substring(1);
		}

		boolean rawRegexp = expression.startsWith(RAW_REGEX_CHAR);
		if (rawRegexp) {
			expression = expression.substring(1);
		}

		return rawRegexp ? expression : expression.replace(".", "\\.").replace("*", "(.*)");
	}
}
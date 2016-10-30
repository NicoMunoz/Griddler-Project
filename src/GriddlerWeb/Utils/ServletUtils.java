package GriddlerWeb.Utils;

import GriddlerWeb.UILogic.GamesManager;
import GriddlerWeb.UILogic.UserManager;
import javax.servlet.ServletContext;

public class ServletUtils
{
	public static UserManager getUserManager(ServletContext servletContext) {
	if (servletContext.getAttribute(Constants.USER_MANAGER_ATTRIBUTE_NAME) == null) {
	    servletContext.setAttribute(Constants.USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
	}
	return (UserManager) servletContext.getAttribute(Constants.USER_MANAGER_ATTRIBUTE_NAME);
    }

	public static GamesManager getGamesManager(ServletContext servletContext) {
		if (servletContext.getAttribute(Constants.GAMES_MANAGER_ATTRIBUTE_NAME) == null) {
			servletContext.setAttribute(Constants.GAMES_MANAGER_ATTRIBUTE_NAME, new GamesManager());
		}
		return (GamesManager) servletContext.getAttribute(Constants.GAMES_MANAGER_ATTRIBUTE_NAME);
	}
}

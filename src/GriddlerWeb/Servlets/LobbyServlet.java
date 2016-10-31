package GriddlerWeb.Servlets;

import GriddlerWeb.GameLogic.BoardInfo;
import GriddlerWeb.GameLogic.GameLogic;
import GriddlerWeb.UILogic.GamesManager;
import GriddlerWeb.UILogic.SimpleBoard;
import GriddlerWeb.UILogic.User;
import GriddlerWeb.UILogic.UserManager;
import GriddlerWeb.Utils.Constants;
import GriddlerWeb.Utils.ServletUtils;
import GriddlerWeb.Utils.SessionUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "LobbyServlet", urlPatterns = {"/lobby"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LobbyServlet extends HttpServlet
{
    private void checkUserPlaying(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        String isAlreadyPlaying = "false";
        User userFromSession = SessionUtils.getLoginUser(request);

        if(userFromSession.isPlaying()){
            isAlreadyPlaying = "true";
        }
        response.getWriter().write(isAlreadyPlaying);
        response.getWriter().flush();
    }

    private void gameAndUserLists(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        Gson gson = new Gson();

        String usersJson = gson.toJson(userManager.getUsers());
        String gameDetalisJson = gson.toJson(gamesManager.getGames());

        String bothJson = "["+usersJson+","+gameDetalisJson+"]"; //Put both objects in an array of 2 elements
        response.getWriter().write(bothJson);
        response.getWriter().flush();
    }

    private void joinGame(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        String success = "true";

        User userFromSession = SessionUtils.getLoginUser(request);
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());

        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        GameLogic gameToJoin = gamesManager.getSpecificGame(gameTitle);
        if(gameToJoin != null)
        {
            String playerName = userFromSession.GetName();
            if(gameToJoin.isUnActiveGame() && gameToJoin.notInGame(playerName) && gameToJoin.hasRoom())
            {
                gameToJoin.insertPlayer(playerName, userFromSession.GetType().equals(Constants.HUMAN));
                request.getSession(true).setAttribute(Constants.GAME_TITLE, gameTitle);
                userFromSession.setPlay(true);
            }
            else {
                success = "false";
            }
            response.getWriter().write(success);
            response.getWriter().flush();
        }
    }

    private void logOut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        User userFromSession = SessionUtils.getLoginUser(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (userFromSession != null) {
            userManager.removeUser(userFromSession.GetName());
            SessionUtils.clearSession(request);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter(Constants.ACTION_TYPE);
        switch (action)
        {
            case Constants.GAME_AND_USER_LIST:
                gameAndUserLists(request, response);
                break;
            case Constants.JOIN_GAME:
                joinGame(request, response);
                break;
            case Constants.JOIN_AS_VISITOR:
                joinGameVisitor(request,response);
                break;
            case Constants.LOGOUT:
                logOut(request, response);
                break;
            case Constants.CHECK_USER_PLAYING:
                checkUserPlaying(request, response);
                break;
        }
    }

    private void joinGameVisitor(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        response.setContentType("application/json");
        boolean canJoin;
        String message ="";
        User userFromSession = SessionUtils.getLoginUser(request);
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String name = userFromSession.GetName();

        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        GameLogic gameToJoin = gamesManager.getSpecificGame(gameTitle);

        if(!gameToJoin.isUnActiveGame()) {
            canJoin = gameToJoin.checkAndEnterVisitor(name);
            request.getSession(true).setAttribute(Constants.GAME_TITLE, gameTitle);
            if(!canJoin)
                message = "There are no Human Players";
        }
        else
        {
            canJoin =false;
            message = "The game didnt started yet";
        }
        String firstEle = new Gson().toJson(canJoin);
        String secEle = new Gson().toJson(message);
        String retJson = "["+firstEle+","+secEle+"]";
        response.getWriter().write(retJson);
        response.getWriter().flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //response.setContentType("text/html");
        response.setContentType("application/json");

        Part myPart = request.getPart(Constants.XML_FILE);
        InputStream file = myPart.getInputStream();
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        User userFromSession = SessionUtils.getLoginUser(request);
        String result = gamesManager.addNewGame(file, userFromSession.GetName());

        if(result != null){
            Gson gson = new Gson();
            response.getWriter().write(gson.toJson(result));
            response.getWriter().flush();
        }
    }
}

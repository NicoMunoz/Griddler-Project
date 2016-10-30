package GriddlerWeb.Servlets;

import GriddlerWeb.UILogic.GamesManager;
import GriddlerWeb.UILogic.User;
import GriddlerWeb.Utils.ServletUtils;
import GriddlerWeb.Utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import javax.servlet.annotation.MultipartConfig;


@WebServlet(name = "UploadServlet", urlPatterns = {"/upload"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadServlet extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
//        response.setContentType("text/html");
//        User userFromSession = SessionUtils.getLoginUser(request);
//        Collection<Part> parts = request.getParts();
//
//        for (Part part : parts)
//        {
//            InputStream file = part.getInputStream();
//            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
//            String result = gamesManager.addNewGame(file,  userFromSession.GetName());
////            if(result != null){                        after change to one button, print error if had
////                response.getWriter().print(result);
////            }
//        }
//        response.sendRedirect("Lobby.html");
    }
}

package GriddlerWeb.Servlets;

import GriddlerWeb.Utils.Constants;

import GriddlerWeb.UILogic.User;
import GriddlerWeb.UILogic.UserManager;
import GriddlerWeb.Utils.SessionUtils;
import GriddlerWeb.Utils.ServletUtils;
import com.google.gson.Gson;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet
{
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        String action = request.getParameter(Constants.ACTION_TYPE);

        switch (action)
        {
            case Constants.PERFOME_LOGIN:
                logIn(request, response);
                break;
            case Constants.CHECK_USER_LOGIN:
                isAlreadyLogIn(request, response);
                break;
        }
    }

    private void isAlreadyLogIn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        String isAlreadyLogIn = "false";
        User userFromSession = SessionUtils.getLoginUser(request);

        if(userFromSession != null) {
            isAlreadyLogIn = "true";
        }
        response.getWriter().write(isAlreadyLogIn);
        response.getWriter().flush();
    }

    private void logIn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        String isNameExist = "false";
        User userFromSession = SessionUtils.getLoginUser(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (userFromSession == null)  //user is not logged in yet
        {
            String userNameFromParameter = request.getParameter(Constants.USER_NAME);
            String userType = request.getParameter(Constants.USER_TYPE);

            userNameFromParameter = userNameFromParameter.trim();  //normalize the username value
            User newUser = new User(userNameFromParameter, userType);

            if (userManager.isUserExists(newUser.GetName())) {   //username already exists
                isNameExist = "true";
            } else {
                //add the new user to the users list
                userManager.addUser(newUser);
                //set the username in a session so it will be available on each request
                //the true parameter means that if a session object does not exists yet
                //create a new one
                request.getSession(true).setAttribute(Constants.LOGIN_USER, newUser);
            }
            response.getWriter().write(isNameExist);
            response.getWriter().flush();
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

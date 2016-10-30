package GriddlerWeb.Servlets;

import GriddlerWeb.GameLogic.*;
import GriddlerWeb.GameLogic.Player;
import GriddlerWeb.UILogic.*;
import GriddlerWeb.Utils.Constants;
import GriddlerWeb.Utils.ServletUtils;
import GriddlerWeb.Utils.SessionUtils;
import GriddlerWeb.jaxb.schema.generated.*;
import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.CASTORE;
import com.sun.org.apache.xerces.internal.impl.dv.xs.BooleanDV;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GameRoomServlet", urlPatterns = {"/gamingRoom"})
public class GameRoomServlet extends HttpServlet
{
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //response.setContentType("application/json");
        //response.setContentType("text/html");
        String action = request.getParameter(Constants.ACTION_TYPE);
        switch (action)
        {
            case Constants.DO_MOVE:
                handleDoMove(request,response);
                break;
            case Constants.UNDO:
                undoRedoMove(request, response, action);
                break;
            case Constants.REDO:
                undoRedoMove(request, response, action);
                break;
            case Constants.PASS_TURN:
                passTurn(request, response);
                break;
            case Constants.GAME_STATUS:
                gameStatus(request, response);
                break;
            case Constants.PERFOME_MOVE_AS_COMPUTER:
                computerMove(request,response);
                break;
            case Constants.IS_GAME_STARTED:
                isGameStarted(request, response);
                break;
            case Constants.EXIT_GAME:
                exitGame(request, response);
                break;
            case Constants.GET_BOARD:
                getBoard(request, response);
                break;
            case Constants.GET_SHOW_BOARD:
                getShowBoard(request,response);
                break;
            case Constants.PULL_BOARD:
                pullBoard(request,response);
                break;
            case Constants.FIRST_PLY_COMP:
                checkAndManageFirstPlayer(request,response);
                break;

        }
    }

    private void checkAndManageFirstPlayer(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String isComputer = "false";
        GameLogic currGame = getGameLogic(request);
        ArrayList<Player> players = currGame.getPlayers();
        String nameJson = new Gson().toJson(((User)request.getSession(false).getAttribute(Constants.LOGIN_USER)).GetName());

       Player thisPly = getSpeceifPlyByName(players,nameJson);

        if(!thisPly.isHuman()){
            isComputer="true";
            String thisPlyName = thisPly.getName();
            String firstPly = players.get(0).getName();
            if(firstPly.equals(thisPlyName)){
                currGame.manageComputerMove();
                currGame.PassTurn();
            }
        }
        response.getWriter().write(isComputer);
        response.getWriter().flush();
    }

    private void pullBoard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        response.setContentType("application/json");
        String nameJson = new Gson().toJson(((User)request.getSession(false).getAttribute(Constants.LOGIN_USER)).GetName());
        GameLogic currGame = getGameLogic(request);

        Player boardFromPly = getSpeceifPlyByName(currGame.getPlayers(),nameJson);
        BoardInfo currBoard = boardFromPly.getBoard();

        String board = new Gson().toJson(currBoard);
        response.getWriter().write(board);
        response.getWriter().flush();
    }

    private Player getSpeceifPlyByName(ArrayList<Player>i_AllPlayes , String nameJson) {
        Player retPly = null;
        for(Player currPly : i_AllPlayes)
        {
            String name = nameJson.substring(1, nameJson.length()-1);
            String currPlayerName = currPly.getName();

            if(name.equals(currPlayerName))
            {
                retPly = currPly;
                break;
            }
        }
        return retPly;
    }

    private void getShowBoard(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        response.setContentType("application/json");

        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());

        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        GameLogic gameBoard = gamesManager.getSpecificGame(gameTitle);
        BoardInfo board = gameBoard.getOriginalBoard();

        SimpleBoard responseBoard = new SimpleBoard(board.getBoard(), board.getRowBlocks(), board.getColBlocks());
        String boardJson = new Gson().toJson(responseBoard);
        response.getWriter().write(boardJson);
        response.getWriter().flush();
    }

    private void computerMove(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
        response.setContentType("application/json");
        String resultPar;
        ArrayList<GameMove> movesDone;
        GameLogic currGame = getGameLogic(request);
        movesDone = currGame.manageComputerMove();
        String firstMove = getCellInJson(movesDone.get(0));
        String secondMove = getCellInJson(movesDone.get(1));
        resultPar = "["+firstMove+","+secondMove+"]";

        response.getWriter().write(resultPar);
        response.getWriter().flush();
    }
    private void undoRedoMove(HttpServletRequest request, HttpServletResponse response, String action)  throws ServletException, IOException
    {
        response.setContentType("application/json");
        GameLogic currGame = getGameLogic(request);
        GameMove gameMove = null;
        if (action.equals(Constants.UNDO)) {
            gameMove = currGame.UndoLastMoveFromPlayer();

        }else {
            gameMove = currGame.RedoLastMoveFromPlayer();
        }
        if(gameMove != null){
            String cellsToJson = getCellInJson(gameMove);
            String resultParameter;
            String perfectRowBlockJson = new Gson().toJson(currGame.getRowList());
            String perfectColBlockJson = new Gson().toJson(currGame.getColumnList());
            resultParameter = "["+cellsToJson+","+perfectRowBlockJson+","+perfectColBlockJson+"]";

            response.getWriter().write(resultParameter);
            response.getWriter().flush();
        }
    }

    private String getCellInJson(GameMove gameMove)
    {
        List<CellUI> listCell = new ArrayList<>();

        LinkedList<Cell> gameMoveCells = gameMove.getCells();
        for (Cell cell: gameMoveCells) {
            listCell.add(new CellUI(cell.getPoint().x, cell.getPoint().y, cell.getStatus().toString()));
        }

        String cellsInJson = new Gson().toJson(listCell);
        return cellsInJson;
    }



    private void handleDoMove(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String resultParameter;
        GameLogic currGame = getGameLogic(request);
        Pair<Boolean,String> responseCanPlay = canPlayerPlay(request,currGame,true);
        if(responseCanPlay.getKey()){
            String signString = request.getParameter(Constants.REQUEST_TYPE);
            BoardInfo.BoardOptions sign =  BoardInfo.BoardOptions.ParseFromString(signString);

            LinkedList<Cell> moveCellList = new LinkedList<>();
            javafx.util.Pair[] selectedCoords = gson.fromJson(request.getParameter(Constants.SELECTED_COORDS), javafx.util.Pair[].class);
            for (javafx.util.Pair coordinate : selectedCoords) {
                moveCellList.add(new Cell(Integer.parseInt((String) coordinate.getKey()), Integer.parseInt((String) coordinate.getValue()), sign));
            }
            currGame.DoMoveOfCurrPlayer(new GameMove(moveCellList));
            String retVal = new Gson().toJson(true);
            String perfectRowBlockJson = new Gson().toJson(currGame.getRowList());
            String perfectColBlockJson = new Gson().toJson(currGame.getColumnList());
            resultParameter = "["+retVal+","+perfectRowBlockJson+","+perfectColBlockJson+"]";
        }else {
            String canMake = new Gson().toJson(responseCanPlay.getKey());
            String message = new Gson().toJson(responseCanPlay.getValue());
            resultParameter = "["+canMake+","+message+"]";
        }

        response.getWriter().write(resultParameter);
        response.getWriter().flush();
    }

    private Pair<Boolean,String> canPlayerPlay(HttpServletRequest request, GameLogic currGame,boolean fromDoMove)
    {

        Pair<Boolean,String> retPair;
        User userFromSession = SessionUtils.getLoginUser(request);
        if(userFromSession.GetName().equals(currGame.getNameCurrPlayer())) {
            if(!fromDoMove|| currGame.canMakeAnotherMove()) {
                retPair = new Pair<>(true, "");
            }
            else
            {
                retPair = new Pair<>(false,"Made The Max Amount Of Moves");
            }
        } else{
            retPair =  new Pair<>(false,"Not Your Turn");
        }
        return retPair;
    }

    private GameLogic getGameLogic(HttpServletRequest request)
    {
        String cuurGameTile = (String)request.getSession(false).getAttribute(Constants.GAME_TITLE);
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        return gamesManager.getSpecificGame(cuurGameTile);
    }

    private void getBoard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        GameLogic currGame = getGameLogic(request);
        BoardInfo board = currGame.getCurrentBoard();
        SimpleBoard responseBoard = new SimpleBoard(board.getBoard(), board.getRowBlocks(), board.getColBlocks());
        String boardJson = new Gson().toJson(responseBoard);
        response.getWriter().write(boardJson);
        response.getWriter().flush();
    }

    private void isGameStarted(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        String isGameStarted = "false";
        GameLogic currGame = getGameLogic(request);

        if(currGame.isFullPlayers()){
            isGameStarted = "true";
            currGame.setActiveGame(false);
        }
        response.getWriter().write(isGameStarted);
        response.getWriter().flush();
    }

    private void passTurn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        boolean nextPlayerIsComputer = false;
        String result = "true";
        GameLogic currGame = getGameLogic(request);
        if(canPlayerPlay(request, currGame,false).getKey()) {
            currGame.PassTurn();

        }else {
            result = "false";
        }
        response.getWriter().write(result);
        response.getWriter().flush();
    }

    private void exitGame(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        GameLogic currGame = getGameLogic(request);
        User userFromSession = SessionUtils.getLoginUser(request);
        currGame.removePlayer(userFromSession.GetName());
        request.getSession(true).removeAttribute(Constants.GAME_TITLE);
        userFromSession.setPlay(false);
    }

    private void gameStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        String nameJson = new Gson().toJson(((User)request.getSession(false).getAttribute(Constants.LOGIN_USER)).GetName());

        String usersJson, gameDetalisJson;
        GameLogic currGame = getGameLogic(request);
        List<Player> gamePlayers = currGame.getPlayers();

        usersJson = getPlayesToJson(gamePlayers);
        gameDetalisJson = getGameDetalisJson(currGame);

        String bothJson = "["+usersJson+","+gameDetalisJson+","+nameJson+"]"; //Put both objects in an array of 2 elements
        response.getWriter().write(bothJson);
        response.getWriter().flush();
    }

    private String getGameDetalisJson(GameLogic game)
    {
        GameDetails gameDetalis = new GameDetails(game.getCurrentPlayer().getName(), game.getCurrGameRound(), game.getTotalRounds(),
                game.getCurrMoveOfSamePlayer(), game.getGameTitle(), game.getWinnerName(), game.getM_TechnicalVictory(), game.getFinishAllRound());
        Gson gson = new Gson();
        String gameDetalisJson =  gson.toJson(gameDetalis);
        return gameDetalisJson;
    }

    private String getPlayesToJson(List<Player> gamePlayers)
    {
        UserManager userManage = new UserManager();
        for (Player ply: gamePlayers)
        {
            String score = Float.toString(ply.getScore());
            String userType = getUserType(ply.isHuman());
            userManage.addUser(new User(ply.getName(), userType, score));
        }

        Gson gson = new Gson();
        String playerJson =  gson.toJson(userManage.getUsers());
        return playerJson;
    }

    private String getUserType(boolean human)
    {
        if(human == true){
            return Constants.HUMAN;
        }
        else {
            return Constants.MACHINE;
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

var refreshRate = 1000; //miliseconds

$(document).ready(function () {
    $.ajaxSetup({cache: false});

    $('#GameAction').hide();
    $('#Replay').hide();
    $('#visitorsTable').hide();

    $('#buttonQuit').on("click", ajaxQuitGame);
    ajaxGamesDeatilsAndPlayers();
    GamesDeatilsAndPlayers = setInterval(ajaxGamesDeatilsAndPlayers, refreshRate);

    getBoard($('#board'));
    var actionType = "isVisitor";
    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success:function (result){
            if(result){
                visitoPlayer();
            }
            else
            {
               realPlayer();
            }
        }
    });

});

function visitoPlayer()
{
    $('#buttonQuit').val("Back To Lobby");
    setInterval(ajaxVisitorBoard,refreshRate);

}

function ajaxVisitorBoard() {
    var actionType = "pullVisitorBoard";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success: function (boardInfo) {
            updateAllBoard(boardInfo);
        }
    });
}


function realPlayer()
{
    $("#radioMark").prop("checked", true);

    startGame = setInterval(ajaxIsGameStarted, refreshRate);

    $('#GameInfo').hide();

    $('.option').on("click", actionSelected);
}

function startIfFirstPlayerComputer()
{
    var actionType = "firstPlyComputer";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success:function (isComp){
            if(isComp === "true"){
                setInterval(ajaxBoard,refreshRate); //onli if is computer it will pull every min
                $('#GameAction').hide();
            }
            else
            {
                $('#GameAction').fadeIn(200);
            }
        }
    });
}
function ajaxBoard()
{
    var actionType = "pullBoard";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success: function (boardInfo) {
            updateAllBoard(boardInfo);
        }
    });
}
function updateAllBoard(boardInfo)
{
    var rows = boardInfo.m_Rows;
    var cols = boardInfo.m_Cols;
    var board = boardInfo.m_Board;
    var rowBlocks = boardInfo.m_RowBlocks;
    var colBlocks = boardInfo.m_ColBlocks;

    updatePullingBoard(rows,cols,board);
    updateSpecificBlocks(rowBlocks,colBlocks);
}
function updatePullingBoard(rows,cols,board){
    for(var currR = 0 ; currR < rows ; currR++) {
        for (var currC = 0; currC < cols; currC++) {
            var containSelecte = false;
            var td = $('[row="' + currR + '"][column="' + currC + '"]');
            var lastClass = td.attr('class').split(' ');
            if ((lastClass[lastClass.length - 1] === "selected")) {
                td.removeClass(td.attr('class').split(' ').pop());
                containSelecte = true;
            }
            td.removeClass(td.attr('class').split(' ').pop());
            td.addClass(board[currR][currC].toLowerCase());
            if (containSelecte) {
                td.addClass("selected");
            }

        }
    }
}

function ajaxGamesDeatilsAndPlayers() {
    var actionType = "GameStatus";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success: function (data) {
            var players = data[0];
            var gameDetails = data[1];
            var PlayerFromSesion = data[2];
            var visitors = data[3];

            refreshGameDeatils(gameDetails);
            refreshPlayerList(players, PlayerFromSesion);
            refreshVisitors(visitors)
        }
    });
}

function refreshPlayerList(players, PlayerFromSesion) {
    $("#playingUsersTable").empty();
    $.each(players || [], function (index, user) {
        var icon;
        if (user.userType === 'Human') {
            icon = "<span class='HumanIcon'></span>"
        }
        else {
            icon = "<span class='MachineIcon'></span>"
        }
        var userList = $('<tr> </tr>');
        $('<th>' + user.userName + '</th>').appendTo(userList);
        $('<th>' + icon + '</th>').appendTo(userList);
        $('<th>' + user.score + '</th>').appendTo(userList);
        userList.appendTo($("#playingUsersTable"));

        if (PlayerFromSesion == user.userName) {
            userList.addClass('success');
        }
    });
}

function refreshVisitors(visitors)
{
    if(visitors.length == 0) {
        $('#visitorsTable').hide();
    } else {
        $('#visitorsTable').show();
        $('#allVisitors').empty();
        for (var i = 0; i < visitors.length; i++) {
            var visitor = $('<tr> </tr>');
            $('<th>' + (i + 1) + '</th>').appendTo(visitor);
            $('<th>' + visitors[i] + '</th>').appendTo(visitor);
            visitor.appendTo($("#allVisitors"));
        }
    }
}

function refreshGameDeatils(gameDetails) {
    $('#lableGameTitle').text(gameDetails.gameTitle);
    $('#lableCurrentPlayer').text(gameDetails.currPlayer);
    $('#lableRound').text(gameDetails.currRound + " / " + gameDetails.totalRounds);
    $('#lableCurrentMove').text( gameDetails.cuurMove + " / 2");

    if  (gameDetails.winnerName !== undefined || gameDetails.finishAllRound === true){
        if (gameDetails.technicalVictory === true) {
            openPopup("Technical Victory To " + gameDetails.winnerName + "!!!");
        } else {
            if(gameDetails.winnerName === undefined)
            {
                openPopup("We Have No Winner")
            }
            else
            openPopup(gameDetails.winnerName + " is Win!!!");
        }
        $('#GameAction').hide();
        $('#GameInfo').hide();
        $('#buttonQuit').val("Back To Lobby");
        clearInterval(GamesDeatilsAndPlayers);
    }
}

function ajaxQuitGame() {
    var actionType = "ExitGame";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success: function (data) {
            window.location.replace("Lobby.html");
        },
        error: function (data) {
            console.log(data);
        }
    });
}

function ajaxIsGameStarted() {
    var actionType = "isGameStarted";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success: function (isGameStarted) {
            if (isGameStarted === true) {
                openPopup("Game Started!");

                clearInterval(startGame);
                $('#GameInfo').fadeIn(200);
                startIfFirstPlayerComputer();
            }
        },
    });
}

 function replayExit() {

     ajaxBoard();
     $('#Replay').hide();
     $('#GameAction').fadeIn(200);
 }
function nextMove() {
    ajaxNextOrPrev("true");
}
function prevMove(){
    ajaxNextOrPrev("false");
}

function ajaxNextOrPrev(next){
    var actionType = "prevOrNext";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType,
            "requestType": next
        },
        success: function (result) {
            if (result[0]) {    // existBoard
                updateAllBoard(result[1]);
            } else {
                openPopup(result[1]);
            }
            removeSelectedSquares();
        }
    });
}
function replyFromEnd(){
ajaxStartReplay("false");
}
function replayFromStart() {
    ajaxStartReplay("true");
}

function ajaxStartReplay(start){
    var actionType = "replay";
    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType,
            "requestType": start
        },
        success: function (result) {
            if (result[0]) {    // existBoard
                $('#GameAction').hide();
                $('#Replay').fadeIn(200);
                updateAllBoard(result[1]);
            } else {
                openPopup(result[1]);
            }
            removeSelectedSquares();
        },
        error:function (e) {
            console.log(e);
        }
    });


}

function actionSelected() {
    $('.option').removeClass('optionSelected');
    $(this).addClass('optionSelected');
}

function openPopup(msg) {
    $("#message").html(msg);
    $("#popup").show();
}

function closePopup() {
    $("#popup").hide();
}
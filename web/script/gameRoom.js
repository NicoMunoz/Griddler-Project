var refreshRate = 1000; //miliseconds

$(document).ready(function () {
    $.ajaxSetup({cache: false});

    $('#buttonQuit').on("click", ajaxQuitGame);
    $("#radioMark").prop("checked", true);

    ajaxGamesDeatilsAndPlayers();
    GamesDeatilsAndPlayers = setInterval(ajaxGamesDeatilsAndPlayers, refreshRate);
    startGame = setInterval(ajaxIsGameStarted, refreshRate);


    getBoard($('#board'));

    $('#GameAction').hide();
    $('#GameInfo').hide();

    $('.option').on("click", actionSelected);
});

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
            var rows = boardInfo.m_Rows;
            var cols = boardInfo.m_Cols;
            var board = boardInfo.m_Board;
            var rowBlocks = boardInfo.m_RowBlocks;
            var colBlocks = boardInfo.m_ColBlocks;

            updatePullingBoard(rows,cols,board);
            updateSpecificBlocks(rowBlocks,colBlocks);
        }
    });
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

            refreshGameDeatils(gameDetails);
            refreshPlayerList(players, PlayerFromSesion);
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

function refreshGameDeatils(gameDetails) {
    $('#lableGameTitle').text(gameDetails.gameTitle);
    $('#lableCurrentPlayer').text(gameDetails.currPlayer);
    $('#lableRound').text(gameDetails.currRound + " / " + gameDetails.totalRounds);
    $('#lableCurrentMove').text( gameDetails.cuurMove + " / 2");

    if  (gameDetails.winnerName !== undefined || gameDetails.finishAllRound === true){
        if (gameDetails.technicalVictory === true) {
            openPopup("Technical Victory To " + gameDetails.winnerName + "!!!");
        } else {
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
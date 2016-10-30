var refreshRate = 2000; //miliseconds

$(document).ready(function ()
{
    ajaxIsAlreadyPlaying();

    $.ajaxSetup({cache: false});
    $('#joinGameButton').hide();
    $('#showBoard').hide();
    

    ajaxUsersAndGameList();
    setInterval(ajaxUsersAndGameList, refreshRate);

    $('#joinGameButton').on("click", joinGame);
    $('#showBoard').on("click",openPopupWithBoard);
    $('#buttonLogOut').on("click", logOut);

    $('#uploadButton').on("click", function (event) {
        event.preventDefault();
        $('#xmlFile').trigger('click');
    });

    $('#xmlFile').change(function () {
        if(valideFileExtension($("#xmlFile").val())){
            uploadFile();
            $('#xmlFile').val("");
        }
    })

});

$(document).on("click", "#gameTable tr", function(e){
    $(this).addClass('success').siblings().removeClass('success');
    $('#joinGameButton').fadeIn(200);
    $('#showBoard').fadeIn(200);
});


function valideFileExtension(file)
{
    var ext = file.split(".");
    ext = ext[ext.length-1].toLocaleLowerCase();
    var arrayExtensions = ["xml"];

    if(arrayExtensions.lastIndexOf(ext) == -1){
        $('#xmlFile').val("");
        openPopup("not support this files");
        return false;
    }
    return true;
}

function joinGame()
{
    $('#joinGameButton').hide();

    var gamettl = $("#gameTable").find("tr.success").attr('value');

    var actionType = "JoinGame";

    $.ajax({
        url: "lobby",
        type: 'GET',
        data: {
          "gameTitle": gamettl,
          "ActionType": actionType
        },
        success: function(success) {
            if (success === true){
                window.location.replace("GameRoom.html");
            } else {
                openPopup("Can not get into this Game.");
            }
        }
    });
}

function logOut()
{
    var actionType = "Logout";

    $.ajax({
        url: "lobby",
        type: 'GET',
        data:{
            "ActionType": actionType
        },
        success: function(data) {
            window.location.replace("Login.html");
        },
        error: function (data) {
            openPopup("There was an error. Please try again.")
        }
    });
}

function ajaxUsersAndGameList()
{
    var actionType = "GameAndUserList";

    $.ajax({
        url: "lobby",
        type: 'GET',
        data:{
            "ActionType": actionType
        },
        success: function(data) {
            refreshUsersAndGameList(data);
        },
        error: function (data) {
            console.log(data);
        }
    });
}

function refreshUsersAndGameList(data)
{
    var usersList = data[0];
    var gamesList = data[1];

    refreshUsersList(usersList);
    refreshGameList(gamesList);
}

function refreshGameList(games)
{
    var selected = $("#gameTable").find("tr.success").attr('value');

    $("#gameTable").empty();
    $.each(games || [], function (gameKey, gameValue)
    {
        var gameList = $('<tr value="'+gameKey+'"> </tr>');
        var bordSize = gameValue.m_OrginalBoard.m_Rows + " * " + gameValue.m_OrginalBoard.m_Cols;
        var players = gameValue.m_Players.length + " / " + gameValue.m_TotalPlayers;

        var gameStatus;
        if(gameValue.m_ActiveGame && gameValue.m_Players.length < gameValue.m_TotalPlayers){
            gameStatus = "Available"
        }else {
            gameStatus = "Not Available"
        }

        $('<th>' + gameKey + '</th>').appendTo(gameList);
        $('<th>' + gameValue.m_Organizer + '</th>').appendTo(gameList);
        $('<th>' + players + '</th>').appendTo(gameList);
        $('<th>' + gameValue.m_TotalRounds + '</th>').appendTo(gameList);
        $('<th>' + bordSize + '</th>').appendTo(gameList);
        $('<th>' + gameStatus + '</th>').appendTo(gameList);
        gameList.appendTo($("#gameTable"));

        if(gameKey == selected) {
            gameList.addClass('success');
        }
    })
}

function refreshUsersList(users)
{
    //clear all current users
    $("#usersTable").empty();
    $.each(users || [], function(index, user) {
        // console.log("Adding user #" + index + ": " + name);
        var icon;
        if(user.userType === 'Human') {
            icon = "<span class='HumanIcon'></span>"
        }
        else {
            icon = "<span class='MachineIcon'></span>"
        }

        var userList = $('<tr class="active"> </tr>')
        $('<th>' + user.userName + '</th>').appendTo(userList);
        $('<th>' + icon + '</th>').appendTo(userList);
        userList.appendTo($("#usersTable"));
    });
}

function ajaxIsAlreadyPlaying()
{
    var actionType = "CheckUserPlaying";

    $.ajax({
        url: "lobby",
        type: 'GET',
        data:{
            "ActionType": actionType
        },
        success: function(isAlreadyPlaying) {
            if (isAlreadyPlaying === true){
                window.location.replace("GameRoom.html");
            }
        }
    });
}

function uploadFile()
{
    var formData = new FormData();
    formData.append('xmlFileName', $('#xmlFile')[0].files[0]);

    $.ajax({
        url: "lobby",
        data: formData,
        type: 'POST',
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        dataType: 'json',
        success: function (message) {
            openPopup(message);
        }
    })
}

function openPopup(msg) {
    $("#message").html(msg);
    $("#popup").show();
}

function closePopup() {
    $("#popup").hide();
    $("#message").removeClass('boardMessage');
}

function openPopupWithBoard(){
    $("#message").addClass('boardMessage');
    $("#message").empty();

    var table = $("<table></table>");
    $("#message").append(table);
    getShowBoard(table);

    $("#popup").show();
}


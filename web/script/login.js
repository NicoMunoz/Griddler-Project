
$(document).ready(function ()
{
    $('#buttonLogin').on("click", checkUserNameAndType);
    $("#radioHuman").prop("checked", true);

    ajaxIsAlreadyLogIn();
});

function checkUserNameAndType()
{
    var inputFromUser = $('#inputFromUser').val();

    if(inputFromUser == ""){
        openPopup("Must fill User Name.");
    }
    else {
        var UserType = $('input[name=UserType]:checked').val();
        var actionType = "PerformLogIn";

        $.ajax({
            url: "login",
            data:{
                "username": inputFromUser,
                "usertype": UserType,
                "ActionType": actionType
            },
            success: function(isNameExist) {
                if (isNameExist === true){
                    openPopup("This user name is already in the system.");
                } else {
                    window.location.replace("Lobby.html");
                }
            }
        });
    }
}

function ajaxIsAlreadyLogIn()
{
    var actionType = "CheckUserLogIn";

    $.ajax({
        url: "login",
        data:{
            "ActionType": actionType
        },
        success: function(isAlreadyLogIn) {
            if (isAlreadyLogIn === true){
                window.location.replace("Lobby.html");
            }
        }
    });
}

function openPopup(msg) {
    $("#message").html(msg);
    $("#popup").show();
}

function closePopup() {
    $("#popup").hide();
}


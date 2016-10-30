$(document).on("click", "td.toggler", function () {
    $(this).toggleClass('selected');
});

$(document).ready(function () {
    $('#buttonPassTurn').on("click", ajaxPassTurn);
});

$(document).on("click", "#buttonPerformeMove", function (e) {
    var userAction = $('.optionSelected').html();
    switch (userAction) {
        case "Mark":
            playerMoveCase("filled");
            break;
        case "UnMark":
            playerMoveCase("empty");
            break;
        case "UnKnown":
            playerMoveCase("undefined");
            break;
        case "Undo":
            handleUndoRedo("Undo");
            break;
        case "Redo":
            handleUndoRedo("Redo");
            break;
        default:

    }
});

function playerMoveCase(userAction) {
    if ($('.selected').length > 0) {
        doMove(userAction);
    } else {
        openPopup("You need to select at least one cell")
    }
}

function ajaxPassTurn() {
    var actionType = "PassTurn";
    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success: function (result) {
            if (result === false    ) {
                openPopup("its not your turn");
            }
        },
    });

}




function doMove(move) {
    var actionType = "doMove";
    var selectedCoords = [];
    $("#board").find("td.toggler.selected").each(function () {
        var row = $(this).attr("row");
        var column = $(this).attr("column");
        selectedCoords.push(new Coordinate(row, column));
    });

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType,
            "requestType": move,
            "selectedCoords": JSON.stringify(selectedCoords),
        },
         success: function (result) {
            if (result[0]) {    // already had the list, so just verify by the logics server
                 $("#board").find("td.toggler.selected").each(function () {
                    $(this).toggleClass('selected');
                    $(this).removeClass(this.className.split(' ').pop());
                     $(this).addClass(move);
                });
                 updatePerfectBlock(result[1], result[2]);
            } else {
                openPopup(result[1]);
            }
             removeSelectedSquares();
         }
    });
}

function updatePerfectBlock(rowBlock, colBlock) {
    updateSpecificBlocks(rowBlock,true);
    updateSpecificBlocks(colBlock,false)
}

function updateSpecificBlocks (blocks,isRow) {
    var direction = (isRow) ? "row" : "column";
    for(var currSlice = 0; currSlice <blocks.length ; currSlice++)
    {
        for(var currBlock = 0 ;currBlock < blocks[currSlice].length ; currBlock++)
        {
            if(blocks[currSlice][currBlock].m_IsPerfect)
            {
                $('td[' + direction + '=' + currSlice + '][blockIndex=' + currBlock + ']').addClass("perfected");
            }
            else
            {
                $('td[' + direction + '=' + currSlice + '][blockIndex=' + currBlock + ']').removeClass("perfected");
            }
            }
        }

}

function Coordinate(row, column) {
    this.key = row;
    this.value = column;
}

function handleUndoRedo(undoOrRedo) {
    removeSelectedSquares();
    var actionType = undoOrRedo;

    $.ajax({
        data: {
            "ActionType": actionType
        },
        url: "gamingRoom",
        success: function (result) {

            updateBoard(result[0]);
            updatePerfectBlock(result[1], result[2]);
        },
        error:function(e){
            console.log(e);
        }
    });
}
function updateBoard(move) {

    var lenMoves = move.length;
    for (i = 0; i < lenMoves; i++) {
        var td = $('[row="' + move[i].row + '"][column="' + move[i].col + '"]');
        td.removeClass(td.attr('class').split(' ').pop());
        td.addClass(move[i].cellType.toLowerCase());
    }
}

function removeSelectedSquares() {
    $("#board").find("td.toggler.selected").each(function () {
        $(this).removeClass('selected');
    });
}
/**
 * Created by nicom on 10/28/2016.
 */




function getShowBoard(table)
{
    var actionType = "getShowBoard";
    var gamettl = $("#gameTable").find("tr.success").attr('value');

    $.ajax({
        url: "gamingRoom",
        data: {
            "gameTitle": gamettl,
            "ActionType": actionType
        },
        success: function (board) {
            createBoard(board, table);
        },
        error: function (data) {
            console.log(data);
        }
    });
}
function getBoard(table) {
    var actionType = "getBoard";

    $.ajax({
        url: "gamingRoom",
        data: {
            "ActionType": actionType
        },
        success: function (board) {
            createBoard(board, table);
        },
        error: function (data) {
            console.log(data);
        }
    });
}

function createBoard(theBoard,table) {
    console.log("board create");
    table.addClass("myTable");
    createTopPart(theBoard, table);
    createDownPart(theBoard, table);
}


function createTopPart(board, table) {
    var theBoard = board.board;
    var columns = theBoard[0].length;
    var columnsBlocks = board.columnsBlocks;
    var maxRowBlock = getMaxLengthOfList(board.rowsBlocks);
    var maxColumnBlock = getMaxLengthOfList(columnsBlocks);

    for (var row = 0; row < maxColumnBlock; row++) {
        var tr = document.createElement('tr');
        for (var column = 0; column < columns + maxRowBlock; column++) {
            var td = document.createElement('td');
            td.setAttribute("column",column- maxRowBlock);
            td.classList.add("block");
            if (column >= maxRowBlock && maxColumnBlock - row <= columnsBlocks[column - maxRowBlock].length) {
                var index =columnsBlocks[column - maxRowBlock].length - maxColumnBlock + row;
                td.setAttribute("blockIndex",index);
                td.innerText = columnsBlocks[column - maxRowBlock][index].m_Value;
            }
            tr.appendChild(td);
        }
        table.append(tr);
    }
}

function createDownPart(board, table) {
    var theBoard = board.board;
    var rowsBlocks = board.rowsBlocks;
    var columnsBlocks = board.columnsBlocks;
    var rows = theBoard.length;
    var columns = theBoard[0].length;
    var maxRowBlock = getMaxLengthOfList(rowsBlocks);

    for (var row = 0; row < rows; row++) {
        var tr = document.createElement('tr');
        for (var column = 0; column < columns + maxRowBlock; column++) {
            var td = document.createElement('td');
            td.setAttribute("row", row);
            if (column < maxRowBlock && maxRowBlock - column <= rowsBlocks[row].length) {
                td.classList.add("block");
                var index = [rowsBlocks[row].length - maxRowBlock + column];
                td.setAttribute("blockIndex",index);
                td.innerText = rowsBlocks[row][index].m_Value;
            }
            else if (column >= maxRowBlock) {   // add class according to real board
                td.setAttribute("column", (column - maxRowBlock));
                td.classList.add("toggler");
                td.classList.add(theBoard[row][column - maxRowBlock].toLowerCase());
            }
            else {
                td.classList.add("block");
            }
            tr.appendChild(td);
        }

        table.append(tr);
    }
}

function getMaxLengthOfList(list) {
    var retValue = 0;
    for (var i = 0; i < list.length; i++) {
        if (list[i].length > retValue) {
            retValue = list[i].length;
        }
    }
    return retValue;
}
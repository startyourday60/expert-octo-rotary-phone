package connectfour

// constants
const val BOARD_DEF_VALUE = '║' // '='
const val BOARD_DEF_VALUE_LEFT = '╚' // '='
const val BOARD_DEF_VALUE_RIGHT = '╝' // '='
const val BOARD_DEF_VALUE_CENTER = '╩' // '='
const val BOARD_DEF_VALUE_BOTTOM = '═' // ═
const val BOARD_DEF_VALUE_PADCHR = ' ' // |$BOARD_DEF_VALUE_PADDING_CHAR|
const val BOARD_SIZE_PADDING = 1 // is have to be like to magic constant

const val BOARD_DEF_SIZE_ROW = 6
const val BOARD_DEF_SIZE_COLUMN = 7
// typealiases
typealias BOARD_DEF_ELEMENT_TYPE = Char
typealias boardType = MutableList<MutableList<BOARD_DEF_ELEMENT_TYPE>>
	
// is maximal values for board range. correctly
const val BOARD_RANGE_START = 5
const val BOARD_RANGE_END = 9

// global variables and values
var gameIsRunned = true
const val debugEnabled = false
var countGames: Int = 1 //? = null




class Board(var rows: Int? = null, var columns: Int? = null, var boardRange: IntRange = BOARD_RANGE_START..BOARD_RANGE_END ) {
	var s_board: boardType
	var sPlayerScore: Int = 0; var fPlayerScore: Int = 0
	var sPlayer: String; var fPlayer: String
	init {
		val (_fPlayer, _sPlayer) = initPlayers() // first player and second player
		fPlayer = _fPlayer
		sPlayer = _sPlayer
		if (rows == null || columns == null) s_board = initialization()
		else s_board = initialization(rows!!,columns!!)
	}
	
	fun clear() {
		this.s_board = initialization(rows!!, columns!!)
	}

	// [deprecated comment] maybe is weird. need to Fix. maybe exists typedef. yet weird. so maybe in future...

	// init board function. return boardType
	fun initialization(rows: Int, columns:Int):boardType {
	    this.rows = rows
	    this.columns = columns
	    
	    if (rows < 1 || columns < 1) throw Exception("Bad row/columns size")
	    val ret = mutableListOf<MutableList<BOARD_DEF_ELEMENT_TYPE>>()
	    repeat(rows){
		val l = mutableListOf<BOARD_DEF_ELEMENT_TYPE>()
		repeat(columns+BOARD_SIZE_PADDING){
		    l.add(BOARD_DEF_VALUE) // |
		    l.add(BOARD_DEF_VALUE_PADCHR) // ' ' // space
		}
		ret.add(l)
	    } // is all parts that is not bottom.
	    // last part of our board. is bottom part.
	    val l = mutableListOf<BOARD_DEF_ELEMENT_TYPE>()
	    for (ind in 1..columns+BOARD_SIZE_PADDING){
		when(ind){
		    1 -> l.add(BOARD_DEF_VALUE_LEFT)
		    (columns+BOARD_SIZE_PADDING) -> l.add(BOARD_DEF_VALUE_RIGHT)
		    else -> {l.add(BOARD_DEF_VALUE_CENTER)} /*if(ind%2 == 0) l.add('╩') else l.add('═')*/
		}
	    }
	    ret.add(l)
	    return ret
	}

	fun getSize(board: boardType = this.s_board) : Pair<Int,Int> {
	    val x = board.first().size-BOARD_SIZE_PADDING
	    val y = board.size-BOARD_SIZE_PADDING
	    return Pair(x/2,y)
	}

	fun calculateColumn(column : Int) : Int{
	    return column*2-BOARD_SIZE_PADDING // maybe uint
	}

	// if the column is not full or if is not full return with index
	// if false then column is full
	fun boardColumnIsFree(board: boardType = this.s_board, column:Int) : Pair<Boolean,Int>{
	    for (boardRowIndex in 1 until board.reversed().size step 1){
		if(board.reversed()[boardRowIndex][calculateColumn(column)]==BOARD_DEF_VALUE_PADCHR){
		    return Pair(true,boardRowIndex)
		}
	    }
	    return Pair(false,0)
	}
	fun columnIsFull(board: boardType = this.s_board, column : Int ) : Boolean {
	    val (isFree, _) = boardColumnIsFree(board, column)
	    return !isFree
	}

	// add charToBoardInColumn.
	fun addChar(board: boardType = this.s_board, column:Int, ch: Char) : boardType {
	    val reversedBoard = board.reversed().toMutableList()
	    val maxColumn =
		    board.first().size/2-BOARD_SIZE_PADDING; // so so weird. fix it future.
	    if (column > maxColumn) throw Exception("outsize")
	    val (isFree, b) = boardColumnIsFree(board, column)
	    if( isFree ){
		reversedBoard[b][calculateColumn(column)]=ch
	    }else throw Exception("Column $column is full")
	    return reversedBoard.reversed().toMutableList()
	}


	// it is a little deprecated, but correctly  print board
	fun print(board: boardType = this.s_board){
	    //println()

	    //var c:Int = 1;
	    //println("Board:")
	    try {
		for( i in 1 /*until*/ .. board.first().size/2-BOARD_SIZE_PADDING ){
		    print(" $i")
		}
		println()
		for(l in board){
		    //print(c.toString()+" ") // left part of number
		    if(l != board.last())
		        println(l.joinToString(""))
		    else
		        println(l.joinToString(BOARD_DEF_VALUE_BOTTOM.toString()))
		    //c++;
		}
		println()
	    } catch (exc: Exception){
		println(exc.toString().split(": ")[1])
	    }
	}
	// initialization of board
	fun initialization() :boardType {
	    // see it regex on the website NxN or N X N or just NOTHING where N is numeric
	    val boardRegex = Regex("^(([0-9])+( ?|\\t?)*(x|X)( ?|\\t?)*([0-9])+)?\$") // https://regex101.com/
	    var tmp: String // temporarily string

	    while(true){
		println("Set the board dimensions (Rows x Columns)")
		println("Press Enter for default ($BOARD_DEF_SIZE_ROW x $BOARD_DEF_SIZE_COLUMN)")
		tmp = readln().trim()
		if (boardRegex.matches(tmp)) break
		println("Invalid input")
	    }
	    if (tmp == "") return initialization(BOARD_DEF_SIZE_ROW, BOARD_DEF_SIZE_COLUMN) // init default size board

	    // get left and right part of our string, with numbers
	    var splinted : List<String> = listOf<String>();

	    // [deprecated comment] when?
	    if ('x' in tmp) {
		splinted = tmp.split('x')
	    }else if('X' in tmp) { // TODO: maybe 'x'.toUpperCase() 'x'.uppercase()? maybe. just constant?
		splinted = tmp.split("X")
	    }
	    //val splitted = tmp.split('x')

	    // if count of numbers is not 2
	    if ( splinted.size != 2 ) throw Exception("regex is broken in initialization")

	    val x = splinted[0].trim().toIntOrNull()?:0
	    val y = splinted[1].trim().toIntOrNull()?:0
	    if (!(x in boardRange)){
		println("Board rows should be from $BOARD_RANGE_START to $BOARD_RANGE_END") // maybe throw but not needable
		return initialization()
	    }
	    if(!(y in boardRange)){ // if boards columns is out
		println("Board columns should be from $BOARD_RANGE_START to $BOARD_RANGE_END")
		return initialization() // is overloading who is not know
	    }
	    return initialization(x,y)
	}	
	
	fun printScore() {
		println("Score")
		println("$fPlayer: $fPlayerScore $sPlayer: $sPlayerScore")
	}
	
	// action of some player on board. ch default value is 'o'
	// Unit is like to void is some languages
	fun turn(board: boardType = this.s_board, playerName: String, maxColumn : Int, ch: Char = 'o') : Unit {
	    // there is while true because we don't want stack overflow
	    while(gameIsRunned) {
		println("$playerName's turn:")
		val playerActionRaw = readln()
		val playerAction = playerActionRaw.toIntOrNull()?:-1 // if is null then is -1
		if (playerAction == -1){
		    if(playerActionRaw == "end"){
		        gameIsRunned = false
		        break;
		    }// weird
		    println("Incorrect column number")
		    continue;
		}else if (playerAction > maxColumn || playerAction == 0){
		    println("The column number is out of range (1 - $maxColumn)")
		    continue;
		}
		try {
		    addChar(board, playerAction, ch)
		    break
		} catch (exc: Exception){
		    println(exc.toString().split(": ")[1] )
		}
	    }
	    if (gameIsRunned && checkToCharacterRepetition(board, ch)){
		gameIsRunned = false
		this.print(board)
		println("Player $playerName won")
		
		if (ch == 'o') fPlayerScore++ // by future way better is create class for players. but for our is optionally for now...
		else if(ch == '*') sPlayerScore++
	    }
	    if ( gameIsRunned && isAllColumsFull(board) ){
		gameIsRunned = false
		this.print(board)
		println("It is a draw")
	    }



	}
	
	fun isAllColumsFull(board: boardType = this.s_board) : Boolean {
	    for( column in 1 until board.first().size/2-BOARD_SIZE_PADDING ){ // so so weird. fix it future
		if( columnIsFull(board, column) == true ) continue // we can to check to last character just?
		else return false
	    }
	    return true
	}


	// is very weird function. but is work.
	// is very weird function. but is work.
	// is very weird function. but is work.
	// is very weird function. but is work.
	// is very weird function. but is work. ?!?

	fun checkOnTop( board: boardType = this.s_board, ch: Char, needCount: Int = 4) : Boolean{
	    val reversedBoard = board.reversed().toMutableList()
	    var found = false
	    LoopColumnIndex@for( boardIndexColumn in 1 until
		    reversedBoard.first().size ) {
		//printDebugMessage("LoopColumnIndex")
		LoopRowIndex@for( boardIndexRaw in 1 until reversedBoard.size) { // 0 is bottom part.
		    //printDebugMessage("$boardIndexRaw ($boardIndexColumn) ${calculateColumn(boardIndexColumn)}")
		    //printDebugMessage( reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)] )
		    if(reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)]  == ch) { // 1. found character
		        printDebugMessage("Found character. now loop")
		        //
		        LoopcheckToStrTop@for( i in 0 until needCount ) {
		            val nextIndexRaw = if (boardIndexRaw+i >= reversedBoard.size) -1 else boardIndexRaw+i
		            val nextIndexColumnRaw = if(boardIndexColumn+i >= reversedBoard.first().size ) -1 else boardIndexColumn+i

		            val nextIndexColumn = calculateColumn(nextIndexColumnRaw)
		            val currentIndexColumn = calculateColumn(boardIndexColumn)

		            printDebugMessage("(currentIndexColumn:boardIndexRaw) $boardIndexColumn:$boardIndexRaw")
		            printDebugMessage("(NEXTINDEX:NEXTCOLUMN) $nextIndexRaw:$nextIndexColumn")

		            if((nextIndexRaw>0 && reversedBoard[nextIndexRaw][currentIndexColumn] == ch)) {
		                printDebugMessage("$boardIndexColumn+$i = $ch")
		                found = true
		            }
		            else{
		                printDebugMessage("$boardIndexColumn+$i != $ch; break")
		                found = false
		                break
		            }
		        }
		        if (found) return true
		    }
		}
	    }
	    return found
	}

	fun checkOnLeftRight( board: boardType = this.s_board, ch: Char, needCount: Int = 4) : Boolean {
	    val reversedBoard = board.reversed().toMutableList()
	    var found = false
	    LoopColumnIndex@for( boardIndexColumn in 1 until
		    reversedBoard.first().size ) {
		//printDebugMessage("LoopColumnIndex")
		LoopRowIndex@for( boardIndexRaw in 1 until reversedBoard.size) { // 0 is bottom part.
		    //printDebugMessage("$boardIndexRaw ($boardIndexColumn) ${calculateColumn(boardIndexColumn)}")
		    //printDebugMessage( reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)] )
		    if(reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)]  == ch) { // 1. found character
		        printDebugMessage("Found character. now loop")
		        //
		        LoopcheckToStrTop@for( i in 0 until needCount ) {
		            val nextIndexRaw = if (boardIndexRaw+i >= reversedBoard.size) -1 else boardIndexRaw+i
		            val nextIndexColumnRaw = if(boardIndexColumn+i >= reversedBoard.first().size ) -1 else boardIndexColumn+i

		            val nextIndexColumn = calculateColumn(nextIndexColumnRaw)
		            val currentIndexColumn = calculateColumn(boardIndexColumn)

		            printDebugMessage("(currentIndexColumn:boardIndexRaw) $boardIndexColumn:$boardIndexRaw")
		            printDebugMessage("(NEXTINDEX:NEXTCOLUMN) $nextIndexRaw:$nextIndexColumn")

		            if( (nextIndexColumn>0 && reversedBoard[boardIndexRaw][nextIndexColumn] == ch ) ) {
		                printDebugMessage("$boardIndexColumn+$i = $ch")
		                found = true
		            }
		            else{
		                printDebugMessage("$boardIndexColumn+$i != $ch; break")
		                found = false
		                break
		            }
		        }
		        if (found) return true
		    }
		}
	    }
	    return found
	}

	fun checkOnRightTop( board: boardType = this.s_board, ch: Char, needCount: Int = 4) : Boolean {
	    val reversedBoard = board.reversed().toMutableList()
	    var found = false
	    LoopColumnIndex@for( boardIndexColumn in 1 until
		    reversedBoard.first().size ) {
		//printDebugMessage("LoopColumnIndex")
		LoopRowIndex@for( boardIndexRaw in 1 until reversedBoard.size) { // 0 is bottom part.
		    //printDebugMessage("$boardIndexRaw ($boardIndexColumn) ${calculateColumn(boardIndexColumn)}")
		    //printDebugMessage( reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)] )
		    if(reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)]  == ch) { // 1. found character
		        printDebugMessage("Found character. now loop")
		        //
		        LoopcheckToStrTop@for( i in 0 until needCount ) {
		            val nextIndexRaw = if (boardIndexRaw+i >= reversedBoard.size) -1 else boardIndexRaw+i
		            val nextIndexColumnRaw = if(boardIndexColumn+i >= reversedBoard.first().size ) -1 else boardIndexColumn+i

		            val nextIndexColumn = calculateColumn(nextIndexColumnRaw)
		            val currentIndexColumn = calculateColumn(boardIndexColumn)

		            printDebugMessage("(currentIndexColumn:boardIndexRaw) $boardIndexColumn:$boardIndexRaw")
		            printDebugMessage("(NEXTINDEX:NEXTCOLUMN) $nextIndexRaw:$nextIndexColumn")

		            if( (nextIndexRaw>0&&nextIndexColumn>0&&reversedBoard[nextIndexRaw][nextIndexColumn] == ch ) ) {
		                printDebugMessage("$boardIndexColumn+$i = $ch")
		                found = true
		            }
		            else{
		                printDebugMessage("$boardIndexColumn+$i != $ch; break")
		                found = false
		                break
		            }
		        }
		        if (found) return true
		    }
		}
	    }
	    return found
	}

	fun checkOnLeftBottom( board: boardType = this.s_board, ch: Char, needCount: Int = 4) : Boolean {
	    val reversedBoard = board.reversed().toMutableList()
	    var found = false
	    LoopColumnIndex@for( boardIndexColumn in 1 until
		    reversedBoard.first().size ) {
		//printDebugMessage("LoopColumnIndex")
		LoopRowIndex@for( boardIndexRaw in 1 until reversedBoard.size) { // 0 is bottom part.
		    //printDebugMessage("$boardIndexRaw ($boardIndexColumn) ${calculateColumn(boardIndexColumn)}")
		    //printDebugMessage( reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)] )
		    if(reversedBoard[boardIndexRaw][calculateColumn(boardIndexColumn)]  == ch) { // 1. found character
		        printDebugMessage("Found character. now loop")
		        //
		        LoopcheckToStrTop@for( i in 0 until needCount ) {
		            val nextIndexRaw = if (boardIndexRaw-i < 0 ) -1 else boardIndexRaw-i
		            val nextIndexColumnRaw = if(boardIndexColumn+i >= reversedBoard.first().size ) -1 else boardIndexColumn+i

		            val nextIndexColumn = calculateColumn(nextIndexColumnRaw)
		            val currentIndexColumn = calculateColumn(boardIndexColumn)

		            printDebugMessage("(currentIndexColumn:boardIndexRaw) $boardIndexColumn:$boardIndexRaw")
		            printDebugMessage("(NEXTINDEX:NEXTCOLUMN) $nextIndexRaw:$nextIndexColumn")

		            if( (nextIndexRaw>0&&nextIndexColumn>0&&reversedBoard[nextIndexRaw][nextIndexColumn] == ch ) ) {
		                printDebugMessage("$boardIndexColumn+$i = $ch")
		                found = true
		            }
		            else{
		                printDebugMessage("$boardIndexColumn+$i != $ch; break")
		                found = false
		                break
		            }
		        }
		        if (found) return true
		    }
		}
	    }
	    return found
	}



	fun checkToCharacterRepetition( board: boardType = this.s_board, ch: Char, needCount : Int = 4 ) : Boolean {
		if (checkOnTop(board, ch, needCount)) {
			printDebugMessage("Board found in top value")
			return true
		} 
		if (checkOnLeftRight(board, ch, needCount)) {
			printDebugMessage("Board found in top value")
			return true
		} 
		if (checkOnRightTop(board, ch, needCount)) {
			printDebugMessage("Board found in top value")
			return true
		} 
		if (checkOnLeftBottom(board, ch, needCount)) {
			printDebugMessage("Board found in top value")
			return true
		} 

		return false
	}
}
// constants



// there can be some like data class player(... class players ... but not needable by some rule...
// init players with their names
fun initPlayers() :Pair<String,String> {
    println("Connect Four")
    println("First player's name:")
    val firstPlayerName = readln()
    println("Second player's name:")
    val secondPlayerName = readln()
    return Pair<String,String>(firstPlayerName, secondPlayerName)
}

// print message if debug is enabled
fun printDebugMessage( msg : String ) {
    if (debugEnabled) println("DEBUG: $msg")
}

// init game
fun initGames() {
 println("Do you want to play single or multiple games?")
 println("For a single game, input 1 or press Enter")
 println("Input a number of games:")
 val input = readln()
 if (input == "\n" || input == "") countGames = 1
 else countGames = input.toInt()
}



// weird because old style of program is not OOP. and need of refactoring, but sometimes better way "Leave as it is already". just for readable - board class.
fun main() {
     
 val myBoard = Board()
 val (x,y) = myBoard.getSize() // TODO("fix it some laters")

 initGames()
 var isGameNumber: Int = 1
 var fPlayer = myBoard.fPlayer
 var sPlayer = myBoard.sPlayer
 do{
	 println("$fPlayer VS $sPlayer")
	 println("$y X $x board")
	 when(countGames) {
	 	1 -> {
	 		println("Single game")
	 	}
	 	else -> println("Game #$isGameNumber")
	 }
	 while(gameIsRunned) {
		if (gameIsRunned) myBoard.print()
		myBoard.turn(playerName = fPlayer, maxColumn = x)

		if (gameIsRunned) myBoard.print()
		myBoard.turn(playerName = sPlayer, maxColumn = x, ch ='*')
	 } 
 countGames--
 isGameNumber++
 if (countGames > 0) {
  myBoard.printScore() 
  gameIsRunned = true
  myBoard.clear()
 }
 
 }while (countGames > 0)
 
 if(isGameNumber > 1) myBoard.printScore() 
 println("Game over!")
}

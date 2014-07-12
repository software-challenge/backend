unit UMyBoard;

// Hier können eigene Funktionen implementiert werden, um die Funktionalität
// des Boards zu erweitern

interface
  uses UBoard, UDefines, UInteger, UPlayer, UMove, URunMove, USetMove, UField, Classes, UUtil, SysUtils, Contnrs;

  type
    TMyBoard = class(TBoard)
     public
      function getPossibleRunMoves : TObjectList;
      function getPossibleSetMoves : TObjectList;
      
      function getPossibleRunMovesForPlayer(playerId : Integer) : TObjectList;
      function getPossibleMovesForPenguin(x : Integer; y : Integer) : TObjectList;

    end;
implementation
  uses Math;

  function TMyBoard.getPossibleRunMoves : TObjectList;
  begin
    Result := Self.getPossibleRunMovesForPlayer(CurrentPlayer);
  end;

	function TMyBoard.getPossibleRunMovesForPlayer(playerId : Integer) : TObjectList;
  var
    Moves : TObjectList;
    MovesTmp : TObjectList;
    i, x, y : Integer;
  begin
		Moves := TObjectList.Create(true);
		for x := 0 to 7 do begin
			for y := 0 to 7 do begin
        if Self.hasPlayerPenguin(x, y, playerId) then begin
				  MovesTmp := Self.getPossibleMovesForPenguin(x, y);
          for i := 0 to MovesTmp.Count - 1 do
            Moves.Add(MovesTmp.Items[i]);
        end;
      end;
    end;
		Result := Moves;
	end;

	function TMyBoard.getPossibleMovesForPenguin(x : Integer; y : Integer) : TObjectList;
  var
    Moves : TObjectList;
    Done : Boolean;
    CurrentX : Integer;
    CurrentY : Integer;
  begin
    Moves := TObjectList.Create(true);
    if Self.hasPenguin(x, y) then begin
      Done := false;
		  CurrentX := x - 1;
		  while not Done do begin
			  if (CurrentX < 0) or (Self.getPenguin(CurrentX, y) <> nil) or (Self.getFishNumber(CurrentX, y) = 0) then
				  Done := true
			  else begin
				  Moves.Add(TRunMove.Create(x, y, CurrentX, y));
				  CurrentX := CurrentX - 1;
        end;
			end;

      Done := false;
		  CurrentX := x + 1;
		  while not Done do begin
			  if (CurrentX > 7) or (Self.getPenguin(CurrentX, y) <> nil) or (Self.getFishNumber(CurrentX, y) = 0) then
				  Done := true
			  else begin
				  Moves.Add(TRunMove.Create(x, y, CurrentX, y));
				  CurrentX := CurrentX + 1;
        end;
			end;

      Done := false;
		  if y mod 2 = 0
        then CurrentX := x
        else CurrentX := x - 1;
      CurrentY := y - 1;
      while not Done do begin
			  if (CurrentX < 0) or (CurrentY < 0) or (Self.getPenguin(CurrentX, CurrentY) <> nil) or (Self.getFishNumber(CurrentX, CurrentY) = 0) then
				  Done := true
			  else begin
				  Moves.Add(TRunMove.Create(x, y, CurrentX, CurrentY));
          if CurrentY mod 2 = 1
            then CurrentX := CurrentX - 1;
          CurrentY := CurrentY - 1;
        end;
			end;

      Done := false;
		  if y mod 2 = 1
        then CurrentX := x
        else CurrentX := x + 1;
      CurrentY := y - 1;
      while not Done do begin
			  if (CurrentX > 7) or (CurrentY < 0) or (Self.getPenguin(CurrentX, CurrentY) <> nil) or (Self.getFishNumber(CurrentX, CurrentY) = 0) then
				  Done := true
			  else begin
				  Moves.Add(TRunMove.Create(x, y, CurrentX, CurrentY));
          if CurrentY mod 2 = 0
            then CurrentX := CurrentX + 1;
          CurrentY := CurrentY - 1;
        end;
			end;

      Done := false;
		  if y mod 2 = 1
        then CurrentX := x
        else CurrentX := x + 1;
      CurrentY := y + 1;
      while not Done do begin
			  if (CurrentX > 7) or (CurrentY > 7) or (Self.getPenguin(CurrentX, CurrentY) <> nil) or (Self.getFishNumber(CurrentX, CurrentY) = 0) then
				  Done := true
			  else begin
				  Moves.Add(TRunMove.Create(x, y, CurrentX, CurrentY));
          if CurrentY mod 2 = 0
            then CurrentX := CurrentX + 1;
          CurrentY := CurrentY + 1;
        end;
			end;

      Done := false;
		  if y mod 2 = 0
        then CurrentX := x
        else CurrentX := x - 1;
      CurrentY := y + 1;
      while not Done do begin
			  if (CurrentX < 0) or (CurrentY > 7) or (Self.getPenguin(CurrentX, CurrentY) <> nil) or (Self.getFishNumber(CurrentX, CurrentY) = 0) then
				  Done := true
			  else begin
				  Moves.Add(TRunMove.Create(x, y, CurrentX, CurrentY));
          if CurrentY mod 2 = 1
            then CurrentX := CurrentX - 1;
          CurrentY := CurrentY + 1;
        end;
			end;
    end;

		Result := Moves;
	end;

  function TMyBoard.getPossibleSetMoves : TObjectList;
  var
    Moves : TObjectList;
    x, y : Integer;
  begin
		Moves := TObjectList.Create(true);
		for x := 0 to 7 do begin
			for y := 0 to 7 do begin
        if (not Self.hasPenguin(x, y)) and (Self.getField(x, y).getFish = 1)
          then Moves.Add(TSetMove.create(x, y));
      end;
    end;
		Result := Moves;
	end;

end.

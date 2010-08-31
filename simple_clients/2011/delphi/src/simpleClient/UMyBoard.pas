unit UMyBoard;

interface
  uses UBoard, UDefines, UNode, UInteger, USheep, UMove, Classes, UUtil, SysUtils;

  type
    TMyBoard = class(TBoard)
      private
        procedure getNeighbourFieldsHelper(Field : Integer; Distance : Integer; Fields : TList; VisitedFields : TList);
      public
        procedure getNeighbourFields(Field : Integer; Distance : Integer; Fields : TList);
        function isFieldInReach(Sheep : TSheep; Field : Integer) : Boolean;
        function isFieldOccupied(Field : Integer) : Boolean;
        function isMoveValid(Move : TMove) : Boolean;
    end;
implementation
  function TMyBoard.isFieldInReach(Sheep : TSheep; Field : Integer) : Boolean;
    var
      ReachableFields : TList;
      n : Integer;
    begin
      ReachableFields := TList.Create;
      for n := 0 to Length(FDice) - 1 do begin
        getNeighbourFields(Sheep.Node, FDice[n], ReachableFields);
      end;
      Result := false;
      for n := 0 to ReachableFields.Count - 1 do begin
        if(TInteger(ReachableFields[n]).Value = Field) then begin
          Result := true;
        end;
      end;
      FreeAndNil(ReachableFields);
    end;

  function TMyBoard.isFieldOccupied(Field : Integer) : Boolean;
    var
      n : Integer;
    begin
      Result := false;
      for n := 0 to FSheeps.Count - 1 do begin
        if(TSheep(FSheeps[n]).Node = Field) then begin
          Result := true;
          exit;
        end;
      end;
    end;

  function TMyBoard.isMoveValid(Move : TMove) : Boolean;
    begin
      Result := true;
      if not (isFieldInReach(getSheep(Move.Sheep), Move.Target)) then begin
        Result := false;
      end;
      // If the field is occupied with own sheep, move is invalid
      if(isFieldOccupied(Move.Target)) then begin
        if(getSheepOnField(Move.Target).PlayerID = FCurrentPlayer) then begin
          Result := false;
        end
        else begin
          // The field is occupied with opponent's sheep
          if(getFieldType(Move.Target) = FIELD_SAVE) then begin
            // Opponent's sheep is on safety field
            if(getSheep(Move.Sheep).DogState <> DOG_ACTIVE) then begin
              // Our sheep doesn't have an active dog
              Result := false;
            end;
          end;
        end;
      end;

      // Moving to a home field that is not ours is invalid
      if(getFieldType(Move.Target) = FIELD_HOME1) and (FCurrentPlayer <> PLAYER_1) then begin
        Result := false;
      end;

      if(getFieldType(Move.Target) = FIELD_HOME2) and (FCurrentPlayer <> PLAYER_2) then begin
        Result := false;
      end;

      if(getFieldType(Move.Target) = FIELD_HOME1) or (getFieldType(Move.Target) = FIELD_HOME2) then begin
        // Target field is home field. Is it the correct home field?
        if(getSheep(Move.Sheep).Target <> Move.Target) then begin
          Result := false;
        end;
      end;
    end;

  procedure TMyBoard.getNeighbourFieldsHelper(Field : Integer; Distance : Integer; Fields : TList; VisitedFields : TList);
    var
      n : Integer;
    begin
      if(Distance = 0) then begin
        Fields.Add(TInteger.create(Field));
        exit;
      end;
      VisitedFields.Add(TInteger.create(Field));
      for n := 0 to TNode(FNodes[Field]).NeighbourCount - 1 do begin
        if not (isIntInList(TInteger(TNode(FNodes[Field]).getDirectNeighbours[n]).Value, VisitedFields)) then begin
          getNeighbourFieldsHelper(TInteger(TNode(FNodes[Field]).getDirectNeighbours[n]).Value, Distance - 1, Fields, VisitedFields);
        end;
      end;
    end;

  procedure TMyBoard.getNeighbourFields(Field : Integer; Distance : Integer; Fields : TList);
    var
      VisitedFields : TList;
    begin
      VisitedFields := TList.create;
      getNeighbourFieldsHelper(Field, Distance, Fields, VisitedFields);
      FreeAndNil(VisitedFields);
    end;

end.

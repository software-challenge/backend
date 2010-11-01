unit UBoard;

interface
  uses UPlayer, UDefines, UNode, USheep, UFlower, SysUtils, Classes;

  type
    TDice = array [0..NUM_DICE-1] of Integer;
    TBoard = class
      protected
        FNodes : array [0..BOARD_SIZE-1] of TNode;
        FPlayers : array [0..1] of TPlayer;
        FSheeps : TList;
        FFlowers : TList;
        FDice : TDice;
        FCurrentPlayer : Integer;
      public
        procedure updatePlayers(player1 : TPlayer; player2 : TPlayer);
        procedure updateSheeps(Sheeps : TList);
        procedure updateFlowers(Flowers : TList);
        procedure updateDice(Dice : TDice);

        procedure setFieldType(field : Integer; value : Integer);
        function getFieldType(field : Integer) : Integer;
        function getField(field : Integer) : TNode;
        procedure setField(Index : Integer; Node : TNode);
        function getSheep(Index : Integer) : TSheep;
        procedure getSheepsFor(PlayerID : Integer; Sheeps : TList);
        function getSheepOnField(FieldIndex : Integer) : TSheep;
        function getFlowersOnField(FieldIndex : Integer) : TFlower;
        function getDice : TDice;
        function getPlayer(PlayerID : Integer) : TPlayer;

        property Flowers : TList read FFlowers write FFlowers;
        property Sheeps : TList read FSheeps write FSheeps;
        property CurrentPlayer : Integer read FCurrentPlayer write FCurrentPlayer;

        constructor create;
        destructor destroy; override;
    end;


implementation
  destructor TBoard.destroy;
  begin
    inherited;
  end;

  function TBoard.getPlayer(PlayerID : Integer) : TPlayer;
    var
      n : Integer;
    begin
      for n := 0 to Length(FPlayers) - 1 do begin
        if(FPlayers[n].PlayerID = PlayerID) then begin
          Result := FPlayers[n];
          exit;
        end;
      end;
      Result := nil;
    end;

  function TBoard.getFlowersOnField(FieldIndex : Integer) : TFlower;
    var
      Flowers : Integer;
      n : Integer;
    begin
      Result := nil;
      for n := 0 to FFlowers.Count - 1 do begin
        if(TFlower(FFlowers[n]).Node = FieldIndex) then begin
          Result := TFlower(FFlowers[n]);
        end;
      end;
    end;

  function TBoard.getDice : TDice;
    begin
      Result := FDice;
    end;

  function TBoard.getSheepOnField(FieldIndex : Integer) : TSheep;
    var
      n : Integer;
    begin
      for n := 0 to FSheeps.Count - 1 do begin
        if(TSheep(FSheeps[n]).Node = FieldIndex) then begin
          Result := FSheeps[n];
          exit;
        end;
      end;
      Result := nil;
    end;

  procedure TBoard.getSheepsFor(PlayerID : Integer; Sheeps : TList);
    var
      n : Integer;
    begin
      for n := 0 to FSheeps.Count - 1 do begin
        if(TSheep(FSheeps[n]).PlayerID = PlayerID) then begin
          Sheeps.Add(FSheeps[n]);
        end;
      end;
    end;

  function TBoard.getSheep(Index : Integer) : TSheep;
    var
      n : Integer;
    begin
      for n := 0 to FSheeps.Count - 1 do begin
        if(TSheep(FSheeps[n]).SheepIndex = Index) then begin
          Result := FSheeps[n];
          exit;
        end;
      end;
      Result := nil;
    end;

  procedure TBoard.updateDice(Dice : TDice);
    begin
      FDice := Dice;
    end;

  procedure TBoard.updateFlowers(Flowers : TList);
    begin
      FreeAndNil(FFlowers);
      FFlowers := Flowers;
    end;

  procedure TBoard.updateSheeps(Sheeps : TList);
    begin
      FreeAndNil(FSheeps);
      FSheeps := Sheeps;
    end;

  procedure TBoard.setFieldType(field : Integer; value : Integer);
    begin
      FNodes[field].NodeType := value;
    end;

  function TBoard.getFieldType(field : Integer) : Integer;
    begin
      Result := FNodes[field].NodeType;
    end;

  procedure TBoard.updatePlayers(player1 : TPlayer; player2 : TPlayer);
    begin
      FPlayers[0] := player1;
      FPlayers[1] := player2;
    end;

  function TBoard.getField(field : Integer) : TNode;
    begin
      Result := FNodes[field]
    end;

  procedure TBoard.setField(Index : Integer; Node : TNode);
    begin
      FNodes[Index] := Node;
    end;

  constructor TBoard.create;
    var
      i : Integer;
    begin
      inherited create;
      FPlayers[0] := nil;
      FPlayers[1] := nil;
      FSheeps := TList.create;
    end;
end.

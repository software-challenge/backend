unit UBoard;

interface
  uses UPlayer, UDefines, UTower, SysUtils, Classes;

  type
    TBoard = class
      protected
        FPlayers : array [0..1] of TPlayer;
        FCurrentPlayer : Integer;
        FFirstPlayer : Integer;
        FTowers : TList;
        FIsSelection : Boolean;
      public
        procedure updatePlayers(player1 : TPlayer; player2 : TPlayer);
        procedure updateTowers(towers : TList);

        function getPlayer(PlayerID : Integer) : TPlayer;
        function isFirstPlayer(playerId : Integer) : Boolean;
        function getTowerAt(city : Integer; slot : Integer) : TTower;

        property Towers : TList read FTowers write FTowers;
        property FirstPlayer : Integer read FFirstPlayer write FFirstPlayer;
        property CurrentPlayer : Integer read FCurrentPlayer write FCurrentPlayer;
        property IsSelection : Boolean read FIsSelection write FIsSelection;

        constructor create;
        destructor destroy; override;
    end;


implementation
  destructor TBoard.destroy;
  begin
    inherited;
  end;

  function TBoard.getTowerAt(city : Integer; slot : Integer) : TTower;
    var
      n : Integer;
      tower : TTower;
    begin
      Result := nil;
      for n := 0 to FTowers.Count - 1 do begin
        tower := TTower(FTowers[n]);
        if((tower.city = city) and (tower.slot = slot)) then begin
          Result := tower;
        end;
      end;
    end;

  function TBoard.isFirstPlayer(playerId : Integer) : Boolean;
    begin
      Result := FFirstPlayer = playerId;
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

  procedure TBoard.updateTowers(towers : TList);
    begin
      FreeAndNil(FTowers);
      FTowers := towers;
    end;

  procedure TBoard.updatePlayers(player1 : TPlayer; player2 : TPlayer);
    begin
      FPlayers[0] := player1;
      FPlayers[1] := player2;
    end;

  constructor TBoard.create;
    var
      i : Integer;
    begin
      inherited create;
      FPlayers[0] := nil;
      FPlayers[1] := nil;
    end;
end.

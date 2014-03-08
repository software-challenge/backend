unit UPlayer;

(*
 * Represents a player
 *)

interface
  uses UDefines, UStone, AdomCore_4_3, Classes;
  type
    TPlayer = class
      private
        FPlayerID : Integer;      // Player's ID (0 or 1)
        FDisplayName : String;    // Player's name
        FPoints : Integer;        // Player's current score
        FStones : TList;          // Stones in player's private stash
      public
        property PlayerID : Integer read FPlayerID write FPlayerID;
        property DisplayName : String read FDisplayName write FDisplayName;
        property Points : Integer read FPoints write FPoints;
        property Stones : TList read FStones write FStones;

        function hasStoneWithSymbol(Symbol : TStoneSymbol) : Boolean;
        function hasStoneWithColor(Color : TStoneColor) : Boolean;
        function hasStoneWith(Symbol : TStoneSymbol; Color : TStoneColor) : Boolean;
        function getStonesWithSymbol(Symbol : TStoneSymbol) : TList;
        function getStonesWithColor(Color : TStoneColor) : TList;
        function getStonesWith(Symbol : TStoneSymbol; Color : TStoneColor) : TList;
        function getStoneWithID(ID : Integer) : TStone;
        function addStone(Stone : TStone) : Integer;
        procedure removeStone(Stone : TStone);

        procedure fromXml(xml : TDomNode);
        constructor Create(DisplayName : String);
        destructor destroy; override;
    end;

implementation

uses SysUtils, TypInfo;

  (*
   * Returns the stone with the given id from the player's private stash
   *)
  function TPlayer.getStoneWithID(ID : Integer) : TStone;
  var
    n : Integer;
    stone : TStone;
  begin
    for n := 0 to FStones.Count - 1 do begin
      stone := TStone(FStones[n]);
      if ID = stone.ID then begin
        Result := stone;
        Break;
      end;
    end;
  end;

  (*
   * Remove the given stone from the players private stash
   *)
  procedure TPlayer.removeStone(Stone : TStone);
  begin
    FStones.Remove(Stone);
  end;

  (*
   * Adds the given stone to the players private stash
   * Returns the new stone count in the stash
   *)
  function TPlayer.addStone(Stone : TStone) : Integer;
  begin
    if FStones.Count < MAX_PLAYER_STONES then FStones.Add(Stone);
    Result := FStones.Count;
  end;

  (*
   * Returns true if the player owns a stone with the given symbol
   *)
  function TPlayer.hasStoneWithSymbol(Symbol : TStoneSymbol) : Boolean;
  begin
    Result := getStonesWithSymbol(Symbol).Count > 0;
  end;

  (*
   * Retuns true if the player owns a stone with the given color
   *)
  function TPlayer.hasStoneWithColor(Color : TStoneColor) : Boolean;
  begin
    Result := getStonesWithColor(Color).Count > 0;
  end;

  (*
   * Returns true if the player owns a stone with the given symbol and color
   *)
  function TPlayer.hasStoneWith(Symbol : TStoneSymbol; Color : TStoneColor) : Boolean;
  begin
    Result := getStonesWith(Symbol, Color).Count > 0;
  end;

  (*
   * Returns all stones with the given symbol owned by this player
   *)
  function TPlayer.getStonesWithSymbol(Symbol : TStoneSymbol) : TList;
  var
    Stones : TList;
    Stone : TStone;
    n : Integer;
  begin
    Stones := TList.Create;
    for n := 0 to FStones.Count - 1 do begin
      Stone := TStone((FStones[n]));
      if Stone.Symbol = Symbol then begin
        Stones.Add(Stone);
      end;
    end;
    Result := Stones;
  end;

  (*
   * Returns all stones with the given color owned by this player
   *)
  function TPlayer.getStonesWithColor(Color : TStoneColor) : TList;
  var
    Stones : TList;
    Stone : TStone;
    n : Integer;
  begin
    Stones := TList.Create;
    for n := 0 to FStones.Count - 1 do begin
      Stone := TStone((FStones[n]));
      if Stone.Color = Color then begin
        Stones.Add(Stone);
      end;
    end;
    Result := Stones;
  end;

  (*
   * Returns all stones with the given symbol and color owned by this player
   *)
  function TPlayer.getStonesWith(Symbol : TStoneSymbol; Color : TStoneColor) : TList;
  var
    Stones : TList;
    Stone : TStone;
    n : Integer;
  begin
    Stones := TList.Create;
    for n := 0 to FStones.Count - 1 do begin
      Stone := TStone((FStones[n]));
      if (Stone.Symbol = Symbol) and (Stone.Color = Color) then begin
        Stones.Add(Stone);
      end;
    end;
    Result := Stones;
  end;

  procedure TPlayer.fromXml(xml : TDomNode);
  var
    n, o : Integer;
    XmlSubNode : TDomNode;
    Stone : TStone;
  begin
    if FStones <> nil then FreeAndNil(FStones);
    FStones := TList.Create;
    // Receive player data
    FDisplayName := xml.Attributes.getNamedItem('displayName').NodeValue;
    FPoints := StrToInt(xml.Attributes.getNamedItem('points').NodeValue);
    if xml.NodeName = 'red' then begin
      FPlayerID := PLAYER_RED;
    end
    else begin
      FPlayerID := PLAYER_BLUE;
    end;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if (XmlSubNode.NodeName = 'stone') then begin
        Stone := TStone.create(XmlSubNode);
        FStones.Add(Stone);
      end;
    end;
  end;

  constructor TPlayer.Create(DisplayName : String);
    begin
      inherited Create;
      FDisplayName := DisplayName;
      FStones := TList.Create;
    end;

  destructor TPlayer.destroy;
    begin
      inherited;
    end;
end.

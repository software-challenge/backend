unit UTower;

interface
  uses UDefines, UPlayer;
  type
    TTower = class
      private
        FCity : Integer;
        FSlot : Integer;
        FRedParts : Integer;
        FBlueParts : Integer;
        FOwnerId : Integer;
      public
        property City : Integer read FCity write FCity;
        property Slot : Integer read FSlot write FSlot;
        property RedParts : Integer read FRedParts write FRedParts;
        property BlueParts : Integer read FBlueParts write FBlueParts;
        property OwnerId : Integer read FOwnerId write FOwnerId;

        function canAddPart(playerId : Integer; size : Integer) : Boolean; overload;
        function canAddPart(player : TPlayer; size : Integer) : Boolean; overload;
        function getHeight : Integer;

        constructor Create(city : Integer; slot : Integer; redParts : Integer; blueParts : Integer; owner : Integer);
    end;
implementation

  (*
  Gibt die Höhe des Turms zurück
  *)
  function TTower.getHeight : Integer;
    begin
      Result := FBlueParts + FRedParts;
    end;

  (*
  Gibt an, ob ein Spieler ein Bauteil gegebener Größe
  auf diesen Turms etzen darf.
  *)
  function TTower.canAddPart(player : TPlayer; size : Integer) : Boolean;
    begin
      Result := canAddPart(player.PlayerID, size);
    end;

  function TTower.canAddPart(playerId : Integer; size : Integer) : Boolean;
    begin
      Result := (playerId = FOwnerId) or (size >= Abs(FRedParts - FBlueParts));
    end;

  constructor TTower.Create(city : Integer; slot : Integer; redParts : Integer; blueParts : Integer; owner : Integer);
    begin
      FCity := city;
      FSlot := slot;
      FRedParts := redParts;
      FBlueParts := blueParts;
      FOwnerId := owner;
    end;
end.

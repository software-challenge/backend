unit UPlayer;

interface
  type
    TPlayer = class
      private
        FPlayerID : Integer;
        FMunchedFlowers : Integer;
        FStolenSheeps : Integer;
        FDisplayName : String;
      public
        property PlayerID : Integer read FPlayerID write FPlayerID;
        property DisplayName : String read FDisplayName write FDisplayName;
        property MunchedFlowers : Integer read FMunchedFlowers write FMunchedFlowers;
        property StolenSheeps : Integer read FStolenSheeps write FStolenSheeps;

        constructor Create(DisplayName : String);
    end;

implementation
  constructor TPlayer.Create(DisplayName : String);
    begin
      inherited Create;
      FDisplayName := DisplayName;
      FMunchedFlowers := 0;
      FStolenSheeps := 0;
    end;
end.
 
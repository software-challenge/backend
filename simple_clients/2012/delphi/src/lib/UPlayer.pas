unit UPlayer;

interface
  uses Classes, USegment, UCard;
  type
    TSegmentCounts = array [1..4] of Integer;
    TCardsForSlots = array [0..4] of Integer;
    TPlayer = class
      private
        FPlayerID : Integer;
        FDisplayName : String;
        FPoints : Integer;
        FSegments : TList;
        FCards : TList;
      public
        property PlayerID : Integer read FPlayerID write FPlayerID;
        property DisplayName : String read FDisplayName write FDisplayName;
        property Segments : TList read FSegments write FSegments;
        property Cards : TList read FCards write FCards;
        property Points : Integer read FPoints write FPoints;

        function getUsableSegmentCount : Integer;
        function getRetainedSegmentCount : Integer;
        function getHighestSegment : Integer;
        function getHighestCurrentSegment : Integer;
        function getRetainedSegmentCounts : TSegmentCounts;
        function getUsableSegmentCounts : TSegmentCounts;
        function getCardsForSlots : TCardsForSlots;
        procedure updateCards(cards : TList);
        procedure updateSegments(segments : TList);

        constructor Create(DisplayName : String);
        destructor destroy; override;
    end;

implementation

uses SysUtils;
  constructor TPlayer.Create(DisplayName : String);
    begin
      inherited Create;
      FDisplayName := DisplayName;
      FSegments := TList.Create;
      FCards := TList.Create;
    end;

  function TPlayer.getCardsForSlots : TCardsForSlots;
    var
      cards : TCardsForSlots;
      n : Integer;
    begin
      for n := 0 to FCards.Count - 1 do begin
        cards[TCard(FCards[n]).Slot] := cards[TCard(FCards[n]).Slot] + 1;
      end;
      Result := cards
    end;

  function TPlayer.getRetainedSegmentCounts : TSegmentCounts;
    var
      segments : TSegmentCounts;
      segment : TSegment;
      n : Integer;
    begin
      for n := 1 to 4 do begin
        segments[n] := 0;
      end;
      for n := 1 to FSegments.Count do begin
        segment := TSegment(FSegments[n - 1]);
        segments[segment.Size] := segments[segment.Size] + segment.Retained;
      end;
      Result := segments;
    end;

  function TPlayer.getUsableSegmentCounts : TSegmentCounts;
    var
      segments : TSegmentCounts;
      segment : TSegment;
      n : Integer;
    begin
      for n := 1 to 4 do begin
        segments[n] := 0;
      end;
      for n := 1 to FSegments.Count do begin
        segment := TSegment(FSegments[n - 1]);
        segments[segment.Size] := segments[segment.Size] + segment.Usable;
      end;
      Result := segments;
    end;

  procedure TPlayer.updateCards(cards : TList);
    begin
      FreeAndNil(FCards);
      FCards := cards;
    end;

  procedure TPlayer.updateSegments(segments : TList);
    begin
      FreeAndNil(FSegments);
      FSegments := segments;
    end;

  (*
  Liefert die Anzahl für spätere Abschnitte zurückgelegter Bausteine
  *)
  function TPlayer.getRetainedSegmentCount : Integer;
    var
      segmentCount : Integer;
      n : Integer;
    begin
      segmentCount := 0;
      for n := 0 to FSegments.Count do begin
        segmentCount := segmentCount + TSegment(FSegments[n]).Retained;
      end;
      Result := segmentCount;
    end;

  (*
  Gibt die Größte des größten jetzt oder in späteren Abschnitten
  verfügbaren Bauteils des Spielers
  *)
  function TPlayer.getHighestSegment : Integer;
    var
      highestSegment : Integer;
      curSegment : TSegment;
      n : Integer;
    begin
      highestSegment := 0;
      for n := 0 to FSegments.Count do begin
        curSegment := TSegment(FSegments[n]);
        if((curSegment.Retained + curSegment.Usable > 0) and (curSegment.Size > highestSegment)) then begin
          highestSegment := curSegment.Size;
        end;
      end;
      Result := highestSegment;
    end;

  (*
  Gibt die Größe des größten in diesem Abschnitt
  verfügbaren Bauteils des Spielers
  *)
  function TPlayer.getHighestCurrentSegment : Integer;
    var
      highestSegment : Integer;
      curSegment : TSegment;
      n : Integer;
    begin
      highestSegment := 0;
      for n := 0 to FSegments.Count do begin
        curSegment := TSegment(FSegments[n]);
        if((curSegment.Usable > 0) and (curSegment.Size > highestSegment)) then begin
          highestSegment := curSegment.Size;
        end;
      end;
      Result := highestSegment;
    end;

  (*
  Liefert die Anzahl in diesem Abschnitt benutzbarer Bauteile
  *)
  function TPlayer.getUsableSegmentCount : Integer;
    var
      segmentCount : Integer;
      n : Integer;
    begin
      segmentCount := 0;
      for n := 0 to FSegments.Count do begin
        segmentCount := segmentCount + TSegment(FSegments[n]).Usable;
      end;
      Result := segmentCount;
    end;

  destructor TPlayer.destroy;
    begin
      FreeAndNil(FSegments);
      FreeAndNil(FCards);
      inherited;
    end;
end.

unit UNode;

interface
  uses Classes, UInteger, SysUtils;
  type
    TNode = class
      private
        FIndex : Integer;
        FType : Integer;
        FNeighbours : TList;
        FNeighbourCount : Integer;
      public
        property Index : Integer read FIndex write FIndex;
        property NodeType : Integer read FType write FType;
        property NeighbourCount : Integer read FNeighbourCount write FNeighbourCount;
        function GetDirectNeighbours : TList;
        procedure AddNeighbour(Index : Integer);
        constructor create(NodeType : Integer; Index : Integer);
        destructor destroy; override;
      end;
implementation
  (*
  Returns the fields directly connected to this one
  *)
  function TNode.GetDirectNeighbours : TList;
    begin
      Result := FNeighbours;
    end;

  procedure TNode.AddNeighbour(Index : Integer);
    var
      neighbour : TInteger;
    begin
      neighbour := TInteger.create(Index);
      FNeighbours.Add(neighbour);
      neighbour := nil;
      neighbour := FNeighbours[0];
      Writeln(IntToStr(neighbour.Value));
      FNeighbourCount := FNeighbourCount + 1;
    end;

  constructor TNode.create(NodeType : Integer; Index : Integer);
    begin
      inherited create;
      FIndex := Index;
      FType := NodeType;
      FNeighbours := TList.Create;
      FNeighbourCount := 0;
    end;

  destructor TNode.destroy;
    begin
      FreeAndNil(FNeighbours);
      inherited;
    end;
end.

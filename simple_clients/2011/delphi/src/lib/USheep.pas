unit USheep;

interface
  uses UDefines;
  type
    TSheep = class
      private
        FIndex : Integer;
        FOwner : Integer;
        FDogState : Integer;
        FSheeps1 : Integer;
        FSheeps2 : Integer;
        FFlowers : Integer;
        FNode : Integer;
        FTarget : Integer;
      public
        property SheepIndex : Integer read FIndex write FIndex;
        property PlayerID : Integer read FOwner write FOwner;
        property DogState : Integer read FDogState write FDogState;
        property Sheeps1 : Integer read FSheeps1 write FSheeps1;
        property Sheeps2 : Integer read FSheeps2 write FSheeps2;
        property Flowers : Integer read FFlowers write FFlowers;
        property Node : Integer read FNode write FNode;
        property Target : Integer read FTarget write FTarget;

        constructor create(Node : Integer; Target : Integer; PlayerID : Integer);
    end;
implementation
  constructor TSheep.create(Node : Integer; Target : Integer; PlayerID : Integer);
    begin
      inherited create;
      FNode := Node;
      FTarget := Target;
      FOwner := PlayerID;
      FDogState := DOG_NONE;
    end;
end.

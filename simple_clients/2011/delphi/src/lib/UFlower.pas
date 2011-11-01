unit UFlower;

interface
  type
    TFlower = class
      private
        FNode : Integer;
        FAmount : Integer;
      public
        property Node : Integer read FNode write FNode;
        property Amount : Integer read FAmount write FAmount;

        constructor create(Node : Integer; Amount : Integer);
    end;
implementation
  constructor TFlower.create(Node : Integer; Amount : Integer);
    begin
      FNode := Node;
      FAmount := Amount;
    end;
end.

unit UInteger;

interface
  uses Classes, UUtil;
  type
    TInteger = class
      private
        FValue : Integer;
      public
        property Value : Integer read FValue write FValue;
        constructor create(Value : Integer);
        function equals (Int : TInteger) : Boolean;
        function isInList(List : TList) : Boolean;

    end;
implementation


  function TInteger.isInList(List : TList) : Boolean;
    begin
      Result := isIntInList(FValue, List);
    end;

  function TInteger.equals(Int : TInteger) : Boolean;
    begin
      if(FValue = Int.Value) then begin
        Result := true;
      end
      else begin
        Result := false;
      end;
    end;

  constructor TInteger.create(Value : Integer);
    begin
      inherited create;
      FValue := Value;
    end;
end.
 
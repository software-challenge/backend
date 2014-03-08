unit UStoneToField;

(*
 * Represents the action of placing a stone on a given field
 *)

interface
  uses UStone, UField, AdomCore_4_3, Classes, Math, Contnrs;

  type
    TStoneToField = class
      private
        FStone : TStone;  // Stone to place
        FField : TField;  // Field to place the stone on
      public
        property Stone : TStone read FStone write FStone;
        property Field : TField read FField write FField;

        function isInRowWith(stonesToFields : TObjectList) : Boolean;

        function toString : String;
        function toXml(parent : TDomDocument) : TDomElement;
        constructor create(Stone : TStone; Field : TField);
    end;

implementation

  constructor TStoneToField.create(stone : TStone; field : TField);
  begin
    FStone := stone;
    FField := field;
  end;

  (*
   * Checks if this stone will be placed in the same connected row with the
   * other given stones to be placed.
   *)
  function TStoneToField.isInRowWith(stonesToFields : TObjectList) : Boolean;
  var
    lowestX, lowestY : Integer;
    highestX, highestY : Integer;
    n : Integer;
    stoneToField : TStoneToField;
  begin
    Result := True;
    lowestX := FField.FieldX;
    lowestY := FField.FieldY;
    highestX := FField.FieldX;
    highestY := FField.FieldY;
    // Calculate biggest vertical and biggest horizontal distance between the stones
    for n := 0 to stonesToFields.Count - 1 do begin
      stoneToField := TStoneToField(stonesToFields[n]);
      lowestX := Min(lowestX, stoneToField.FField.FieldX);
      lowestY := Min(lowestY, stoneToField.FField.FieldY);
      highestX := Max(highestX, stoneToField.FField.FieldX);
      highestY := Max(highestY, stoneToField.FField.FieldY);
    end;
    // Stones are in connected row if they have the same Y and the horizontal distance is equal to the number of stones
    // or if they have the same X and the vertical distance is equal to the number of stones
    Result := ((highestX - lowestX = stonesToFields.Count) and (lowestY = highestY)) or ((highestY - lowestY = stonesToFields.Count) and (lowestX = highestX));
  end;

  function TStoneToField.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := TDomElement.create(parent, 'stoneToField');
    xmlElement.AppendChild(FStone.toXml(parent));
    xmlElement.AppendChild(FField.toXml(parent));
    Result := xmlElement;
  end;

  function TStoneToField.toString : String;
  begin
    Result := 'Stone ' + FStone.toString() + sLineBreak + 'to field: ' + FField.toString();
  end;

end.
 
unit UField;

(*
 * Represents a single field of the board
 *)

interface
  uses UDefines, UUtil, UStone, Classes, AdomCore_4_3;

  type
    TField = class
      private
        FStone : TStone;      // The stone placed on this field if any
        FFieldX : Integer;    // The horizontal (X) location of this field
        FFieldY : Integer;    // The vertical (Y) location of this field
      public
        function isOccupied : Boolean;
        function toString : String;
        function toXml(parent : TDomDocument) : TDomElement;

        property Stone : TStone read FStone write FStone;
        property FieldX : Integer read FFieldX write FFieldX;
        property FieldY : Integer read FFieldY write FFieldY;
        constructor create(FieldX : Integer; FieldY : Integer); overload;
        constructor create(FieldX : Integer; FieldY : Integer; Stone : TStone); overload;
        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;

implementation

uses SysUtils, Math;

  function TField.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
  begin
    xmlElement := TDomElement.Create(parent, 'field');
    xmlElement.SetAttribute('posX', IntToStr(FFieldX));
    xmlElement.SetAttribute('posY', IntToStr(FFieldY));
    Result := xmlElement;
  end;

  function TField.toString : String;
  begin
    Result := 'Field (' + IntToStr(FFieldX) + ', ' + IntToStr(FFieldY) + ')';
    if FStone <> nil then begin
      Result := Result + ' with Stone ' + FStone.toString();
    end;
  end;

  (*
   * Returns true if this field is occupied (has a stone placed on it)
   *)
  function TField.isOccupied : Boolean;
  begin
    Result := FStone <> nil;
  end;

  constructor TField.create(xml : TDomNode);
  var
    n, o : Integer;
    XmlSubNode : TDomNode;
  begin
    FFieldX := StrToInt(xml.Attributes.getNamedItem('posX').NodeValue);
    FFieldY := StrToInt(xml.Attributes.getNamedItem('posY').NodeValue);
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlSubNode := xml.ChildNodes.Item(n);
      if XmlSubNode.NodeName = 'stone' then begin
        FStone := TStone.create(XmlSubNode);
      end;
    end;
  end;

  constructor TField.create(FieldX : Integer; FieldY : Integer);
  begin
    FFieldX := FieldX;
    FFieldY := FieldY;
  end;

  constructor TField.create(FieldX : Integer; FieldY : Integer; Stone : TStone);
  begin
    FFieldX := FieldX;
    FFieldY := FieldY;
    FStone := Stone;
  end;

  destructor TField.destroy;
  begin
    inherited;
    FreeAndNil(FStone);
  end;

end.
 
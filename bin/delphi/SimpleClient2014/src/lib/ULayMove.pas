unit ULayMove;

(*
 * Move to place stones on the board
 *)

interface
  uses UMove, UStone, UField, UStoneToField, UUtil, UDefines, AdomCore_4_3, SysUtils, Classes, Contnrs;

  type
    TLayMove = class(TMove)
      private
        FStonesToFields : TObjectList;
      public
        procedure addStoneToField(stone : TStone; field : TField);
        procedure setStonesToAdd(stonesToFields : TObjectList);
        function toXml(parent : TDomDocument) : TDomElement; override;
        function toString : String; override;
        constructor create; overload;
        constructor create(xml : TDomNode); overload;
        destructor destroy; override;
    end;

implementation

  (*
   * Place the given stones on the board
   *)
  procedure TLayMove.setStonesToAdd(stonesToFields : TObjectList);
  begin
    if FStonesToFields <> nil then FStonesToFields.Free;
    FStonesToFields := stonesToFields;
  end;

  function TLayMove.toString : String;
  var
    n : Integer;
    stoneToField : TStoneToField;
  begin
    Result := 'LAY' + sLineBreak;
    for n := 0 to FStonesToFields.Count - 1 do begin
      stoneToField := TStoneToField(FStonesToFields[n]);
      Result := Result + stoneToField.toString + sLineBreak;
    end;
  end;

  function TLayMove.toXml(parent : TDomDocument) : TDomElement;
  var
    xmlElement : TDomElement;
    n : Integer;
  begin
    xmlElement := inherited toXml(parent);
    xmlElement.SetAttribute('class', 'laymove');
    if FStonesToFields <> nil then begin
      for n := 0 to FStonesToFields.Count - 1 do begin
        xmlElement.AppendChild((TStoneToField(FStonesToFields[n])).toXml(parent));
      end;
    end;
    Result := xmlElement;
  end;

  (*
   * Add the given stone to the list of stones to be placed on the board.
   * The given stone will be placed on the given field.
   *)
  procedure TLayMove.addStoneToField(stone : TStone; field : TField);
  var
    stoneToField : TStoneToField;
  begin
    stoneToField := TStoneToField.Create(stone, field);
    FStonesToFields.Add(stoneToField);
  end;

  constructor TLayMove.create;
  begin
    FStonesToFields := TObjectList.Create;
  end;

  constructor TLayMove.create(xml : TDomNode);
  var
    n, o : Integer;
    XmlStFNode : TDomNode;
    XmlSubNode : TDomNode;
    stone : TStone;
    field : TField;
  begin
    FStonesToFields := TObjectList.Create;
    for n := 0 to xml.ChildNodes.Length - 1 do begin
      XmlStFNode := xml.ChildNodes.Item(n);
      if XmlStFNode.NodeName = 'stoneToField' then begin
        for o := 0 to XmlStFNode.ChildNodes.Length - 1 do begin
          XmlSubNode := XmlStFNode.ChildNodes.Item(o);
          if XmlSubNode.NodeName = 'stone' then begin
            stone := TStone.Create(XmlSubNode);
          end;
          if XmlSubNode.NodeName = 'field' then begin
            field := TField.create(XmlSubNode);
          end;
        end;
        addStoneToField(stone, field);
      end;
    end;
  end;

  destructor TLayMove.destroy;
    begin
      FStonesToFields.Free;
      inherited;
    end;

end.

unit UProtocol;

interface

uses
  AdomCore_4_3, SysUtils, UPlayer, UBoard, UClient, UCard, USegment, UTower, UMove, UDefines, Classes;

type
  TProtocol = class
  private
    FXmlDomImpl : TDomImplementation;
    FXmlParser : TXmlToDomParser;
    FBaseNetworkClass : TObject;

    FBoard : TBoard;
    FClient : TClient;

    FRoomID : String;

  public
    constructor Create(network : TObject; board : TBoard; client : TClient);
    destructor Destroy(); override;

    procedure processString(text : String);
end;

implementation
uses UNetwork;

procedure TProtocol.processString(text : String);
var
  XmlInputSource : TXmlInputSource;
  network : TNetwork;
  XmlRequest : TDomDocument;
  i, j, k, l, m, n, o : Integer;
  a, b : Integer;
  XmlBlock : TDomNode;
  XmlSubNodei, XmlSubNodej, XmlSubNodek, XmlSubNodel, XmlSubNodem : TDomNode;
  player : array [0..1] of TPlayer;
  move : TMove;
  NodeType : Integer;
  segments : TList;
  cards : TList;
  towers : TList;
  towerOwner : Integer;
  xmlDoc : TDomDocument;
  xmlElement : TDomElement;
begin
  XmlSubNodei := nil;
  XmlSubNodej := nil;
  XmlSubNodek := nil;
  XmlSubNodel := nil;
  XmlSubNodem := nil;

  network := FBaseNetworkClass as TNetwork;

  // String in XML Document umwandeln
  XmlInputSource := TXmlInputSource.Create('<message>' + text + '</message>', '', '', 1024, False, 0, 0, 0, 0, 1);

  try
    XmlRequest := FXmlParser.Parse(XmlInputSource);
  except
    // No complete valid XML block received
    exit;
  end;

  FreeAndNil(XmlInputSource);

  // XML Document verarbeiten
  if(XmlRequest = nil) then exit;

  // Jeden Childknoten durchgehen
  for i := 0 to XmlRequest.ChildNodes.Item(0).ChildNodes.length - 1 do begin
    XmlBlock := XmlRequest.ChildNodes.Item(0).ChildNodes.Item(i);

    if(XmlBlock.NodeName = 'error') then begin
      // Fehler aufgetreten
      writeln;
      writeln('Error occured:');
      writeln(XmlBlock.attributes.getNamedItem('message').NodeValue);
      network.sendString('</protocol>');
      network.disconnect;
    end;

    if(XmlBlock.NodeName = 'joined') then begin
      FRoomID := XmlBlock.Attributes.getNamedItem('roomId').NodeValue;
      writeln;
      writeln('Joined room: ' + FRoomID);
      network.log('Joined room: ' + FRoomID);
    end;

    if(XmlBlock.NodeName = 'left') then begin
      // Left room
      writeln('Left room: ' + FRoomID);
      network.log('Left room: ' + FRoomID);
      network.sendString('</protocol>');
      network.disconnect;
      break;
    end;

    if(XmlBlock.NodeName = 'room') then begin
      for j := 0 to XmlBlock.ChildNodes.Length - 1 do begin
        XmlSubNodei := xmlBlock.ChildNodes.Item(j);
        if(XmlSubNodei.NodeName = 'data') then begin
          // Die Welcome-Nachricht enthält die Spielerfarbe
          if(XmlSubNodei.Attributes.GetNamedItem('class').NodeValue = 'manhattan:welcome') then begin
            // Die Spieler auslesen
            if(XmlSubNodei.Attributes.GetNamedItem('color').NodeValue = 'red') then begin
              FClient.setId(PLAYER_RED);
            end
            else begin
              FClient.setId(PLAYER_BLUE);
            end;
            writeln;
            writeln('My id is: ' + XmlSubNodei.Attributes.getNamedItem('color').NodeValue);
            network.log('My id is: ' + XmlSubNodei.Attributes.getNamedItem('color').NodeValue);
          end;

          if(XmlSubNodei.Attributes.getNamedItem('class').NodeValue = 'memento') then begin
            for k := 0 to XmlSubNodei.ChildNodes.Length - 1 do begin
              XmlSubNodej := xmlSubNodei.ChildNodes.Item(k);
              if(XmlSubNodej.NodeName = 'state') then begin
                // Game state wurde empfangen
                a := 0;
                towers := TList.Create;
                // Aktuelle Rundennummer und Spieler auslesen
                FClient.CurrentTurn := StrToInt(XmlSubNodej.Attributes.getNamedItem('turn').NodeValue);
                if(XmlSubNodej.Attributes.getNamedItem('current').NodeValue = 'red') then begin
                  FBoard.CurrentPlayer := PLAYER_RED;
                end
                else begin
                  FBoard.CurrentPlayer := PLAYER_BLUE;
                end;
                if(XmlSubNodej.Attributes.getNamedItem('start').NodeValue = 'red') then begin
                  FBoard.FirstPlayer := PLAYER_RED;
                end
                else begin
                  FBoard.FirstPlayer := PLAYER_BLUE;
                end;
                if(XmlSubNodej.Attributes.getNamedItem('type').NodeValue = 'select') then begin
                  FBoard.IsSelection := True;
                end
                else begin
                  FBoard.IsSelection := False;
                end;
                for m := 0 to XmlSubNodej.ChildNodes.Length - 1 do begin
                  XmlSubNodel := XmlSubNodej.ChildNodes.Item(m);
                  // Spielerinfo auslesen
                  if((XmlSubNodel.NodeName = 'red') or (XmlSubNodel.NodeName = 'blue')) then begin
                    // Receive player data
                    player[a] := FClient.getPlayer(a);
                    player[a].DisplayName := XmlSubNodel.Attributes.getNamedItem('displayName').NodeValue;
                    player[a].Points := StrToInt(XmlSubNodel.Attributes.getNamedItem('points').NodeValue);
                    cards := TList.Create;
                    segments := TList.Create;
                    for n := 0 to XmlSubNodel.ChildNodes.Length - 1 do begin
                      XmlSubNodem := XmlSubNodel.ChildNodes.Item(n);
                      if(XmlSubNodem.NodeName = 'card') then begin
                        cards.Add(TCard.Create(StrToInt(XmlSubNodem.Attributes.getNamedItem('slot').NodeValue)));
                      end;
                      if(XmlSubNodem.NodeName = 'segment') then begin
                        segments.Add(TSegment.Create(
                          StrToInt(XmlSubNodem.Attributes.getNamedItem('size').NodeValue),
                          StrToInt(XmlSubNodem.Attributes.getNamedItem('usable').NodeValue),
                          StrToInt(XmlSubNodem.Attributes.getNamedItem('retained').NodeValue)
                        ));
                      end;
                    end;
                    player[a].updateCards(cards);
                    player[a].updateSegments(segments);
                    if(XmlSubNodel.NodeName = 'red') then begin
                      player[a].PlayerID := PLAYER_RED;
                    end
                    else begin
                      player[a].PlayerID := PLAYER_BLUE;
                    end;
                    a := a + 1;
                  end;

                  if(XmlSubNodel.NodeName = 'tower') then begin
                    if(XmlSubNodel.Attributes.getNamedItem('owner').NodeValue = 'red') then begin
                      towerOwner := PLAYER_RED;
                    end
                    else begin
                      towerOwner := PLAYER_BLUE;
                    end;
                    towers.Add(TTower.Create(
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('city').NodeValue),
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('slot').NodeValue),
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('red').NodeValue),
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('blue').NodeValue),
                      towerOwner
                    ));
                  end;
                end;
                // Das Board aktualisieren
                FBoard.updatePlayers(player[0], player[1]);
                FBoard.updateTowers(towers);
              end;
            end;
          end;

          if(XmlSubNodei.Attributes.GetNamedItem('class').NodeValue = 'sc.framework.plugins.protocol.MoveRequest') then begin
            // Move request. Zug anfordern
            move := FClient.zugAngefordert;
            xmlDoc := TDomDocument.Create(FXmlDomImpl);
            xmlElement := TDomElement.Create(xmlDoc, 'room');
            xmlElement.SetAttribute('roomId', FRoomID);
            xmlElement.AppendChild(move.toXml(xmlDoc));
            xmlDoc.AppendChild(xmlElement);
            network.sendXml(xmlDoc);
            FreeAndNil(xmlDoc);
            FreeAndNil(move);
          end;
        end;
      end;
    end;
  end;
  network.emptyReceiveBuffer;
  if(XmlSubNodei <> nil) then FreeAndNil(XmlSubNodei);
  if(XmlSubNodej <> nil) then FreeAndNil(XmlSubNodej);
  if(XmlSubNodek <> nil) then FreeAndNil(XmlSubNodek);
  if(XmlSubNodel <> nil) then FreeAndNil(XmlSubNodel);
  if(XmlSubNodem <> nil) then FreeAndNil(XmlSubNodem);
  FreeAndNil(XmlRequest);
  if(move <> nil) then FreeAndNil(move);
end;

constructor TProtocol.Create(network : TObject; board : TBoard; client : TClient);
begin
  inherited Create();
  FXmlDomImpl := TDomImplementation.Create(nil);
  FXmlParser := TXmlToDomParser.Create(nil);
  FXmlParser.DOMImpl := FXmlDomImpl;
  FBaseNetworkClass := network;
  FBoard := board;
  FClient := client;
end;

destructor TProtocol.Destroy;
begin
  if(FXmlDomImpl <> nil) then FXmlDomImpl.Free;
  if(FXmlParser <> nil) then FXmlParser.Free;

  inherited;
end;

end.

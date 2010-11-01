unit UProtocol;

interface

uses
  AdomCore_4_3, SysUtils, UPlayer, UBoard, UClient, UMove, UNode, UDefines, Classes, USheep, UFlower;

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
  Node : TNode;
  NodeType : Integer;
  Sheep : TSheep;
  Sheeps : TList;
  Flower : TFlower;
  Flowers : TList;
  DiceCount : Integer;
  Dice : TDice;

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
          // Die Welcome-Nachricht enthalt alle Felder und ihre Verbindungen (das Board)
          if(XmlSubNodei.Attributes.GetNamedItem('class').NodeValue = 'sit:welcome') then begin
            // Die Spieler auslesen
            if(XmlSubNodei.Attributes.GetNamedItem('color').NodeValue = 'RED') then begin
              FClient.setId(PLAYER_RED);
            end
            else begin
              FClient.setId(PLAYER_BLUE);
            end;
            writeln;
            writeln('My id is: ' + XmlSubNodei.Attributes.getNamedItem('color').NodeValue);
            network.log('My id is: ' + XmlSubNodei.Attributes.getNamedItem('color').NodeValue);
            for k := 0 to XmlSubNodei.ChildNodes.Length - 1 do begin
              XmlSubNodej := xmlSubNodei.ChildNodes.Item(k);
              // Ein Knoten (Feld) wurde empfangen
              if(XmlSubNodej.NodeName = 'node') then begin
                NodeType := FIELD_UNKNOWN;
                if(XmlSubNodej.Attributes.GetNamedItem('type').NodeValue = 'HOME1') then
                  begin
                    NodeType := FIELD_HOME1;
                  end;
                if(XmlSubNodej.Attributes.GetNamedItem('type').NodeValue = 'HOME2') then
                  begin
                    NodeType := FIELD_HOME2;
                  end;
                if(XmlSubNodej.Attributes.GetNamedItem('type').NodeValue = 'SAVE') then
                  begin
                    NodeType := FIELD_SAVE;
                  end;
                if(XmlSubNodej.Attributes.GetNamedItem('type').NodeValue = 'GRASS') then
                  begin
                    NodeType := FIELD_GRASS;
                  end;

                Node := TNode.create(NodeType, StrToInt(XmlSubNodej.Attributes.GetNamedItem('index').NodeValue));
                for l := 0 to XmlSubNodej.ChildNodes.Length - 1 do begin
                  XmlSubNodek := xmlSubNodej.ChildNodes.Item(l);
                  // Lese die Nachbarn des Knotens aus
                  if(XmlSubNodek.NodeName = 'neighbour') then begin
                    Node.AddNeighbour(StrToInt(XmlSubNodek.ChildNodes.Item(0).NodeValue));
                  end;
                end;
                FBoard.setField(Node.Index, Node);
              end;
            end;
          end;

          if(XmlSubNodei.Attributes.getNamedItem('class').NodeValue = 'memento') then begin
            for k := 0 to XmlSubNodei.ChildNodes.Length - 1 do begin
              XmlSubNodej := xmlSubNodei.ChildNodes.Item(k);
              if(XmlSubNodej.NodeName = 'state') then begin
                // Game state wurde empfangen
                Sheeps := TList.Create;
                Flowers := TList.Create;
                DiceCount := 0;
                a := 0;
                // Aktuelle Rundennummer und Spieler auslesen
                FClient.CurrentTurn := StrToInt(XmlSubNodej.Attributes.getNamedItem('turn').NodeValue);
                if(XmlSubNodej.Attributes.getNamedItem('currentPlayer').NodeValue = 'RED') then begin
                  FBoard.CurrentPlayer := PLAYER_RED;
                end
                else begin
                  FBoard.CurrentPlayer := PLAYER_BLUE;
                end;
                for m := 0 to XmlSubNodej.ChildNodes.Length - 1 do begin
                  XmlSubNodel := XmlSubNodej.ChildNodes.Item(m);
                  // Spielerinfo auslesen
                  if(XmlSubNodel.NodeName = 'player') then begin
                    // Receive player data
                    player[a] := FClient.getPlayer(a);
                    player[a].DisplayName := XmlSubNodel.Attributes.getNamedItem('displayName').NodeValue;
                    if(XmlSubNodel.Attributes.GetNamedItem('color').NodeValue = 'RED') then begin
                      player[a].PlayerID := PLAYER_RED;
                    end;
                    if (XmlSubNodel.Attributes.GetNamedItem('color').NodeValue = 'BLUE') then begin
                      player[a].PlayerID := PLAYER_BLUE;
                    end;
                    player[a].MunchedFlowers := StrToInt(XmlSubNodel.Attributes.getNamedItem('munchedFlowers').NodeValue);
                    player[a].StolenSheeps := StrToInt(XmlSubNodel.Attributes.getNamedItem('stolenSheeps').NodeValue);
                    a := a + 1;
                  end;

                  // Ein Wurfelwert wurde empfangen
                  if(XmlSubNodel.NodeName = 'die') then begin
                    Dice[DiceCount] := StrToInt(XmlSubNodel.Attributes.getNamedItem('value').NodeValue);
                    DiceCount := DiceCount + 1;
                  end;

                  // Eine Blume wurde empfangen
                  if(XmlSubNodel.NodeName = 'flowers') then begin
                    Flower := TFlower.create(
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('node').NodeValue),
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('amount').NodeValue)
                    );
	            Flowers.Add(Flower);
                  end;

                  // Ein Schaf wurde empfangen
                  if(XmlSubNodel.NodeName = 'sheep') then begin
                    // Position und Zielstartfeld auslesen
                    Sheep := TSheep.create(
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('node').NodeValue),
                      StrToInt(XmlSubNodel.Attributes.getNamedItem('target').NodeValue),
                      NO_PLAYER
                    );
                    // Besitzer auslesen, wenn vorhanden
                    if(XmlSubNodel.Attributes.GetNamedItem('owner') <> nil) then begin
                      if(XmlSubNodel.Attributes.GetNamedItem('owner').NodeValue = 'RED') then begin
                        Sheep.PlayerID := PLAYER_RED;
                      end
                      else begin
                        Sheep.PlayerID := PLAYER_BLUE;
                      end;
                    end;
                    // Hundstatus auslesen, wenn vorhanden
                    if(XmlSubNodel.Attributes.GetNamedItem('dog') <> nil) then begin
                      if(XmlSubNodel.Attributes.GetNamedItem('dog').NodeValue = 'PASSIVE') then begin
                        Sheep.DogState := DOG_PASSIVE;
                      end
                      else begin
                        Sheep.DogState := DOG_ACTIVE;
                      end;
                    end;
                    // Anzahl der Schafe und Blumen sowie Index auslesen
                    Sheep.Sheeps1 := StrToInt(XmlSubNodel.Attributes.GetNamedItem('sheeps1').NodeValue);
                    Sheep.Sheeps2 := StrToInt(XmlSubNodel.Attributes.GetNamedItem('sheeps2').NodeValue);
                    Sheep.Flowers := StrToInt(XmlSubNodel.Attributes.GetNamedItem('flowers').NodeValue);
                    Sheep.SheepIndex := StrToInt(XmlSubNodel.Attributes.GetNamedItem('index').NodeValue);
                    Sheeps.Add(Sheep);
                  end;
                end;
                // Das Board aktualisieren
                FBoard.updatePlayers(player[0], player[1]);
                FBoard.updateSheeps(Sheeps);
                FBoard.updateFlowers(Flowers);
                FBoard.updateDice(Dice);
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

unit UNetwork;

interface

uses
  UDefines,
  USocket,
  AdomCore_4_3,
  UBoard,
  UClient,
  SysUtils, Classes, UProtocol;

type
	TNetwork = class
	private
    Fsocket   : TSocket;  // Netzwerk-Kommunikationsschnittstelle mit Windows
		Fhost     : String;   // Host-Adresse
		Fport     : Integer;  // Host-Port
    FLog      : Boolean;  // Do we log?

    Fboard    : TBoard;   // Spielbrett
    FClient   : TClient;  // Simple Client
    FProtocol : TProtocol; // Protocl layer
    FFile     : ^TextFile; // Log File

    FReceiveBuffer : String;
    FReservationID : String;

    FXml : TDomElement;
    FXmlDoc : TDomDocument;
    FXmlDomImpl : TDomImplementation;

    Fconnected: Boolean;
	public
    constructor Create(const host: String; port: Integer; reservationID: String; board : TBoard; client : TClient);
    destructor Destroy(); override;

    function connect(): Boolean;
    procedure disconnect();
    procedure sendString(const s: String);
    procedure sendXml(xml : TDomDocument);
    procedure logFile(var logFile : TextFile);
    procedure log(text : String);

    procedure emptyReceiveBuffer();
    function readString() : Boolean;
    function processMessages() : Boolean;

    property connected: Boolean read Fconnected;
end;

implementation

constructor TNetwork.Create(const host: String; port: Integer; reservationID: String; board : TBoard; client : TClient);
begin
  inherited Create();
  Fhost := host;
  Fport := port;
  Fconnected := False;
  FLog := false;

  FReservationID := reservationID;
  Fboard := board;
  FClient := client;

  Fsocket := TSocket.Create();
  FProtocol := TProtocol.Create(self, FBoard, FClient);
  FXmlDomImpl := TDomImplementation.Create(nil);

  WriteLn('> Network/Socket created.');
end;

destructor TNetwork.Destroy;
begin
  if Fsocket <> nil then
    Fsocket.Free;
  if(FProtocol <> nil) then FreeAndNil(FProtocol);
  if(FXmlDomImpl <> nil) then FreeAndNil(FXmlDomImpl);
  inherited;
end;

//----------------------------------------------------------------------------------------

(*
 * baut eine Verbindung auf.
 *)
function TNetwork.connect(): Boolean;
var
  n: Integer;
begin
  WriteLn('> Connecting...');
  if(FLog) then begin
    Writeln(FFile^, '> Connecting...');
    Flush(FFile^);
  end;

  n := 0;
  while not Fsocket.Connected do
  begin
    try
      Fsocket.Connect(Fhost, Fport);
    except
      WriteLn('Establishing connection (retry: '+IntToStr(n)+') on port '+
                                    IntToStr(Fport)+' failed!');
      inc(n);
    end;
  end;

  WriteLn('> Connected.');
  if(FLog) then Writeln(FFile^, '> Connected.');
  Fconnected := Fsocket.Connected;

  // Connected, start protocol and join the game
  Self.sendString('<protocol>');
  // Do we have a reservation ID?
  if(FReservationID <> '') then begin
    // Got reservation id, use it
    FXmlDoc := TDomDocument.Create(FXmlDomImpl);
    FXml := TDomElement.Create(FXmlDoc, 'joinPrepared');
    FXml.SetAttribute('reservationCode', FReservationID);
    FXmlDoc.AppendChild(FXml);
    Self.sendXml(FXmlDoc);
    FreeAndNil(FXmlDoc);
  end
  else begin
    // No reservation, join based on gametype
    FXmlDoc := TDomDocument.Create(FXmlDomImpl);
    FXml := TDomElement.Create(FXmlDoc, 'join');
    FXml.SetAttribute('gameType', 'SIT');
    FXmlDoc.AppendChild(FXml);
    Self.sendXml(FXmlDoc);
    FreeAndNil(FXmlDoc);
  end;

  result := Fconnected;
end;

(*
 * Bricht die Verbindung ab, sollte eine existieren
 *)
procedure TNetwork.disconnect();
begin      
  if (Fsocket.Connected) then
  begin
    Fsocket.Free();
    Fsocket := TSocket.Create; // workaround: wird nämlich in Network.free freigegeben
    Fconnected := false;
  end;
  WriteLn('> Disconnected.');
end;

(*
 * readMessages liest den Socket-Buffer aus und prüft, ob eine vollständige
 * XML-Nachricht empfangen wurde
 *)
function TNetwork.readString : Boolean;
var
  sockMsg : String;
begin
  sockMsg := '';
  if(not Fsocket.Connected) then exit;
  try
    sockMsg := Fsocket.receive();
  except
    writeln('Netzwerkfehler. Verbindung beendet');
    self.sendString('</protocol>');
    self.disconnect;
    Result := false;
    exit;
  end;
  if(sockMsg <> '') then begin
    // Auf dem Socket wurde etwas empfangen, es wird in den Buffer kopiert
    FReceiveBuffer := FReceiveBuffer + sockMsg;

    // <protocol> - Tag entfernen
    FReceiveBuffer := StringReplace(FReceiveBuffer, '<protocol>', '', [rfReplaceAll, rfIgnoreCase]);

    Writeln('Receive:');
    Writeln;
    Writeln(sockMsg);
    if(FLog) then begin
      Writeln(FFile^, 'Receive:');
      Writeln(FFile^);
      Writeln(FFile^, sockMsg);
    end;

    // Text verarbeiten
    FProtocol.processString(FReceiveBuffer);
  end;
  Result := true;
end;

procedure TNetwork.emptyReceiveBuffer();
begin
  FReceiveBuffer := '';
end;

(*
 * processMessages wird regelmäßig aufgerufen und verarbeitet die empfangenen XML-Nachrichten
 *)
function TNetwork.processMessages : Boolean;
begin
  if(not Fsocket.Connected) then begin
    result := false;
    exit;
  end;

  result := self.readString;
end;

(*
 * sendString schickt eine Nachricht an den Server
 *)
procedure TNetwork.sendString(const s: String);
begin
  if(FSocket.Connected) then begin
    FSocket.sendLn(s);
    writeln('Send:');
    writeln;
    writeln(s);
    if(FLog) then begin
      writeln(FFile^, 'Send:');
      writeln(FFile^);
      writeln(FFile^, s);
    end;
  end;
end;

procedure TNetwork.sendXML(xml : TDomDocument);
var
  text : String;
  FXmlParser : TDomToXmlParser;
begin
  FXmlParser := TDomToXmlParser.Create(nil);
  FXmlParser.DOMImpl := FXmlDomImpl;
  FXmlParser.IncludeXmlDecl := false;
  FXmlParser.WriteToString(xml, '', text);
  self.sendString(text);
  FreeAndNil(FXmlParser);
end;

procedure TNetwork.logFile(var logFile : TextFile);
begin
  FLog := true;
  FFile := @logFile;
end;

procedure TNetwork.log(text : String);
begin
  if(Flog) then begin
    writeln;
    writeln(FFile^, text);
  end;
end;

end.

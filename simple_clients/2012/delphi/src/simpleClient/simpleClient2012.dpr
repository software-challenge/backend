program simpleClient2012;

{$APPTYPE CONSOLE}

uses
  SysUtils,
  UNetwork in '..\lib\UNetwork.pas',
  UBoard in '..\lib\UBoard.pas',
  UClient,
  UMyBoard,
  UProtocol in '..\lib\UProtocol.pas',
  UDefines in '..\lib\UDefines.pas',
  UMove in '..\lib\UMove.pas',
  UPlayer in '..\lib\UPlayer.pas',
  USocket in '..\lib\USocket.pas',
  AbnfUtils in '..\lib\xml\AbnfUtils.pas',
  AdomCore_4_3 in '..\lib\xml\AdomCore_4_3.pas',
  CodecUtilsWin32 in '..\lib\xml\CodecUtilsWin32.pas',
  EncodingUtils in '..\lib\xml\EncodingUtils.pas',
  LangUtils in '..\lib\xml\LangUtils.pas',
  ParserUtilsWin32 in '..\lib\xml\ParserUtilsWin32.pas',
  TreeUtils in '..\lib\xml\TreeUtils.pas',
  UriUtils in '..\lib\xml\UriUtils.pas',
  WideStringUtils in '..\lib\xml\WideStringUtils.pas',
  XmlRulesUtils in '..\lib\xml\XmlRulesUtils.pas',
  UDebugHint in '..\lib\UDebugHint.pas',
  UInteger in '..\lib\UInteger.pas',
  UUtil in '..\lib\UUtil.pas',
  USegment in '..\lib\USegment.pas',
  UCard in '..\lib\UCard.pas',
  UTower in '..\lib\UTower.pas';

type
  Starter = class
    private
      network : TNetwork;
      simpleClient : TClient;
      FFile : TextFile;
      board : TMyBoard;
      client : TClient;

      procedure start;
      function parseOption(key, longKey : String; default : String) : String;
      procedure zeigeHilfe(fehlerNachricht : String);
    public
      constructor create;
      destructor destroy; override;
  end;

constructor Starter.create;
begin
  inherited create;
end;

destructor Starter.destroy;
begin
  if(simpleClient <> nil) then FreeAndNil(simpleClient);
  if(board <> nil) then FreeAndNil(board);
  if(client <> nil) then FreeAndNil(client);
  if(network <> nil) then FreeAndNil(network);
  inherited;
end;

procedure Starter.start;
  var
    host : String;
    port : Integer;
    reservierung : String;
    ConsoleGame : Boolean;
    logNumber : Integer;
    log : Boolean;
  begin
    // Startparameter auslesen
    log := false;
    //if(FindCmdLineSwitch('-l')) or (FindCmdLineSwitch('--log')) then begin
    if(true) then begin   // DEBUG: Always create log file
      log := true;
      logNumber := 0;
      while(true) do begin
        AssignFile(FFile, 'log_' + IntToStr(logNumber) + '.txt');
        try
          ReWrite(FFile);
          Writeln(FFile, 'Startup');
        except
          logNumber := logNumber + 1;
          if(logNumber < 10) then begin
            continue;
          end;
          log := false;
        end;
        break;
      end;
    end;

    if(log) then begin
      Writeln(FFile, CmdLine);
      Writeln(FFile);
    end;

    // Soll die Hilfe angezeigt werden?
    if(FindCmdLineSwitch('-?')) or (FindCmdLineSwitch('/?')) or (FindCmdLineSwitch('?')) then begin
      zeigeHilfe('Hilfe');
      exit;
    end;

    host := parseOption('-h', '--host', '127.0.0.1');
    if(host = 'localhost') then host := '127.0.0.1';
    port := StrToInt(parseOption('-p', '--port', '13050'));
    reservierung := parseOption('-r', '--reservation', '');
    if(FindCmdLineSwitch('-c', true) or FindCmdLineSwitch('--console', true)) then begin
      ConsoleGame := true;
    end
    else begin
      ConsoleGame := false;
    end;

    writeln('Software Challenge 2012');
    writeln('Delphi Client');
    writeln;
    writeln('Host: ' + host);
    writeln('Port: ' + IntToStr(port));
    writeln('Reservierung: ' + reservierung);
    writeln;

    if(log) then begin
      writeln(FFile, 'Host: ' + host);
      writeln(FFile, 'Port: ' + IntToStr(port));
      writeln(FFile, 'Reservierung: ' + reservierung);
      writeln(FFile);
      Flush(FFile);
    end;

    board := TMyBoard.create;
    client := TClient.Create(board);

    network := TNetwork.Create(host, port, reservierung, board, client);
    if(log) then begin
      network.logFile(FFile);
    end;

    network.connect;
    if(network.connected = false) then begin
      Writeln('Not connected');
      if(log) then begin
        Writeln(FFile, 'Not connected');
      end;
      exit;
    end;

    while network.processMessages do begin
      // Spiel-Schleife ... läuft solange weiter bis die Verbindung beendet wurde
      Sleep(10);
      if(log) then flush(FFile);
    end;

    Writeln('Program end...');
    FreeAndNil(network);
    FreeAndNil(client);
    FreeAndNil(board);
    if(log) then begin
      Writeln(FFile, 'Program end...');
      flush(FFile);
    end;
    if(ConsoleGame) then begin
      readln;
    end;
  end;

function Starter.parseOption(key : String; longKey : String; default : String) : String;
  var
    i : Integer;
  begin
    Result := default;
    for i := 0 to ParamCount() do
      begin
        // Jeden Startparameter überprüfen
        if (ParamStr(i) = key) or (ParamStr(i) = longKey) then begin
          Result := ParamStr(i + 1);
        end;
      end;
  end;

procedure Starter.zeigeHilfe(fehlerNachricht : String);
  begin
    Writeln;
    Writeln(fehlerNachricht);
    Writeln;
    Writeln('Bitte das Programm mit folgenden Parametern (optional) aufrufen: \n'
      + 'simpleClient2011.exe [{-h,--host} hostname]\n'
      + '                  [{-p,--port} port]\n'
      + '                  [{-r,--reservation} reservierung]');
    Writeln;
    Writeln('Beispiel: \n'
      + 'simpleClient2011.exe --host 127.0.0.1 --port 10500 --reservation SCHAEFCHEN');
    Writeln;
    Readln;
  end;

var
start : Starter;

begin
  { TODO -oUser -cConsole Main : Insert code here }
  start := Starter.create;
  start.start;
  FreeAndNil(start);
end.


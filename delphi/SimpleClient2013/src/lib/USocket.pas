unit USocket;

interface

uses
  Windows, WinSock,  //connection
  SysUtils, Classes;

const
  MAXBUFFERSIZE = 1536;

type
  TBuffer = array[0..MAXBUFFERSIZE-1] of Char;

  TSocket = class
  private
    FConnected  : Boolean;
    FSocket     : Integer;
    FMessages   : TStringList;
  public
    constructor Create();                                 //throws exception
    destructor Destroy(); override;

    procedure connect(const host: string; port: word);    //throws exception
    function receiveLn(): String;                         //throws exception
    function receiveRest(): String;                       //throws exception
    function receive() : String;
    procedure sendLn(const s: string);                    //throws exception

    property Connected: Boolean read FConnected;
  end;

implementation

constructor TSocket.Create();
var
  wsa: TWSAData;
begin
  inherited;

  FMessages := TStringList.Create;

  if (WSAStartup(MAKEWORD(2,2), wsa) <> 0) then
    Exception.Create('Could not initialize WinSock!');

  FSocket := WinSock.socket(AF_INET, SOCK_STREAM, 0);
  if FSocket = INVALID_SOCKET then
    Exception.Create('Could not create socket!');
end;

destructor TSocket.Destroy();
begin
  CloseSocket(FSocket);
  WSACleanup();

  if FMessages <> nil then
    FMessages.Free;

  inherited;
end;

//-------------------------------------------------------------------------------

procedure TSocket.connect(const host: string; port: Word);
var
  sa  : SOCKADDR_IN;
  ret : Integer;
begin
  ZeroMemory(@sa, sizeof(sa));
  sa.sin_family := AF_INET;
  sa.sin_port := htons(port);
  sa.sin_addr.S_addr := inet_addr(PChar(host));

  ret := WinSock.connect(FSocket, sa, sizeof(sa));
  if ret = SOCKET_ERROR then
    Exception.Create('Could not connect to '+host+' through port '+IntToStr(port)+'!')
  else
    FConnected := true;
end;

function TSocket.receive() : String;
var
  bytesRead : Integer;
  buffer: TBuffer;
begin
  bytesRead := WinSock.recv(FSocket, buffer, sizeOf(buffer), 0); // wartet auf eine neue Nachricht
  // -1 = Server wurde ("hart") geschlossen
  // 0  = Server wurde normal beendet
  if (bytesRead <= 0) then
  begin
    WriteLn('> ERROR @ TSocket.receiveLn()');
    writeln('server has shut down!');
    FConnected := false;
  end;

  result := Copy(buffer, 0, bytesRead);
end;

function TSocket.receiveLn(): String;
var
  bytesRead, len, oldLen, off: Integer;
  buffer: TBuffer;
begin
  bytesRead := WinSock.recv(FSocket, buffer, sizeOf(buffer), 0); // wartet auf eine neue Nachricht
  // -1 = Server wurde ("hart") geschlossen
  // 0  = Server wurde normal beendet
  if (bytesRead <= 0) then
  begin
    WriteLn('> ERROR @ TSocket.receiveLn()');
    Exception.Create('server has shut down!');
  end;

  if (bytesRead = MAXBUFFERSIZE) then
  begin
    WriteLn('ERROR @ TSocket.receiveLn()');
    Exception.Create('MAXBUFFERSIZE is too low. Increase buffer size.');
  end;

  len := 0;
  oldLen := 0;
  off := 0;
  while (len < bytesRead) do
  begin
    if (buffer[len] = #13) then
    begin
      FMessages.Add( Copy(buffer, oldLen, len - oldLen + off) );
      oldLen := len + 3;
      off := 1;
    end;
    inc(len);
  end;

  try
    result := FMessages[0];
    FMessages.Delete(0);
  except
    WriteLn('> ERROR @ TSocket.receiveLn()');
    Exception.Create('message queue is empty!');
  end;
end;

function TSocket.receiveRest(): String;
begin
  if (FMessages.Count > 0) then
  begin
    try
      result := FMessages[0];
      FMessages.Delete(0);
    except
      WriteLn('> ERROR @ TSocket.receiveRest()');
      Exception.Create('message queue is empty!');
    end;
  end else
    result := '';
end;

procedure TSocket.sendLn(const s: string);
var
  buffer: string;
begin
  buffer := s;
  if (WinSock.send(FSocket, buffer[1], length(buffer), 0) = SOCKET_ERROR) then
    Exception.Create('Could not send data!');
end;

end.

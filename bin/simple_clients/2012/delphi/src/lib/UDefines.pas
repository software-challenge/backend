unit UDefines;

interface

(*
 * es folgen einige statische Konstanten, die zur Identifizierung von
 * Nachrichten, die zwischen Client und Server ausgetauscht werden, dienen.
 *)

const
  MESSAGE_SERVER_INITIAL = '0';
  MESSAGE_SERVER_ANSWER_BOARD = '2';
  MESSAGE_SERVER_REQUEST_MOVE = '3';
  MESSAGE_SERVER_ANSWER_SCORE = '4';
  MESSAGE_SERVER_REQUEST_CHANGE = '6';
  MESSAGE_SERVER_RESET = '8';
  MESSAGE_SERVER_TERMINATE = '9';

  MESSAGE_CLIENT_ANSWER_CHANGEREJECT = '0';
  MESSAGE_CLIENT_ANSWER_CHANGEACCEPT = '1';
  MESSAGE_CLIENT_REQUEST_BOARD = '2';
  MESSAGE_CLIENT_ANSWER_MOVE = '3';
  MESSAGE_CLIENT_REQUEST_SCORE = '4';
  MESSAGE_CLIENT_ANSWER_CHANGE = '6';

  NO_PLAYER           = -1;
  PLAYER_RED            = 0;
  PLAYER_BLUE           = 1;

  BOARD_SIZE = 65;

type
  TIntArray = Array of Integer;
  TIntIntArray = Array of Array of Integer;

  TBoardData = Array[0..BOARD_SIZE-1] of Integer;
  TScoreData = Array[0..1] of Integer;

  TStringTokenizer = class
  private
    Fstr: String;
    Fcur: Integer;
    Fend: Integer;
  public
    constructor Create(const s: String);
    function nextToken(): String;
  end;
    
implementation

uses UNetwork;

constructor TStringTokenizer.Create(const s: String);
begin
  inherited Create();
  Fstr := s;
  Fcur := 1;
  Fend := 1;
end;

function TStringTokenizer.nextToken(): String;
var
  i: Integer;
  ret: String;
begin
  ret := '';
  Fcur := Fend;
  for i := Fcur to Length( Fstr ) do
  begin
    if ( Fstr[i] = ' ' ) then
    begin
      ret := Copy( Fstr, Fcur, i - Fcur );
      Fend := i + 1;
      Break;
    end;
  end;

  if ( (i >= (Length( Fstr )-1)) and (ret = '') ) then
  begin
    ret := Copy( Fstr, Fcur, Length( Fstr ) - Fcur + 1 );
  end;
  
  result := ret;
end;

end.

grammar Commands;

options {
  language = Java;
}

@parser::header {
  package com.javadude.speech;
}
@lexer::header {
  package com.javadude.speech;
}

@members {
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
      throw new RuntimeException(e);
    }
}
command[Game game]
  : ( 'get' w1=Word
        ('from' w2=Word {game.getFrom($w1.text, $w2.text);}
        | {game.get($w1.text);}
        )
    | 'examine' w1=Word {game.examine($w1.text);}
    | 'drop' w1=Word {game.drop($w1.text);}
    | 'look' {game.look();}
    | 'inventory' {game.inventory();}
    | 'open' w1=Word {game.open($w1.text);}
    | 'close' w1=Word {game.close($w1.text);}
    | 'unlock' w1=Word {game.unlock($w1.text);}
    | 'lock' w1=Word {game.lock($w1.text);}
    | 'go'
      ( 'west' {game.go(Direction.WEST);}
      | 'east' {game.go(Direction.EAST);}
      | 'north' {game.go(Direction.NORTH);}
      | 'south' {game.go(Direction.SOUTH);}
      )
    ) EOF
  ;


Word :
  Letter Letter*;

fragment
Letter :
  'a'..'z' |
  'A'..'Z';

Whitespace :
  (' ' | '\t' | '\f')+ -> skip;
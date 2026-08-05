package camp.delta.deltatypetheory.plugin.language;

import com.intellij.psi.tree.IElementType;
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryTypes;
import static com.intellij.psi.TokenType.*;

%%

%class DeltaTypeTheoryLexer
%implements com.intellij.lexer.FlexLexer
%unicode
%function advance
%type IElementType
%eof{
  return;
%eof}

WHITE_SPACE   = [\ \n\t\r]+
COMMENT       = "--" [^\r\n]*
IDENTIFIER    = [a-zA-Z_][a-zA-Z0-9_]*

%%

<YYINITIAL> {
      {WHITE_SPACE}   { return WHITE_SPACE; }
      {COMMENT}       { return WHITE_SPACE; }

      "axiom"   { return DeltaTypeTheoryTypes.AXIOM_KEYWORD; }
      "def"     { return DeltaTypeTheoryTypes.DEF_KEYWORD; }
      ":="      { return DeltaTypeTheoryTypes.EQUAL; }
      "=>"      { return DeltaTypeTheoryTypes.FOLLOWS; }
      "→"       { return DeltaTypeTheoryTypes.TO; }
      "->"      { return DeltaTypeTheoryTypes.TO; }
      "λ"       { return DeltaTypeTheoryTypes.LAMBDA; }
      ";"       { return DeltaTypeTheoryTypes.SEMICOLON; }
      ":"       { return DeltaTypeTheoryTypes.COLON; }
      "("       { return DeltaTypeTheoryTypes.LPAREN; }
      ")"       { return DeltaTypeTheoryTypes.RPAREN; }

      {IDENTIFIER}      { return DeltaTypeTheoryTypes.IDENTIFIER; }

}

package camp.delta.deltatypetheory.plugin.language;
import com.intellij.psi.tree.IElementType;
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryType;
import static com.intellij.psi.TokenType.*;
import com.intellij.lexer.FlexLexer;

%%

%public
%class DeltaTypeTheoryLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
  return;
%eof}

WHITE_SPACE   = [\ \n\t\r]+
COMMENT       = "--" [^\r\n]*
IDENTIFIER    = [a-zA-Z_ℕ∃][a-zA-Z0-9_ℕ∃]*

%%

<YYINITIAL> {
      {WHITE_SPACE}   { return WHITE_SPACE; }
      {COMMENT}       { return DeltaTypeTheoryType.COMMENT; }

      "axiom"      { return DeltaTypeTheoryType.AXIOM_KEYWORD; }
      "def"        { return DeltaTypeTheoryType.DEF_KEYWORD; }
      "theorem"    { return DeltaTypeTheoryType.DEF_KEYWORD; }
      "fun"        { return DeltaTypeTheoryType.DEF_KEYWORD; }
      ":="         { return DeltaTypeTheoryType.EQUAL; }
      "=>"         { return DeltaTypeTheoryType.FOLLOWS; }
      "→"          { return DeltaTypeTheoryType.TO; }
      "->"         { return DeltaTypeTheoryType.TO; }
      "λ"          { return DeltaTypeTheoryType.LAMBDA; }
      ";"          { return DeltaTypeTheoryType.SEMICOLON; }
      ":"          { return DeltaTypeTheoryType.COLON; }
      "("          { return DeltaTypeTheoryType.LPAREN; }
      ")"          { return DeltaTypeTheoryType.RPAREN; }
      {IDENTIFIER} { return DeltaTypeTheoryType.IDENTIFIER; }
      [^]          { return BAD_CHARACTER; }

}

package cn.seecoder;

import java.util.ArrayList;

public class Parser {
    private Lexer lexer;

    public Parser(Lexer l) {
        lexer = l;
    }

    private AST parse() {

        return term(new ArrayList<>());
    }

    private AST term(ArrayList<String> ctx) {
        //Term ::= Application| LAMBDA LCID DOT Term
        if (this.lexer.skip(TokenType.LAMBDA)) {
            String id = this.lexer.token(TokenType.LCID);
            this.lexer.match(TokenType.DOT);
            ctx.add(0, id);
            AST term = this.term(ctx);
            ctx.remove(0);
            return new Abstraction(new Identifier(id,"0"), term);
        } else {
            return this.application(ctx);
        }
    }

    private AST application(ArrayList<String> ctx) {
        // Application ::= Atom Application'
        AST left = atom(ctx);

        // Application' ::= Atom Application'
        //                | ε
        while (true) {
            AST right = this.atom(ctx);

            if (right == null) {
                return left;
            } else {
                //Application ::= Application Atom
                //              | Atom
                left = new Application(left, right);
            }

        }
    }

    private AST atom(ArrayList<String> ctx) {
        // Atom ::= LPAREN Term RPAREN| LCID
        if (lexer.skip(TokenType.LPAREN)) {
            AST term = term(ctx);
            lexer.match(TokenType.RPAREN);
            return term;
        } else if (lexer.next(TokenType.LCID)) {
            String id = this.lexer.token(TokenType.LCID);
            return new Identifier(id,Integer.toString(ctx.indexOf(id)));
        }
        return null;

    }
}

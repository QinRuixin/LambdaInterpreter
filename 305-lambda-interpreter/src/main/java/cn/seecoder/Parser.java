package cn.seecoder;

import java.util.ArrayList;

public class Parser {
    Lexer lexer;
    int i = 0;
    int j = 0;

    public Parser(Lexer l) {
        lexer = l;
    }

    public AST parse() {

        AST ast = term(new ArrayList<>());
//        System.out.println(lexer.match(TokenType.EOF));
        return ast;
    }

    private AST term(ArrayList<String> ctx) {
        //Term ::= Application| LAMBDA LCID DOT Term
        if (this.lexer.skip(TokenType.LAMBDA)) {
            String id = this.lexer.token(TokenType.LCID);
            this.lexer.match(TokenType.DOT);
            ctx.add(0, id);
            AST term = this.term(ctx);
            ctx.remove(0);
            //实际上无影响？
            return new Abstraction(new Identifier(id,"0"), term);
//            return new Abstraction(new Identifier(id,Integer.toString(ctx.indexOf(id))), term);
        } else {
            return this.application(ctx);
        }
    }

    private AST application(ArrayList<String> ctx) {
        // write your code here
        // Application ::= Atom Application'
        AST left = atom(ctx);

        // Application' ::= Atom Application'
        //                | ε
        while (true) {
            AST right = this.atom(ctx);

            if (right == null) {
                return left;
            } else {
                left = new Application(left, right);
            }

        }
    }

    private AST atom(ArrayList<String> ctx) {
        // write your code here
        //Atom ::= LPAREN Term RPAREN| LCID
        if (lexer.skip(TokenType.LPAREN)) {
            AST term = term(ctx);
            lexer.match(TokenType.RPAREN);
            return term;
        } else if (lexer.next(TokenType.LCID)) {
            String id = this.lexer.token(TokenType.LCID);
            //
            return new Identifier(id,Integer.toString(ctx.indexOf(id)));
        }
        return null;

    }
}

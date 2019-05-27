package cn.seecoder;

public class Lexer {

    public String source;
    public int index;
    public TokenType token;
    public String tokenvalue;

    public Lexer(String s) {
        index = 0;
        source = s;
        nextToken();
    }

    //get next token
    private TokenType nextToken() {

        char c;
        do{
            c = nextChar();
        }while (c==' ');

        switch (c) {
            case '(':
                token = TokenType.LPAREN;
                System.out.println("LPAREN");

                break;
            case ')':
                token = TokenType.RPAREN;
                System.out.println("RPAREN");

                break;
            case '\\':
                token = TokenType.LAMBDA;
                System.out.println("LAMBDA");
                break;
            case '.':
                token = TokenType.DOT;
                System.out.println("DOT");
                break;
            case '\0':
                token = TokenType.EOF;
                System.out.println("EOF");
                break;
            default:
                if (c >= 'a' && c <= 'z') {
                    StringBuilder sb = new StringBuilder();
                    do {
                        sb.append(c);
                        c = nextChar();
//                    } while (Pattern.matches("[a-z][a-zA-Z]*", sb.toString()));
                    } while (c>='A'&&c<='z');

                    index -= 1;
                    tokenvalue = sb.toString();
                    token = TokenType.LCID;
                    System.out.println("LCID");
                    break;
                } else {
                    throw new IllegalArgumentException("输入错误1");
                }

        }
        return null;
    }

    // get next char
    private char nextChar() {


        if(index >= source.length()){
            return '\0';
        }
        index++;
        return source.charAt(index-1);
    }


    //check token == t
    public boolean next(TokenType t) {
        //write your code here
        return t.equals(token);
    }

    //assert matching the token type, and move next token
    public void match(TokenType t) {
        //write your code here
        if (next(t)) {
            nextToken();
            return;
        } else {
            throw new IllegalArgumentException("输入错误2");
        }
    }

    //skip token  and move next token
    public boolean skip(TokenType t) {
        //write your code here
        if (next(t)) {
            nextToken();
            return true;
        }
        return false;
    }

    public String token(TokenType t) {
        //待完善
        if (t == null) {
            return tokenvalue;
        }
        String temp = tokenvalue;
        match(t);
        return temp;
    }

}

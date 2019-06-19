package cn.seecoder;

public class Lexer {

    public String source;
    public int index;
    public TokenType token;
    public String tokenvalue;

    public Lexer(String s) {
        index = 0;
        if (s.charAt(0) != '(' || s.charAt(s.length() - 1) != ')') {
            source = '(' + s + ')';
        } else {
            source = s;
        }
        nextToken();
    }

    //get next token
    private TokenType nextToken() {

        char c;
        do {
            c = nextChar();
        } while (c == ' ');

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
                    } while (c >= 'A' && c <= 'z');
                    index -= 1;

                    tokenvalue = sb.toString();
                    token = TokenType.LCID;
                    System.out.println("LCID");
                    break;
                } else {
                    throw new IllegalArgumentException("输入错误");
                }

        }
        return null;
    }

    // get next char
    private char nextChar() {
        if (index >= source.length()) {
            return '\0';
        }
        index++;
        return source.charAt(index - 1);
    }


    //断言 next 方法返回 true 并 skip(用于DOT、右括号)
    public void match(TokenType t) {
        if (next(t)) {
            nextToken();
            return;
        } else {
            throw new IllegalArgumentException("输入错误");
        }
    }

    //返回下一个 token 是否匹配 Token,如果匹配的话会跳过(用于LAMBDA、左括号)
    public boolean skip(TokenType t) {
        if (next(t)) {
            nextToken();
            return true;
        }
        return false;
    }

    //断言 next 方法并返回 token(LCID专用)
    public String token(TokenType t) {
        String temp = tokenvalue;
        match(t);
        return temp;
    }

    //返回下一个 token 是否匹配 Token
    public boolean next(TokenType t) {
        return t.equals(token);
    }
}

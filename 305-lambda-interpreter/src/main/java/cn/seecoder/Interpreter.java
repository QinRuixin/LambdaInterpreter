package cn.seecoder;

public class Interpreter {
    Parser parser;
    AST astAfterParser;

    public Interpreter(Parser p){
        parser = p;
        astAfterParser = p.parse();
        System.out.println("After parser:"+astAfterParser.toString());
    }


    private  boolean isAbstraction(AST ast){
        return ast instanceof Abstraction;
    }
    private  boolean isApplication(AST ast){
        return ast instanceof Application;
    }
    private  boolean isIdentifier(AST ast){
        return ast instanceof Identifier;
    }

    public AST eval(){

        return evalAST(astAfterParser);
    }

    private  AST evalAST(AST ast){
        while (true) {
            //如果 t1 是值为 t1' 的项， t1 t2 求值为 t1' t2。即一个 application 的左侧先被求值。
            //如果 t2 是值为 t2' 的项， v1 t2 求值为 v1 t2'。注意这里左侧的是 v1 而非 t1， 这意味着它是 value，不能再一步被求值，也就是说，只有左侧的完成之后，才会对右侧求值。
            //application (\x. t12) v2 的结果，和 t12 中出现的所有 x 被有效替换之后是一样的。注意在对 application 求值之前，两侧必须都是 value。
            /**
             * 分情况讨论 App,Abs,Ide
             *      若App
             *          分左树为 Abs,App,Ide
             *              Abs:替换
             *              App:左树求值后如果右树可求则求，若左树为Abs,递归调用求值后返回
             *              Ide:求右树后返回
             *      若Abs
             *          param固定，对body求值后返回
             *      若Ide
             *          直接返回
             */

            if (isApplication(ast)) {
                if (isAbstraction(((Application) ast).lhs)) {
                    ast = substitute(((Abstraction)((Application) ast).lhs).body, ((Application) ast).rhs);
                }else if(isApplication(((Application) ast).lhs)){
                    ((Application) ast).lhs = evalAST(((Application) ast).lhs);
                    if(!isIdentifier(((Application) ast).rhs)){
                        ((Application) ast).rhs = evalAST(((Application) ast).rhs);
                    }
                    if(isAbstraction(((Application) ast).lhs)){
                        ast = evalAST(ast);
                    }
                    return ast;
                }else if(isIdentifier(((Application) ast).lhs)){
                    ((Application) ast).rhs = evalAST(((Application) ast).rhs);
                    return ast;
                }

            }
            else if (isAbstraction(ast)) {
                ((Abstraction) ast).body = evalAST(((Abstraction) ast).body);
                return ast;
            }
            else{
                return ast;
            }

        }

    }
    private AST substitute(AST node,AST value){

        return shift(-1,subst(node,shift(1,value,0),0),0);

    }

    /**
     *  value替换node节点中的变量：
     *  如果节点是Applation，分别对左右树替换；
     *  如果node节点是abstraction，替入node.body时深度得+1；
     *  如果node是identifier，则替换De Bruijn index值等于depth的identifier（替换之后value的值加深depth）

     *@param value 替换成为的value
     *@param node 被替换的整个节点
     *@param depth 外围的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */
    private AST subst(AST node, AST value, int depth){
        //write your code here
        if(isApplication(node)){
            Application app = (Application) node;
            return new Application(subst(app.lhs,value,depth),subst(app.rhs,value,depth));
        }else if(isAbstraction(node)){
            Abstraction abs = (Abstraction) node;
            return new Abstraction(abs.param,subst(abs.body,value,depth+1));
        }else {
            Identifier id = (Identifier) node;
            int valueInt = Integer.parseInt(id.value);
            if(valueInt==depth){
                return shift(depth,value,0);
            }else {
                return id;
            }
        }

    }

    /**

     *  De Bruijn index值位移
     *  如果节点是Applation，分别对左右树位移；
     *  如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
     *  如果node是identifier，则新的identifier的De Bruijn index值如果大于等于from则加by，否则加0（超出内层的范围的外层变量才要shift by位）.

        *@param by 位移的距离
     *@param node 位移的节点
     *@param from 内层的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */

    private AST shift(int by, AST node,int from){
        //write your code here
        if(isApplication(node)){
            Application app = (Application) node;
            return new Application(shift(by,app.lhs,from),shift(by,app.rhs,from));
        }else if(isAbstraction(node)){
            Abstraction abs = (Abstraction) node;
            return new Abstraction(abs.param,shift(by,abs.body,from+1));
        }else {
            Identifier id = (Identifier) node;
            int newValue = Integer.parseInt(id.value);
            if(newValue>=from){
                newValue = by+Integer.parseInt(id.value);
            }
            return new Identifier(id.name,Integer.toString(newValue));
        }


    }
    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m "+SUCC+") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    private static String app(String func, String x){
        return "(" + func + x + ")";
    }
    private static String app(String func, String x, String y){
        return "(" +  "(" + func + x +")"+ y + ")";
    }
    private static String app(String func, String cond, String x, String y){
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        // write your code here


        String[] sources = {
                ZERO,//0
                ONE,//1
                TWO,//2
                THREE,//3
                app(PLUS, ZERO, ONE),//4
                app(PLUS, TWO, THREE),//5
                app(POW, TWO, TWO),//6
                app(PRED, ONE),//7
                app(PRED, TWO),//8
                app(SUB, FOUR, TWO),//9
                app(AND, TRUE, TRUE),//10
                app(AND, TRUE, FALSE),//11
                app(AND, FALSE, FALSE),//12
                app(OR, TRUE, TRUE),//13
                app(OR, TRUE, FALSE),//14
                app(OR, FALSE, FALSE),//15
                app(NOT, TRUE),//16
                app(NOT, FALSE),//17
                app(IF, TRUE, TRUE, FALSE),//18
                app(IF, FALSE, TRUE, FALSE),//19
                app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
                app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
                app(ISZERO, ZERO),//22
                app(ISZERO, ONE),//23
                app(LEQ, THREE, TWO),//24
                app(LEQ, TWO, THREE),//25
                app(EQ, TWO, FOUR),//26
                app(EQ, FIVE, FIVE),//27
                app(MAX, ONE, TWO),//28
                app(MAX, FOUR, TWO),//29
                app(MIN, ONE, TWO),//30
                app(MIN, FOUR, TWO),//31
        };

        for(int i=0 ; i<sources.length; i++) {
            i=5;


            String source = sources[i];

            System.out.println(i+":"+source);

            Lexer lexer = new Lexer(source);

            Parser parser = new Parser(lexer);

            Interpreter interpreter = new Interpreter(parser);

            AST result = interpreter.eval();

            System.out.println(i+":" + result.toString());

        }

    }
}

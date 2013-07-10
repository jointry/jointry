package jp.ac.aiit.jointry.parser;

import static jp.ac.aiit.jointry.parser.Parser.*;

import java.util.HashSet;

import jp.ac.aiit.jointry.ast.ASTree;
import jp.ac.aiit.jointry.ast.BinaryExpr;
import jp.ac.aiit.jointry.ast.BlockStmnt;
import jp.ac.aiit.jointry.ast.IfStmnt;
import jp.ac.aiit.jointry.ast.MoveStmnt;
import jp.ac.aiit.jointry.ast.Name;
import jp.ac.aiit.jointry.ast.NegativeExpr;
import jp.ac.aiit.jointry.ast.NullStmnt;
import jp.ac.aiit.jointry.ast.NumberLiteral;
import jp.ac.aiit.jointry.ast.PrimaryExpr;
import jp.ac.aiit.jointry.ast.RotateStmnt;
import jp.ac.aiit.jointry.ast.StringLiteral;
import jp.ac.aiit.jointry.ast.WhileStmnt;
import jp.ac.aiit.jointry.parser.Parser.Operators;

public class JoinTryParser {

    HashSet<String> reserved = new HashSet<String>();
    Operators operators = new Operators();

    /*
     * 基本的な文法定義
     *  primary   : ( "[" [ elements ] "]" | "(" expr ")"
     *            | NUMBER | IDENTIFIER | STRING ) { postfix }
     *  factor    : "-" primary | primary
     *  expr      : factor { OP factor }
     *  block     : "{" [ statement ] { ( ";" | EOF ) [ statement ] } "}"
     *  simple    : expr [ args ]
     *  statement : "if" expr block [ "else" block ]
     *            | "while" expr block
     *            | "rotate" expr
     *            | "move"
     *            | simple
     *  program   : [ defclass | def | statemnet ] ( ";" | EOL )
     */
    Parser expr0 = rule();
    Parser primary = rule(PrimaryExpr.class)
            .or(rule().sep("(").ast(expr0).sep(")"),
            rule().number(NumberLiteral.class),
            rule().identifier(Name.class, reserved),
            rule().string(StringLiteral.class));
    Parser factor = rule().or(rule(NegativeExpr.class).sep("-").ast(primary),
            primary);
    Parser expr = expr0.expression(BinaryExpr.class, factor, operators);
    Parser statement0 = rule();
    Parser block = rule(BlockStmnt.class)
            .sep("{").option(statement0)
            .repeat(rule().sep(";", Token.EOL).option(statement0))
            .sep("}");
    Parser simple = rule(PrimaryExpr.class).ast(expr);
    Parser statement =
            statement0.or(rule(IfStmnt.class).sep("if").ast(expr).ast(block)
            .option(rule().sep("else").ast(block)),
            rule(WhileStmnt.class).sep("while").ast(expr).ast(block),
            rule(RotateStmnt.class).sep("rotate").ast(expr),
            rule(MoveStmnt.class).sep("move").ast(expr),
            simple);
    Parser program =
            rule().or(statement, rule(NullStmnt.class)).sep(";", Token.EOL);

    /*
     * 関数に関係する文法規則
     *  param     : IDENTIFIER
     *  params    : param { "," param }
     *  paramlist : "(" [ params ] ")"
     *  def       : "def" IDENTIFIER paramlist block
     *  args      : expr { "," expr }
     *  postfix   : "." IDENTIFIER | "(" [ args ] ")" | "[" expr "]"
     */
    //Parser param = rule().identifier(reserved);
    //Parser params = rule(ParameterList.class)
    //	.ast(param).repeat(rule().sep(",").ast(param));
    //Parser paramList = rule().sep("(").maybe(params).sep(")");
    //Parser def = rule(DefStmnt.class)
    //	.sep("def").identifier(reserved).ast(paramList).ast(block);
    //Parser args = rule(Arguments.class)
    //	.ast(expr).repeat(rule().sep(",").ast(expr));
    //Parser postfix = rule().sep("(").maybe(args).sep(")");

    /*
     * クラスに関係する文法規則
     *  member    : def | simple
     *  classbody : "{" [ member ] { ( "; " | EOL ) [ member ] } "}"
     *  defclass  : "class" IDENTIFIER [ "extends" IDENTIFIER ] classbody
     */
    //Parser member = rule().or(def, simple);
    //Parser classBody = rule(ClassBody.class).sep("{").option(member)
    //						.repeat(rule().sep(";", Token.EOL).option(member))
    //						.sep("}");
    //Parser defclass = rule(ClassStmnt.class).sep("class").identifier(reserved)
    //						.option(rule().sep("extends").identifier(reserved))
    //						.ast(classBody);

    /*
     * 配列に関係する文法規則
     *  elements  : expr { !,! expr }
     */
    //Parser elements =
    //	rule(ArrayLiteral.class).ast(expr).repeat(rule().sep(",").ast(expr));
    /*======================================================================*/
    public JoinTryParser() {
        //primary.repeat(postfix);
        //simple.option(args);
        //program.insertChoice(def);
        reserved.add(";");
        reserved.add("}");
        reserved.add(")");
        reserved.add(Token.EOL);

        operators.add("=", 1, Operators.RIGHT);
        operators.add("==", 2, Operators.LEFT);
        operators.add(">", 2, Operators.LEFT);
        operators.add("<", 2, Operators.LEFT);
        operators.add("+", 3, Operators.LEFT);
        operators.add("-", 3, Operators.LEFT);
        operators.add("*", 4, Operators.LEFT);
        operators.add("/", 4, Operators.LEFT);
        operators.add("%", 4, Operators.LEFT);

        //primary.insertChoice(rule(Fun.class)
        //		.sep("fun").ast(paramList).ast(block));

        //postfix.insertChoice(rule(Dot.class).sep(".").identifier(reserved));
        //program.insertChoice(defclass);

        //reserved.add("]");
        //primary.insertChoice(rule().sep("[").maybe(elements).sep("]"));
        //postfix.insertChoice(rule(ArrayRef.class).sep("[").ast(expr).sep("]"));
    }

    public ASTree parse(Lexer lexer) throws ParseException {
        return program.parse(lexer);
    }
}

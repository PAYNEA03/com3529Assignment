package assignmentFiles.execution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {

//    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/Triangle.java";
    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/BMICalculator.java";
//    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/Calendar.java";
//    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/VendingMachine.java";


    public static void main(String[]args) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

//traverses the file whilst keeping a record of occurrences
        List<HashMap> ifStmt = new ArrayList<>();
        VoidVisitor<List<HashMap>> ifStmtCollector = new IfStmtCollector();

        ifStmtCollector.visit(cu, ifStmt);
        ifStmt.forEach(n ->
                System.out.println(n));
    }


    /**
     * ifStmtCollector
     * passes in a list object and adding criteria to the list and returning the obejct
     * */
    private static class IfStmtCollector extends VoidVisitorAdapter<List<HashMap>> {
        @Override
        public void visit(IfStmt md, List<HashMap> collector) {
            super.visit(md, collector);
            
            collector.add(parseIf(md));

            if (md.hasElseBlock()){
                collector.add(parseElse(md.getElseStmt().get()));
            }
        }

        public static HashMap<String, Object> parseIf(IfStmt n) {
            HashMap<String, Object> ifStmt = new HashMap<>();

/*
            @TODO
              MethodDeclaration m = (MethodDeclaration) n.getParentNode().get().getParentNode().get();
              ifStmt.put("methodName", m.getNameAsString());
              above comments retrieve name, however throw error on calendar class so commented out until resolved
*/

            ifStmt.put("type", "IF");
            ifStmt.put("lineStart", n.getBegin().get().line);
            ifStmt.put("lineEnd", n.getEnd().get().line);
            ifStmt.put("conditions", n.getCondition());

/*
          @TODO
            ifStmt.put("object", n); stores the full object for later access. Not in use whilst debugging
            n.getCondition().asBinaryExpr() - can get variable names and also predicate operator,
            commented out as throws error on Calendar.java
*/

            return ifStmt;
        }

        public static HashMap<String, Object> parseElse(Statement n) {
            HashMap<String, Object> elseStmt = new HashMap<>();

            elseStmt.put("type", "ELSE");
            elseStmt.put("lineStart", n.getBegin().get().line);
            elseStmt.put("lineEnd", n.getEnd().get().line);

            /* @TODO elseStmt.put("object", n); stores the full object for later access. Not in use whilst debugging
            */

            return elseStmt;
        }

    }




}

package assignmentFiles.execution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.metamodel.MethodDeclarationMetaModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {

//    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/Triangle.java";
//    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/BMICalculator.java";
    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/Calendar.java";
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
     * MethodNameCollector
     * passes in a list object and adding criteria to the list and returning the obejct
     * */
    private static class IfStmtCollector extends VoidVisitorAdapter<List<HashMap>> {
        @Override
        public void visit(IfStmt md, List<HashMap> collector) {
            super.visit(md, collector);
            
            collector.add(parseIf(md));
        }

        public static HashMap<String, Object> parseIf(IfStmt n) {
            HashMap<String, Object> ifStmt = new HashMap<>();
//            MethodDeclaration m = (MethodDeclaration) n.getParentNode().get().getParentNode().get();

//            ifStmt.put("methodName", m.getNameAsString());
            ifStmt.put("lineStart", n.getBegin().get().line);
            ifStmt.put("lineEnd", n.getEnd().get().line);
            ifStmt.put("conditions", n.getCondition());
//            n.getCondition().asBinaryExpr() - can get variable names and also predicate operator,
//            commented out as throws error on Calendar.java

            return ifStmt;
        }

    }




}

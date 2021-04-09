package assignmentFiles.execution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static final String FILE_PATH = "src/main/java/assignmentFiles/subjectFiles/Triangle.java";
//    private static final String FILE_PATH = "src/main/java/uk/ac/shef/com3529/lectures/BMICalculator.java";
//    private static final String FILE_PATH = "src/main/java/uk/ac/shef/com3529/lectures/Calendar.java";


    public static void main(String[]args) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

//        HashMap<String, Object> ifStmt = new HashMap<>();

        VoidVisitor<Void> ifStmtParser = new IfStmtParser();
        ifStmtParser.visit(cu,null);

//traverses the file whilst keeping a record of occurrences
        List<String> methodNames = new ArrayList<>();
        VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();

        methodNameCollector.visit(cu, methodNames);
        methodNames.forEach(n ->
                System.out.println("MethodNameCollected:" + n));
    }

    private static class IfStmtParser extends VoidVisitorAdapter<Void> {

//        public static Object newMethod(IfStmt n, HashMap<String, Object> ifStmt) {
//
//            ifStmt.put("lineStart", n.getBegin().get().line);
//            ifStmt.put("lineEnd", n.getEnd().get().line);
//
//            System.out.println("Found an if statement @ " + n.getBegin().get().line);
//            System.out.println("End at line:  " + n.getEnd().get().line);
//            System.out.println("Conditions: " + n.getCondition().asBinaryExpr());
//            System.out.println("");
//
//            return ifStmt;
//        }

        @Override
        public void visit(IfStmt n, Void arg) {
            parseIf(n);
        }

        public void parseIf(IfStmt n) {
            System.out.println("Found an if statement @ " + n.getBegin().get().line);
            System.out.println("End at line:  " + n.getEnd().get().line);
            System.out.println("Conditions: " + n.getCondition().asBinaryExpr());
            System.out.println("");

            if (n.getThenStmt().isBlockStmt()) {
                visit(n.getThenStmt().asBlockStmt(), null);
            }
            if (n.hasCascadingIfStmt()) {
                IfStmt el = n.getElseStmt().get().asIfStmt();
                visit(el,null);
            } else if (n.hasElseBlock()) {
                visit(n.getElseStmt().get().asBlockStmt(), null);
            }

            System.out.println("");

        }

    }
    /**
     * MethodNameCollector, works same as the below method name printer, but with the added benefit of
     * passing in a list object and adding criteria to the list and returning the obejct
     * */
    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);


            collector.add(md.getNameAsString());
        }

    }


    /**
     * MethodNamePrinter, stateless traversal of a file, scans each line and returns method names to console
     * */
    private static class MethodParamPrinter extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);

            List<Parameter> list = md.getParameters();
            // returns all parameter name and types
            for(Parameter n:list){
                System.out.println("Param Name: " + n.getName());
                System.out.println("Param Type: " + n.getType());
                System.out.println("Param line: " + n.getBegin().get().line);

                System.out.println("");
            }

            System.out.println("");
        }
    }


}

package assignmentFiles.execution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {

    private static final String[] FILE_PATH = {
            "src/main/java/assignmentFiles/subjectFiles/Triangle.java",
            "src/main/java/assignmentFiles/subjectFiles/BMICalculator.java",
            "src/main/java/assignmentFiles/subjectFiles/Calendar.java",
            "src/main/java/assignmentFiles/subjectFiles/VendingMachine.java",
            "src/main/java/assignmentFiles/subjectFiles/SignUtils.java"
    };


    public static void main(String[]args) throws Exception {


        for (String file: FILE_PATH) {
            System.out.println("");
            System.out.println("File to be parsed: " + file);

            // Parse File using Java Parser
            CompilationUnit cu = StaticJavaParser.parse(new File(file));

            /**
             * Javaparser visitor for if and else statements, returns a hasmap with details of the conditions
            * */
            List<HashMap> ifStmt = new ArrayList<>();
            VoidVisitor<List<HashMap>> ifStmtCollector = new IfStmtCollector();

            ifStmtCollector.visit(cu, ifStmt);
            ifStmt.forEach(n ->
                    System.out.println(n));


            /**
             * Javaparser visitor for method declarations, returns a hashmap with method names and their params
             * */

            List<HashMap> methodNames = new ArrayList<>();
            VoidVisitor<List<HashMap>> methodCollector = new MethodCollector();

            methodCollector.visit(cu, methodNames);
            methodNames.forEach(n ->
                    System.out.println(n));

            System.out.println("");
        }


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

    private static class MethodCollector extends VoidVisitorAdapter<List<HashMap>> {
        @Override
        public void visit(MethodDeclaration md, List<HashMap> collector) {
            super.visit(md, collector);

            collector.add(parseMethod(md));
        }

        /**
         * Creates a hashmap of methods by name, each method name contains a list of hash maps
         * with the parameter details. main HashMap returned
         * */
        public static HashMap<String, List> parseMethod(MethodDeclaration md) {
            HashMap<String, List> methodList = new HashMap<>();

            List<HashMap> parameterList = new ArrayList<>();

            List<Parameter> list = md.getParameters();
            for(Parameter p:list){
                HashMap<String, Object> methodDetails = new HashMap<>();
//                methodDetails.put("methodObject", md);
                methodDetails.put("paramName", p.getName());
                methodDetails.put("paramType", p.getType());
                methodDetails.put("paramLineStart", p.getBegin().get().line);
                parameterList.add(methodDetails);
            }

            methodList.put(md.getNameAsString(), parameterList);

            return methodList;
        }

    }







}

package assignmentFiles.execution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.sun.source.doctree.SeeTree;
import javassist.compiler.ast.Stmnt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Parser {


    /**
     * ifStmtCollector
     * passes in a list object and adding criteria to the list and returning the obejct
     * */


    public static class IfStmtCollector extends VoidVisitorAdapter<List<HashMap>> {
        @Override
        public void visit(IfStmt md, List<HashMap> collector) {
            super.visit(md, collector);

            ifConvertor(md);

            collector.add(parseIf(md));

            if (md.hasElseBlock()){
                collector.add(parseElse(md.getElseStmt().get()));
            }
        }

        public static HashMap<String, Object> parseIf(IfStmt n) {
            HashMap<String, Object> ifStmt = new HashMap<>();

            n.getThenStmt().asBlockStmt().addStatement(0, new NameExpr("System.out.println(\"Hello World\")"));

    /*
            @TODO
              MethodDeclaration m = (MethodDeclaration) n.getParentNode().get().getParentNode().get();
              ifStmt.put("methodName", m.getNameAsString());
              above comments retrieve name
              , however throw error on calendar class so commented out until resolved
*/
            ifStmt.put("type", "IF");
            ifStmt.put("ifLineLocation", n.getBegin());
//            ifStmt.put("ifEnd", n.getEnd().get().line);
            ifStmt.put("conditions", n.getCondition());
//           @todo then block line start - insert instrumenter here?
//            ifStmt.put("insertInstrumentAtLine", n.getThenStmt().getBegin().get().line);
//            ifStmt.put("obj", n);


/*
          @TODO
            ifStmt.put("object", n); stores the full object for later access. Not in use whilst debugging
            n.getCondition().asBinaryExpr() - can get variable names and also predicate operator,
            commented out as throws error on Calendar.java
*/

            return ifStmt;
        }

        private static void ifConvertor(IfStmt n) {
            if (n.getThenStmt().isExpressionStmt()) {
                String t = n.getThenStmt().toString().replaceFirst(".$","");
                BlockStmt k = new BlockStmt();
                k.addStatement(new NameExpr(t));
                n.replace(n.getThenStmt().asExpressionStmt(),k);
            }
        }

        public static HashMap<String, Object> parseElse(Statement n) {
            HashMap<String, Object> elseStmt = new HashMap<>();
            n.asBlockStmt().addStatement(0, new NameExpr("System.out.println(\"Hello World\")"));


            elseStmt.put("type", "ELSE");
            elseStmt.put("lineStart", n.getBegin().get().line);
            elseStmt.put("lineEnd", n.getEnd().get().line);
//            elseStmt.put("obj", n);



            /* @TODO elseStmt.put("object", n); stores the full object for later access. Not in use whilst debugging
            */

            return elseStmt;
        }

    }

//    public static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
//        @Override
//        public void visit(MethodCallExpr n, Void arg) {
//            // Found a method call
//            System.out.println(n.getBegin() + " - " + n.getName() + " - " + n.getArguments());
//            n.addArgument("testarg");
//            System.out.println("Parserd: " + n.getBegin() + " - " + n.getName() + " - " + n.getArguments());
//
//            // Don't forget to call super, it may find more method calls inside the arguments of this method call, for example.
//            super.visit(n, arg);
//        }
//    }

    public static class MethodCollector extends VoidVisitorAdapter<List<HashMap>> {
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
//            md.addParameter("Set<Integer>", "coveredBranches");
//
//            VoidVisitor methodCall = new Parser.MethodCallVisitor();
//            md.accept(methodCall,null);

//          add expression to method body
//          System.out.println(md.getBody().get().addStatement("System.out.println(\"Hello World\");"));
//          System.out.println(md.getBody().get().addStatement(0, new NameExpr("System.out.println(\"Hello World\")")));


            List<HashMap> ifStmt = new ArrayList<>();
            VoidVisitor<List<HashMap>> ifStmtCollector = new Parser.IfStmtCollector();

            ifStmtCollector.visit(md,ifStmt);
//            IfStmtCollector.parseIf(md.getBody().get().asBlockStmt());

            for(Parameter p:list){
                HashMap<String, Object> methodDetails = new HashMap<>();
//                methodDetails.put("methodObject", md);
                methodDetails.put("paramName", p.getName());
                methodDetails.put("paramType", p.getType());
//                methodDetails.put("paramLineStart", p.getBegin().get().line);
                parameterList.add(methodDetails);
            }

            methodList.put(md.getNameAsString(), parameterList);


            return methodList;
        }

    }







}

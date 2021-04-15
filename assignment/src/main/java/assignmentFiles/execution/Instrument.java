package assignmentFiles.execution;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;

import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



public class Instrument {

    public static int branchCount = 0;
    public static int conditionCount = 0;

    public static BinaryExpr.Operator[] operators = {
            BinaryExpr.Operator.PLUS,
            BinaryExpr.Operator.MINUS,
            BinaryExpr.Operator.MULTIPLY,
            BinaryExpr.Operator.DIVIDE,
            BinaryExpr.Operator.REMAINDER
    };


    public static void parseClass(CompilationUnit cu) {

        // get class name
        List<String> className = new ArrayList<>();
        VoidVisitor<List<String>> classNameVisitor = new ClassNameCollector();
        classNameVisitor.visit(cu,className);

        // set param to instrument into class
        String[] param = {"Set<Integer>", "coveredBranches"};

        //instrument methods in class
        HashMap<String, List> methodDetail = new HashMap<>();
        VoidVisitor methodParser = new Instrument.MethodParser();
        methodParser.visit(cu, methodDetail);

        //instrument if statements
        VoidVisitor ifStmtParser = new Instrument.IfStmtParser();
        ifStmtParser.visit(cu,param);

        // instrument while statements
        VoidVisitor whileStmtParser = new Instrument.WhileStmtParser();
        whileStmtParser.visit(cu,null);

        // write instrumented file
        writeInstrumentedFile(cu, className);

        System.out.println(methodDetail);


    }

    private static void createMethod(ClassOrInterfaceDeclaration type) {
        MethodDeclaration method = type.addMethod("testingGetterAndSetter");
        method.setModifiers(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC)
                .setType("void")
                .addParameter("String[]","paramList")
                .addParameter("Set<Integer>","coveredBranches")
                .setBody(new BlockStmt()
                        .addStatement(new NameExpr("String out"))
                        .addStatement(new NameExpr("String out2")));

    }

    private static void writeInstrumentedFile(CompilationUnit cu, List<String> className) {
        String newName = "Instrumented"+className.get(0);
        String filePath = "src/main/java/assignmentFiles/instrumentedFiles/";
        ClassOrInterfaceDeclaration myClass = cu.getClassByName(className.get(0)).get();
        createMethod(myClass);

        myClass.setName(newName);
        cu.setPackageDeclaration("assignmentFiles.instrumentedFiles");

        cu.addImport(new ImportDeclaration("java.util.TreeSet", false, false));
        cu.addImport(new ImportDeclaration("java.util.Set", false, false));
        cu.addImport(new ImportDeclaration("assignmentFiles.execution", false, true));


        WriteToFile.writeClass(cu.toString(),newName,filePath);

    }

//    https://stackoverflow.com/questions/65377062/javaparser-how-to-get-classname-in-compilationunit
    private static class ClassNameCollector extends VoidVisitorAdapter<List<String>>{
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {
            super.visit(n, collector);
            collector.add(n.getNameAsString());
        }
    }

    public static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }


    private static class MethodParser extends VoidVisitorAdapter<HashMap<String, List>> {
        @Override
        public void visit(MethodDeclaration md, HashMap<String, List> methodList) {
            super.visit(md, methodList);
//            gets list of methodNames in class
            List<String> methodNames = new ArrayList<>();
            VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
            methodNameCollector.visit(md.findCompilationUnit().get(),methodNames);

//            all parameters found in method
            List<Parameter> methodParameters = md.getParameters();

//           parameter list builder
            List<HashMap> parameterList = new ArrayList<>();

//            populate parameter list details
            for(Parameter p:methodParameters){
                HashMap<String, Object> methodDetails = new HashMap<>();
                methodDetails.put("paramName", p.getName());
                methodDetails.put("paramType", p.getType());
                parameterList.add(methodDetails);
            }
//            add parameter details and method name to main method list
            methodList.put(md.getNameAsString(), parameterList);

//            parse method instrumentations
            // add arg to list of parameters in all methods
            md.addParameter("Set<Integer>", "coveredBranches");

            //add parameter arg to all method calls in class
            VoidVisitor methodCall = new Instrument.MethodCallVisitor();
            md.accept(methodCall,methodNames);
        }
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodCallExpr n, List<String> methodNames) {
            if (methodNames.contains(n.getNameAsString())) {
                n.addArgument("coveredBranches");
            }
            super.visit(n, methodNames);
        }
    }

    private static String addBranchLogger() {
        ++branchCount;
        String branch = "TestDataGenerator.coveredBranch(" + branchCount + ", coveredBranches)";

        return branch;
    }

    private static NameExpr addConditionLogger(String stmt) {
        conditionCount++;
        String condition = "TestDataGenerator.logCondition(" + conditionCount + ", " + stmt + ")";
        NameExpr newCondition = new NameExpr(condition);

        return newCondition;
    }

    private static void recursiveConditionParser(Expression expr) {

        List<BinaryExpr.Operator> operatorList = new ArrayList<>(Arrays.asList(operators));

//        check if expression is binary ( (x>y), etc)
        if (expr instanceof BinaryExpr) {
            BinaryExpr child = (BinaryExpr) expr;
            NameExpr leftInstrument;
            NameExpr rightInstrument;

//            if left condition is binary ( (x>y), etc) explore further, else add expression. dont want to explore method
//            calls further as know they have not operators so bypassed if isMethodCallExpr is true
            if (!child.getLeft().isMethodCallExpr()) {
                if (child.getLeft().isNameExpr()) {
//                    checks if operator is boolean return
                    if (!operatorList.contains(child.getOperator())) {
                        leftInstrument = addConditionLogger(child.toString());
                        child.replace(leftInstrument);
                    }

                } else {
                    recursiveConditionParser(child.getLeft());
                }
            }

            if (!child.getRight().isMethodCallExpr()) {

//            if RIGHT condition is binary ( (x>y), etc) explore further, else print expression
                if (child.getRight().isNameExpr()) {
                    if (!operatorList.contains(child.getOperator())) {
                        rightInstrument = addConditionLogger(child.toString());
                        child.replace(rightInstrument);
                    }

                } else {
                    recursiveConditionParser(child.getRight());
                }

            }
        //enclosed expression is in brackets, so (x > r) instead of x > r, converts then re-calls method
        } else if (expr instanceof EnclosedExpr) {
            EnclosedExpr enclosedChild = (EnclosedExpr) expr;
            BinaryExpr parsed = enclosedChild.getInner().asBinaryExpr();
            recursiveConditionParser(parsed);
        }

    }

    private static class WhileStmtParser extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(WhileStmt md, Void args) {
            super.visit(md, args);

            md.getBody().asBlockStmt().addStatement(0, new NameExpr(addBranchLogger()));

            if( md.getCondition().isBinaryExpr()) {
                recursiveConditionParser(md.getCondition().asBinaryExpr());
            } else {
                md.setCondition(addConditionLogger(md.getCondition().toString()));
            }
        }

    }

    private static class IfStmtParser extends VoidVisitorAdapter<String[]> {
        @Override
        public void visit(IfStmt md, String[] param) {
            super.visit(md, param);

            convertIfStmt(md);

            parseIfStmt(md);

            if (md.hasElseBlock()){
                parseElse(md.getElseStmt().get());
            }
        }

        private static void parseIfStmt(IfStmt n) {
            n.getThenStmt().asBlockStmt().addStatement(0, new NameExpr(addBranchLogger()));
            if( n.getCondition().isBinaryExpr()) {
                recursiveConditionParser(n.getCondition().asBinaryExpr());
            } else {
                n.setCondition(addConditionLogger(n.getCondition().toString()));
            }
        }

        private static void parseElse(Statement n) {
            n.asBlockStmt().addStatement(0, new NameExpr(addBranchLogger()));
        }


        private static void convertIfStmt(IfStmt n) {
            if (n.getThenStmt().isExpressionStmt()) {
                String t = n.getThenStmt().toString().replaceFirst(".$","");
                BlockStmt k = new BlockStmt();
                k.addStatement(new NameExpr(t));
                n.replace(n.getThenStmt().asExpressionStmt(),k);
            }
        }
    }


}

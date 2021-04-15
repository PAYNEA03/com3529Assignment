package assignmentFiles.execution;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;

import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;


public class Instrument {

    private static int branchCount = 0;
    private static int conditionCount = 0;
    private static HashMap<Integer, Expression> ifStmtLogs = new HashMap<>();
    private static List<String> classNames = new ArrayList<>();

    public int branchTotal;
    public int conditionTotal;
    public String path;
    public String className;
    public HashMap<String, List> methodDetails;
    public HashMap<Integer, Expression> ifStmts;

    public static BinaryExpr.Operator[] operators = {
            BinaryExpr.Operator.PLUS,
            BinaryExpr.Operator.MINUS,
            BinaryExpr.Operator.MULTIPLY,
            BinaryExpr.Operator.DIVIDE,
            BinaryExpr.Operator.REMAINDER
    };

    public Instrument(String path, HashMap<String, List>methodDetail) {
        this.path = path;
        this.methodDetails = methodDetail;
        this.ifStmts = ifStmtLogs;
        this.branchTotal = branchCount;
        this.conditionTotal = conditionCount;
    }


    public static Instrument parseClass(CompilationUnit cu) {

        // get class name
        VoidVisitor<List<String>> classNameVisitor = new ClassNameCollector();
        classNameVisitor.visit(cu,classNames);

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
        String writtenFilePath = WriteToFile.writeInstrumentedFile(cu, classNames, methodDetail);

        Instrument file = new Instrument(writtenFilePath, methodDetail);

        return file;

    }

    public static void createVariableAssignMethod(ClassOrInterfaceDeclaration type, HashMap<String, List> methodDetail) {
        MethodDeclaration method = type.addMethod("assignVariables");
        method.setModifiers(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC)
                .setType("Object")
                .addParameter("HashMap<String, List>", "paramList")
                .addParameter("Set<Integer>","coveredBranches")
                .setBody(new BlockStmt()
                        .addStatement(new NameExpr("Object result = \"empty\""))
                        .addStatement(new NameExpr("for (Map.Entry<String, List> methodEntry : paramList.entrySet()) {"))
                        .addStatement(new NameExpr("String methodName = methodEntry.getKey()"))
                        .addStatement(new NameExpr("List methodParams = methodEntry.getValue()")));

        for (Map.Entry<String, List> entry : methodDetail.entrySet()) {
            String key = entry.getKey();
            List value = entry.getValue();
            String paramCall = "";

            method.getBody().get()
                    .addStatement(new NameExpr("if (methodName.equals(\"" + key + "\")) {"))
                    .addStatement(new NameExpr("System.out.println(\"********Parsing Method: " + key +" ****\");"));

            for (Object params : value) {
                HashMap detail = (HashMap) params;
                String varName = detail.get("paramName").toString();
                Object varType = detail.get("paramType").toString();
                if (paramCall.isEmpty()) {
                    paramCall = varName;
                } else {
                    paramCall = paramCall + ", " + varName;
                }
                method.getBody().get().addStatement(new NameExpr(varType + " "+ varName+" = TestDataGenerator.assignValues(\"" + varName + "\", methodParams)"));
            }

            method.getBody().get()
                    .addStatement(new NameExpr("try {"))
                    .addStatement(new NameExpr(key + "(" + paramCall +", coveredBranches)"))
                    .addStatement(new NameExpr("} catch (Exception e) {"))
                    .addStatement(new NameExpr("System.out.println(e)"))
                    .addStatement(new NameExpr("System.out.println(\"Something went wrong passing values to function\")"))
                    .addStatement(new NameExpr("}"))
                    .addStatement(new NameExpr("}"));
        }

        method.getBody().get()
                .addStatement(new NameExpr("}"))
                .addStatement(new NameExpr("return result"));


    }

    private static String addBranchLogger() {
        ++branchCount;
        String branch = "TestDataGenerator.coveredBranch(" + branchCount + ", coveredBranches)";

        return branch;
    }

    private static NameExpr addConditionLogger(Expression stmt) {
        conditionCount++;
        ifStmtLogs.put(conditionCount,stmt);
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
                        leftInstrument = addConditionLogger(child);
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
                        rightInstrument = addConditionLogger(child);
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
//                methodDetails.put("value", 0);
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

    private static class WhileStmtParser extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(WhileStmt md, Void args) {
            super.visit(md, args);

            md.getBody().asBlockStmt().addStatement(0, new NameExpr(addBranchLogger()));

            if( md.getCondition().isBinaryExpr()) {
                recursiveConditionParser(md.getCondition().asBinaryExpr());
            } else {
                md.setCondition(addConditionLogger(md.getCondition()));
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
                n.setCondition(addConditionLogger(n.getCondition()));
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

            if (!n.hasElseBranch()) {
                n.setElseStmt(new BlockStmt());
            }
        }
    }


}

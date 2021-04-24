package assignmentFiles.execution;

import assignmentFiles.utils.Pair;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;

import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import javax.swing.plaf.nimbus.State;
import java.util.*;


public class Instrument {

    private static int branchCount = 0;
    private static int conditionCount = 0;
    private static HashMap<Integer, Expression> ifStmtLogs = new HashMap<>();
    private static List<String> classNames = new ArrayList<>();
    public static HashMap<String, HashMap<Integer,Pair<Boolean, Boolean>>> methodConditions = new HashMap<>();
    public static HashMap<String, HashMap<Integer,Pair<Boolean, Boolean>>> methodBranches = new HashMap<>();
    public static EnumDeclaration parsedEnum;
    private final EnumDeclaration enumDec;

    private HashMap<String, HashMap<Integer, Pair<Boolean, Boolean>>> methodConditionsWithPairs;
    public HashMap<String, HashMap<Integer,Pair<Boolean, Boolean>>> methodBranchBooleans;
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
        this.methodConditionsWithPairs = methodConditions;
        this.methodBranchBooleans = methodBranches;
        this.className = classNames.get(0);
        this.enumDec = parsedEnum;
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

        VoidVisitor enumParser = new Instrument.EnumParser();
        enumParser.visit(cu,null);

        // instrument while statements
        VoidVisitor whileStmtParser = new Instrument.WhileStmtParser();
        whileStmtParser.visit(cu,null);

        // write instrumented file
        String writtenFilePath = WriteToFile.writeInstrumentedFile(cu, classNames, methodDetail);

        Instrument file = new Instrument(writtenFilePath, methodDetail);

        return file;

    }

    public static void createVariableAssignMethod2(ClassOrInterfaceDeclaration type, HashMap<String, List> methodDetail) {
//        set method name
        MethodDeclaration method = type.addMethod("assignVariables");
//        set method dynamics
        method.setModifiers(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC)
                .setType("Object")
                .addParameter("Map.Entry<String,List>", "paramList")
                .addParameter("Set<Integer>","coveredBranches")
                .addParameter("HashMap<Integer,Boolean>","coveredConditions")
//       initialize body and set up for loop to iterate through the hashmap method names
                .setBody(new BlockStmt()
                        .addStatement(new NameExpr("Object result = \"empty\""))
                      //1  .addStatement(new NameExpr("for (Map.Entry<String, List> methodEntry : paramList.entrySet()) {"))
                        .addStatement(new NameExpr("String methodName = paramList.getKey()"))
                        .addStatement(new NameExpr("List methodParams = paramList.getValue()")));

//        gets the method name from the hashmap, and writes an if statement that has its name to ensure its called.
        for (Map.Entry<String, List> entry : methodDetail.entrySet()) {
            String key = entry.getKey();
            List value = entry.getValue();
            String paramCall = "";

//            method.getBody gets info between curly braces. addStatement allows adding new line, new NameExpr allows
//            entering statement details as a string
            method.getBody().get()
                    .addStatement(new NameExpr("if (methodName.equals(\"" + key + "\")) {"))
                    .addStatement(new NameExpr("System.out.println(\"********Parsing Method: " + key +" ****\");"));

//            for each of the methods parameters, initialise the variable and set up a call to assign values, passing in the list of parameters
//            and the variable name to initialise. the assignValues method in testdatagenerator will matchup the hashmap to the name
            for (Object params : value) {
                HashMap detail = (HashMap) params;
                String varName = detail.get("paramName").toString();
                Object varType = detail.get("paramType").toString();
//                parameter builder, concatonates string for using in add statement.
                if (paramCall.isEmpty()) {
                    paramCall = varName;
                } else {
                    paramCall = paramCall + ", " + varName;
                }
//                add line to assign values to method parameters
                method.getBody().get().addStatement(new NameExpr(varType + " "+ varName+" = (" + varType + ") TestDataGenerator.assignValues(\"" + varName + "\", methodParams)"));
            }

//            try/catch is incase values outside methods normal range (40 used in 30 day month for example)
            method.getBody().get()
                    .addStatement(new NameExpr("try {"));
//            check if methd has parameters, if not just add loggers and assign to variable to get return results
            if (paramCall.isEmpty()) {
                method.getBody().get().addStatement(new NameExpr(key +"(coveredBranches, coveredConditions)"));
            } else {
                method.getBody().get().addStatement(new NameExpr("result = " + key + "(" + paramCall +", coveredBranches, coveredConditions)"));
            }
            method.getBody().get().addStatement(new NameExpr("} catch (Exception e) {"))
                    .addStatement(new NameExpr("System.out.println(e)"))
                    .addStatement(new NameExpr("System.out.println(\"Something went wrong passing values to function\")"))
                    .addStatement(new NameExpr("}"))
                    .addStatement(new NameExpr("}"));
        }

        method.getBody().get()
              //1  .addStatement(new NameExpr("}"))
                .addStatement(new NameExpr("return result"));


    }

    private static String addBranchLogger(Statement stmt) {
        ++branchCount;
        String branch = "TestDataGenerator.coveredBranch(" + branchCount + ", coveredBranches)";

        String methodName = traverseMethod(stmt);

        HashMap populate = methodBranches.get(methodName);


        HashMap<Integer,Pair<Boolean, Boolean>> h = new HashMap<>();

        for (Object i : populate.keySet()) {
            h.put((Integer) i,new Pair<>(false,false));
        }
        h.put(branchCount,new Pair<>(false,false));

        methodBranches.put(methodName,h);


        return branch;
    }

    private static String traverseMethod(Node stmt) {
        String metName = "";

        if (stmt.getParentNode().get().getClass().getSimpleName().equals("MethodDeclaration")) {
            MethodDeclaration m = (MethodDeclaration) stmt.getParentNode().get();
            metName = m.getNameAsString();
            return metName;
        } else {
            metName = traverseMethod(stmt.getParentNode().get());
        }

        return metName;
    }

    private static NameExpr addConditionLogger(Expression stmt) {

        NameExpr newCondition = new NameExpr();

        if (!stmt.getParentNode().isEmpty()) {
            conditionCount++;
            ifStmtLogs.put(conditionCount,stmt);

            String methodName = traverseMethod(stmt);

            HashMap populate = methodConditions.get(methodName);

            HashMap<Integer,Pair<Boolean, Boolean>> h = new HashMap<>();

            for (Object i : populate.keySet()) {
                h.put((Integer) i, new Pair<>(false,false));
            }
            h.put(conditionCount,new Pair<>(false,false));

            methodConditions.put(methodName,h);


            String condition = "TestDataGenerator.logCondition(" + conditionCount + ", " + stmt + ", coveredConditions)";
            newCondition = new NameExpr(condition);
        }

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

    public static void createTestCase(List item, String methodName, ClassOrInterfaceDeclaration myClass,
                                      int count, HashMap<String, List> methodDetails, String className, EnumDeclaration parsedEnum) {

    //setup method structure
        MethodDeclaration method = myClass.addMethod(methodName+count);
        method.addAnnotation("Test");
        method.setModifiers(Modifier.Keyword.PUBLIC).setType("void");

    //get common details
        HashMap methodHash = (HashMap) methodDetails.get(methodName).get(0);
        Object methodType = methodHash.get("methodType");
        HashMap testInfo = (HashMap) item.get(0);
        Object result = testInfo.get("result");

    // iterate and assign all variables to string
        String args = "";
        for (Object o: item) {
            HashMap h = (HashMap) o;
            if (args.isEmpty())
                args = h.get("value").toString();
            else
                args = args + ", " + h.get("value").toString();
        }


    //set class call and assign to a variable the result
        method.setBody(new BlockStmt());

//        check if enum present, if true then add class to methodType and assert statement
        if (parsedEnum == null) {
            method.getBody().get().addStatement(new NameExpr(
                    methodType + " var = " + className + "." + methodName + "(" + args + ")"

            ));
            //        assert equal to result
            method.getBody().get().addStatement(new NameExpr(
                    "assertEquals(" + result + ", " + "var)"
            ));
        } else {
            method.getBody().get().addStatement(new NameExpr(
                    className + "." + methodType + " var = " + className + "." + methodName + "(" + args + ")"
            ));
            //        assert equal to result
            method.getBody().get().addStatement(new NameExpr(
                    "assertEquals(" + className + "." + methodType + "." + result + ", " + "var)"
            ));
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

            HashMap<Integer, Pair<Boolean, Boolean>> setup = new HashMap();
//            create methodConditons used in testDataGenerator
            for (String name:methodNames) {
                methodConditions.put(name,setup);
                methodBranches.put(name,setup);
            }



//            all parameters found in method
            List<Parameter> methodParameters = md.getParameters();

//           parameter list builder
            List<HashMap> parameterList = new ArrayList<>();

//            populate parameter list details
            for(Parameter p:methodParameters){
                HashMap<String, Object> methodDetails = new HashMap<>();
                methodDetails.put("paramName", p.getName());
                methodDetails.put("paramType", p.getType());
                methodDetails.put("methodType",md.getType());
//                methodDetails.put("value", 0);
                parameterList.add(methodDetails);
            }
//            add parameter details and method name to main method list
            methodList.put(md.getNameAsString(), parameterList);

//            parse method instrumentations
            // add arg to list of parameters in all methods
            md.addParameter("Set<Integer>", "coveredBranches");
            md.addParameter("HashMap<Integer,Boolean>", "coveredConditions");


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
                n.addArgument("coveredConditions");
            }
            super.visit(n, methodNames);
        }
    }

    private static class WhileStmtParser extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(WhileStmt md, Void args) {
            super.visit(md, args);

            md.getBody().asBlockStmt().addStatement(0, new NameExpr(addBranchLogger(md)));

            if( md.getCondition().isBinaryExpr()) {
                recursiveConditionParser(md.getCondition().asBinaryExpr());
            } else {
                md.setCondition(addConditionLogger(md.getCondition()));
            }
        }

    }

    private static class EnumParser extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(EnumDeclaration md, Void args) {
            super.visit(md, args);

            parsedEnum = md;
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
            n.getThenStmt().asBlockStmt().addStatement(0, new NameExpr(addBranchLogger(n)));

            if( n.getCondition().isBinaryExpr()) {
                recursiveConditionParser(n.getCondition().asBinaryExpr());
            } else {
                n.setCondition(addConditionLogger(n.getCondition()));
            }
        }

        private static void parseElse(Statement n) {
            n.asBlockStmt().addStatement(0, new NameExpr(addBranchLogger(n)));
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

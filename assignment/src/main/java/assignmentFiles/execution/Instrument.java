package assignmentFiles.execution;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class Instrument {

    public static void parse(CompilationUnit cu) {

        // get class name
        List<String> className = new ArrayList<>();
        VoidVisitor<List<String>> classNameVisitor = new ClassNameCollector();
        classNameVisitor.visit(cu,className);

        // set param to instrument into class
        String[] param = {"Set<Integer>", "coveredBranches", "coveredBranch(1, coveredBranches)"};

        //parse methods in class
        VoidVisitor methodParser = new Instrument.MethodParser();
        methodParser.visit(cu, param);

        //parse if statements
        VoidVisitor ifStmtParser = new Instrument.IfStmtParser();
        ifStmtParser.visit(cu,param);

        // @todo parse while statements
        //      VoidVisitor whileStmtParser = new Instrument.WhileStmtParser();
        //      whileStmtParser.visit(cu,param);

        // write instrumented file
        writeInstrumentedFile(cu, className);


    }

    public static void writeInstrumentedFile(CompilationUnit cu, List<String> className) {
        String newName = "Instrumented"+className.get(0);
        String filePath = "src/main/java/assignmentFiles/instrumentedFiles/";
        ClassOrInterfaceDeclaration myClass = cu.getClassByName(className.get(0)).get();
        myClass.setName(newName);
        cu.setPackageDeclaration("assignmentFiles.instrumentedFiles");

        cu.addImport(new ImportDeclaration("java.util.TreeSet", false, false));
        cu.addImport(new ImportDeclaration("java.util.Set", false, false));

        WriteToFile.writeClass(cu.toString(),newName,filePath);

    }

//    https://stackoverflow.com/questions/65377062/javaparser-how-to-get-classname-in-compilationunit
    public static class ClassNameCollector extends VoidVisitorAdapter<List<String>>{
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {
            super.visit(n, collector);
            collector.add(n.getNameAsString());
        }
    }

    private static class MethodParser extends VoidVisitorAdapter<String[]> {
        @Override
        public void visit(MethodDeclaration md, String[] param) {
            super.visit(md, param);

            // add arg to list of parameters in all methods
            md.addParameter(param[0], param[1]);

            //add parameter arg to all method calls in class
            VoidVisitor methodCall = new Instrument.MethodCallVisitor();
            md.accept(methodCall,param);

        }
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<String[]> {
        @Override
        public void visit(MethodCallExpr n, String[] param) {
            n.addArgument(param[1]);
            super.visit(n, param);
        }
    }

    public static class IfStmtParser extends VoidVisitorAdapter<String[]> {
        @Override
        public void visit(IfStmt md, String[] param) {
            super.visit(md, param);

            convertIfStmt(md);

            parseIfStmt(md, param);

            if (md.hasElseBlock()){
                parseElse(md.getElseStmt().get(), param);
            }

        }

        private static void parseIfStmt(IfStmt n, String[] param) {
            n.getThenStmt().asBlockStmt().addStatement(0, new NameExpr(param[2]));
        }

        private static void parseElse(Statement n, String[] param) {
            n.asBlockStmt().addStatement(0, new NameExpr(param[2]));
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

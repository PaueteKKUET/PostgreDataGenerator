package com.pauete.Lienzo;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class DMLReader {

    private static final String CREATE_TABLE = "create table";

    private String[] tokens;
    private final Database database;

    public DMLReader() {
        this.database = new Database();
    }

    public Database getDatabase() {
        return this.database;
    }

    public void read(String dml) {

        // Quitar todos los comentarios
        dml = dml.replaceAll("/\\*.+\\*/","");

        dml = dml.replaceAll("(\n|\t)", " ");
        StringTokenizer tokens = new StringTokenizer(dml, ";");
        this.tokens = new String[tokens.countTokens()];
        int index = 0;
        while (tokens.hasMoreTokens()) {
            this.tokens[index] = tokens.nextToken().trim();
            index++;
        }
    }

    public Database generateDatabase() {
        if (this.tokens == null || this.tokens.length == 0) {
            throw new IllegalStateException("Reader is empty");
        }

        for (String token : this.tokens) {
            DMLAction action = getTokenAction(token);
            if (action == null) {
                continue;
            }

            switch (action) {
                case CREATE_TABLE -> {
                    this.database.addTable(createTable(token));
                }
            }
        }

        return null;

    }

    private @Nullable DMLAction getTokenAction(String token) {
        for (DMLAction action : DMLAction.values()) {
            if (action.matches(token)) {
                return action;
            }
        }
        return null;
    }

    private Table createTable(String token) {
        // Quitamos create table del token
        token = token.replaceFirst("create table ", "");

        // Sacamos el nombre de la tabla
        String tableName = token.replaceAll("\\(.*\\)$", "");

        // Limpiamos el token
        token = token.replaceFirst(tableName + "\\(", "").replaceFirst("\\)$", "").trim();

        // Creamos el contenedor que contendrá cada instrucción limpiamente
        ArrayList<String> statements = new ArrayList<>();

       //  Separamos las instrucciones individualmente
        {
            StringTokenizer statementTokens = new StringTokenizer(token, ",", true);
            String cache = "";
            int brackets = 0;
            while (statementTokens.hasMoreTokens()) {
                String statement = statementTokens.nextToken();
                if (statement.equals(",") && brackets == 0) {
                    continue;
                }
                for (char letter : statement.toCharArray()) {
                    if (letter == '(') {
                        brackets++;
                    } else if (letter == ')') {
                        brackets--;
                    }
                }
                cache += statement;
                if (brackets == 0) {
                    statements.add(cache.trim());
                    cache = "";
                }
            }
        }

        //Recogemos las constraints
        ConstraintSet constraints = new ConstraintSet();
        for (String statement : statements) {
            if (isConstraint(statement)) {

                // Quitamos la palabra clave "constraint"
                statement = statement.replaceFirst("constraint", "").trim();
                // Obtenemos el nombre de la constraint
                String constraintName = statement.replaceAll("\\s.*", "").trim();
                statement = statement.replaceFirst(constraintName, "").trim();
                // Limpiamos el nombre de la instrucción
                String key = statement.replaceFirst("\\(.*", "").trim();
                // Buscamos coincidencias de los tipos de clave conocidos
                Constraint.ConstraintType keyType = null;
                for (Constraint.ConstraintType type : Constraint.ConstraintType.values()) {
                    if (type.keyWord().equals(key)) {
                        keyType = type;
                    }
                }
                if (keyType == null) {
                    continue;
                }

                // Limpiamos la instrucción del tipo de clave y de posibles paréntesis.
                // Esto último no se hace si es una clave ajena.
                statement = statement.replaceFirst(keyType.keyWord(), "").trim();
                if (!keyType.equals(Constraint.ConstraintType.FK)) {
                    statement = statement.replaceFirst("\\(", "").replaceFirst("\\)$", "").trim();
                }

                // Construimos la constraint de una u otra forma según el tipo
                switch (keyType) {
                    case PK -> {
                        constraints.addConstraint(Constraint.primaryKey(constraintName, statement));
                    }
                    case FK -> {
                        String referredcolumn = statement.replaceFirst("\\(", "").replaceFirst("\\).*", "").trim();
                        statement = statement.replaceFirst(".*references","").trim();
                        String targetTable = statement.replaceFirst("\\(.*", "").trim();
                        String targetColumn = statement.replaceFirst(".*\\(", "").replaceAll("\\).*", "").trim();

                        constraints.addConstraint(Constraint.foreignKey(constraintName, referredcolumn, targetTable, targetColumn));
                    }
                    case UK -> {
                        constraints.addConstraint(Constraint.uniqueKey(constraintName, statement));
                    }
                    case CHECK -> {
                        constraints.addConstraint(Constraint.check(constraintName, statement));
                    }
                }
            }
        }

        // Comenzamos la creación del objeto tabla
        Table.Builder tabla = new Table.Builder()
                .setName(tableName)
                .addConstraint(constraints);

        // Creamos las columnas
        for (String statement : statements) {
            // Descartamos constraints
            if (isConstraint(statement)) {
                continue;
            }

            // Nombre de la columna
            String columnName = statement.substring(0, statement.indexOf(" "));

            // Limpiamos el nombre de la instrucción
            statement = statement.replaceFirst(columnName, "").trim();

            // Comprobamos la cláusula "not null"
            boolean isNotNull = statement.matches(".*not null.*");

            // Limpiamos la cláusula "not null", si existiera
            statement = statement.replaceAll("not null", "").trim();

            //Seleccionamos el tipo de dato
            String dataName = statement.replaceFirst("\\(.*", "");
            statement = statement
                    .replaceAll(dataName, "")
                    .replaceFirst("\\(", "")
                    .replaceFirst("\\)", "")
                    .trim();

            DataType dataType = null;

            switch (dataName.toUpperCase()) {
                case "VARCHAR" -> {
                    int numChars = Integer.parseInt(statement);
                    dataType = DataType.VARCHAR(numChars);
                }
                case "SERIAL" -> dataType = DataType.SERIAL();
                case "DATE" -> dataType = DataType.DATE();
                case "NUMERIC" -> {
                    if (statement.matches(".*,.*")) {
                        int integerDigits = Integer.parseInt(statement.replaceFirst(",.*", ""));
                        int decimalDigits = Integer.parseInt(statement.replaceFirst(".*,", ""));
                        dataType = DataType.NUMERIC(integerDigits, decimalDigits);
                    } else {
                        dataType = DataType.NUMERIC(Integer.parseInt(statement));
                    }
                }
                case "INTEGER" -> dataType = DataType.INTEGER();
            }

            tabla.addColumn(new Column(columnName, dataType, isNotNull));
        }



        return tabla.build();
    }

    private static boolean isConstraint(String statement) {
        return statement.toLowerCase().startsWith("constraint");
    }


    enum DMLAction {
        CREATE_TABLE("create table");

        private final String statement;

        DMLAction(String statement) {
            this.statement = statement;
        }

        private boolean matches(String statement) {
            return statement.toLowerCase().startsWith(this.statement);
        }

    }

}

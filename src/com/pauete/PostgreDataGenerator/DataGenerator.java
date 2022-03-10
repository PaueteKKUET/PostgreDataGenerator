package com.pauete.PostgreDataGenerator;

import com.pauete.PostgreDataGenerator.Exceptions.NoSuchTableException;

import java.util.ArrayList;

public class DataGenerator {

    private static final String INSERT_INTO = "insert into %table% values\n%data%;";
    private static final String DATA_PRESET = "(\n%data%)";

    private final Database database;

    public DataGenerator(Database database) {
        this.database = database;
    }


    public void generateData(String tableName, int entries) throws NoSuchTableException {
        Table table = database.getTableByName(tableName);
        StringBuilder data = new StringBuilder("insert into %table% values\n");
        for (int i = 0; i < entries; i++) {
            for (Column column : table.getColumns()) {

            }
        }

        // TODO: desarrollar esto
    }

    /**
     * Generates the preset for insert data into the specified table
     * @param tablename a vlaid table name
     * @return the preset
     * @throws NoSuchTableException if the table is not found
     */
    public String generatePreset(String tablename, int n) throws NoSuchTableException {
        Table table = this.database.getTableByName(tablename);
        String preset = INSERT_INTO.replaceAll("%table%", table.getName());
        StringBuilder data = new StringBuilder();

        ArrayList<String> statements = new ArrayList<>();
        for (Column column : table.getColumns()) {
            DataType type = column.getType();
            statements.add(type.defaultString().replace("%name%", column.getName()) + ",\n");
        }

        // Eliminar la coma del último
        statements.set(statements.size() - 1, statements.get(statements.size() - 1).replaceAll(",$.*", ""));

        for (String statement : statements) {
            data.append("\t" + statement);
        }

        String basedata = data.toString();
        data = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if(i != 0) {
                data.append(",\n");
            }
            data.append(DATA_PRESET.replaceFirst("%data%", basedata));
        }


        preset = preset.replaceAll("%data%", data.toString());

        return preset;
    }

    public String generateAllPresets(int n) {
        StringBuilder presets = new StringBuilder();
        for (Table t : database.getTables()) {
            try {
                presets.append(generatePreset(t.getName(), n) + "\n");
            } catch (NoSuchTableException e) {
                e.printStackTrace();
            }
            presets.append("\n");
        }

        return presets.toString();
    }

    public String generateAllPresets() {
        return generateAllPresets(1);
    }

}

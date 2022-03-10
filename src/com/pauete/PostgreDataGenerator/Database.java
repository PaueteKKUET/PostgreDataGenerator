package com.pauete.PostgreDataGenerator;

import com.pauete.PostgreDataGenerator.Exceptions.NoSuchTableException;

import java.util.ArrayList;

public class Database {

    private final ArrayList<Table> tables;

    public Database() {
        tables = new ArrayList<>();
    }

    public boolean isEmpty() {
        return tables.isEmpty();
    }

    public void addTable(Table table) {
        this.tables.add(table);
    }

    public void debug() {
        for (Table table : tables) {
            table.debug();
        }
    }

    public Table getTableByName(String name) throws NoSuchTableException {
        for (Table table : tables) {
            if (table.getName().equals(name.toUpperCase())) {
                return table;
            }
        }
        throw new NoSuchTableException();
    }

    public ArrayList<Table> getTables() {
        return this.tables;
    }

}

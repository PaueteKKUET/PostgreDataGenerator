package com.pauete.Lienzo;

import com.pauete.Lienzo.Table;

import java.util.ArrayList;

public class Database {

    private ArrayList<Table> tables;

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

}

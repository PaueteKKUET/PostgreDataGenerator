package com.pauete.PostgreDataGenerator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A Table object, a set of columns which can be used to generate data.
 */
public class Table {

    private final String name;
    private final ArrayList<Column> columns;
    private final ConstraintSet constraints;


    /**
     * Creates a table object. Use {@link Builder} in order to create a Table.
     * @param builder the builder
     */
    private Table(Builder builder) {
        this.name = builder.name;
        this.columns = builder.columns;
        this.constraints = builder.constraints;
    }

    public void debug() {
        System.out.println("Nombre de la tabla: " + this.name);
        for (Column column : this.columns) {
            System.out.println("\tColumna: " + column.getName());
            System.out.println("\t\tType: " + column.getType().format());
            System.out.println("\t\tNot null: " + column.isNotNull());
        }
        for (Constraint ctr : this.constraints) {
            System.out.println("\tConstraint:");
            System.out.println("\t\t" + ctr.format());
        }
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Column> getColumns() {
        return this.columns;
    }

    /**
     * Helper to create a table.
     */
    public static class Builder {

        private String name;
        private final ArrayList<Column> columns;
        private final ConstraintSet constraints;

        /**
         * Creates a table builder.
         */
        public Builder() {
            this.columns = new ArrayList<>();
            this.constraints = new ConstraintSet();
        }

        /**
         * Adds a column to the builder
         * @param column the column to add. Should not be null
         * @return the builder
         */
        public Builder addColumn(@NotNull Column column) {
            this.columns.add(column);
            return this;
        }

        /**
         * Sets the table name
         * @param name the name
         * @return the builder
         */
        public Builder setName(@NotNull String name) {
            if (name.equals("")) {
                throw new IllegalArgumentException("The name can not be empty");
            }
            this.name = name.toUpperCase();
            return this;
        }

        public Builder addConstraint(@NotNull Constraint ctr) {
            this.constraints.addConstraint(ctr);
            return this;
        }

        public Builder addConstraint(@NotNull ConstraintSet constraints) {
            for (Constraint ctr : constraints) {
                this.constraints.addConstraint(ctr);
            }
            return this;
        }

        /**
         * Builds the table
         * @return the table
         * @throws IllegalArgumentException if the builder has not any column added.
         */
        public Table build() {
            if (this.columns.size() == 0) {
                throw new IllegalStateException("Can not create an empty table");
            }
            if (this.name == null) {
                throw new IllegalStateException("Can not create an unnamed table");
            }
            if (!this.constraints.hasPublicKey()) {
                throw new IllegalStateException("Tables need a primary key");
            }
            return new Table(this);
        }

    }
}

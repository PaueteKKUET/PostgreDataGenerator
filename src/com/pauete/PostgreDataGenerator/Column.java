package com.pauete.PostgreDataGenerator;

import org.jetbrains.annotations.NotNull;

public class Column {

    private final String name;
    private final DataType type;
    private final boolean notNull;

    public Column(@NotNull String name, @NotNull DataType type, boolean isNotNull) {
        if (name.equals("")) {
            throw new IllegalArgumentException("Column name can not be empty");
        }

        this.name = name;
        this.type = type;
        this.notNull = isNotNull;
    }

    @NotNull public DataType getType() {
        return this.type;
    }

    @NotNull public String getName() {
        return this.name;
    }

    public boolean isNotNull() {
        return this.notNull;
    }

}

package com.pauete.PostgreDataGenerator;

public class Constraint implements Formatable {

    private final ConstraintType type;
    private final String constraintName;
    private final String referredColumn;

    private Constraint(ConstraintType type, String constraintName, String referredColumn) {
        this.type = type;
        this.constraintName = constraintName;
        this.referredColumn = referredColumn;
    }

    public ConstraintType getType() {
        return this.type;
    }

    public static Constraint primaryKey(String constraintName, String referredColumnName) {
        return new Constraint(ConstraintType.PK, constraintName, referredColumnName);
    }

    public static Constraint foreignKey(String constraintName, String referredColumnName, String targetTable, String targetColumn) {
        return new ForeignConstraint(constraintName, referredColumnName, targetTable, targetColumn);
    }

    public static Constraint uniqueKey(String constraintName, String referredColumnName){
        return new Constraint(ConstraintType.UK, constraintName, referredColumnName);
    }

    public static Constraint check(String constraintName, String condition) {
        return new Constraint(ConstraintType.CHECK, constraintName, condition);
    }

    @Override
    public String format() {
        return new StringBuilder()
                .append("constraint ")
                .append(this.constraintName)
                .append(" ")
                .append(this.type.keyWord)
                .append(" (")
                .append(referredColumn)
                .append(")")
                .toString();
    }

    enum ConstraintType {
        PK("primary key"),
        FK("foreign key"),
        UK("unique"),
        CHECK("check");

        private final String keyWord;

        ConstraintType(String keyWord) {
            this.keyWord = keyWord;
        }

        public String keyWord() {
            return this.keyWord;
        }
    }

    public static class ForeignConstraint extends Constraint {

        private final String targetTable;
        private final String targetColumn;

        public ForeignConstraint(String constraintName, String referredColumn, String targetTable, String targetColumn) {
            super(ConstraintType.FK, constraintName, referredColumn);
            this.targetTable = targetTable;
            this.targetColumn = targetColumn;
        }

        @Override
        public String format() {
            return new StringBuilder()
                    .append(super.format())
                    .append(" references ")
                    .append(this.targetTable)
                    .append(" (")
                    .append(targetColumn)
                    .append(")")
                    .toString();
        }
    }

}

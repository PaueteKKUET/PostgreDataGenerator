package com.pauete.PostgreDataGenerator;

public abstract class DataType {

    public static DataType VARCHAR(int length) {
        return Varchar.get(length);
    }
    public static DataType SERIAL() {
        return Serial.get();
    }
    public static DataType DATE() {
        return Date.get();
    }
    public static DataType NUMERIC(int integers) {
        return Numeric.get(integers);
    }
    public static DataType NUMERIC(int integers, int decimals) {
        return Numeric.get(integers, decimals);
    }
    public static DataType INTEGER() {
        return Integer.get();
    }

    public String format() {
        return this.getClass().getSimpleName();
    }
    public abstract String defaultString();



    private static class Varchar extends DataType {
        private final int length;

        public Varchar(int length) {
            this.length = length;
            if (length < 0) {
                throw new IllegalArgumentException("Length can not be less than 0");
            }
        }

        private static DataType get(int length) {
            return new Varchar(length);
        }

        @Override
        public String format() {
            return super.format() + "(" + this.length + ")";
        }

        @Override
        public String defaultString() {
            return "'%name%'";
        }
    }

    private static class Serial extends DataType {
        static final DataType SERIAL = new Serial();

        private static DataType get() {
            return SERIAL;
        }

        @Override
        public String defaultString() {
            return "DEFAULT";
        }
    }

    private static class Date extends DataType {
        static final DataType DATE = new Date();

        private static DataType get() {
            return DATE;
        }

        @Override
        public String defaultString() {
            return "'2000-01-31' <%name%>";
        }
    }

    private static class Numeric extends DataType {
        private final int[] numbers;

        private Numeric(int integers, int decimals) {
            this.numbers = new int[] { integers, decimals };
        }

        private static DataType get(int integers, int decimals) {
            return new Numeric(integers, decimals);
        }
        private static DataType get(int integers) {
            return get(integers, 0);
        }
        @Override
        public String format() {
            return super.format() + "(" + this.numbers[0] + (this.numbers[1] == 0 ? "" : "," + this.numbers[1]) + ")";
        }

        @Override
        public String defaultString() {
            return 0 + (this.numbers[1] == 0 ? "" : ".0") + " <%name%>";
        }
    }

    private static class Integer extends DataType {
        static final DataType INTEGER = new Integer();

        private static DataType get() {
            return INTEGER;
        }

        @Override
        public String defaultString() {
            return "0 <%name%>";
        }
    }


}

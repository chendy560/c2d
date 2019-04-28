package com.chendayu.c2d.processor.declaration;

public class Declarations {

    public static final Declaration STRING = new StringDeclaration();
    public static final Declaration NUMBER = new NumberDeclaration();
    public static final Declaration TIMESTAMP = new TimestampDeclaration();
    public static final Declaration BOOLEAN = new BooleanDeclaration();
    public static final Declaration ENUM_CONST = new EnumConstDeclaration();
    public static final Declaration DYNAMIC = new DynamicDeclaration();
    public static final Declaration VOID = new VoidDeclaration();
    public static final Declaration UNKNOWN = new UnknownDeclaration();
    public static final Declaration FILE = new FileDeclaration();


    private Declarations() {

    }

    public static ArrayDeclaration arrayOf(Declaration declaration) {
        return () -> declaration;
    }

    public static TypeArgDeclaration typeArgOf(String name) {
        return () -> name;
    }

    public interface ArrayDeclaration extends Declaration {

        @Override
        default DeclarationType getType() {
            return DeclarationType.ARRAY;
        }

        Declaration getComponentType();
    }

    public interface TypeArgDeclaration extends Declaration {

        @Override
        default DeclarationType getType() {
            return DeclarationType.TYPE_PARAMETER;
        }

        String getName();
    }

    private static final class StringDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.STRING;
        }
    }

    private static final class NumberDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.NUMBER;
        }
    }

    private static final class TimestampDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.TIMESTAMP;
        }
    }

    private static final class BooleanDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.BOOLEAN;
        }
    }

    private static final class EnumConstDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.ENUM_CONST;
        }
    }

    private static final class DynamicDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.DYNAMIC;
        }
    }

    private static final class VoidDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.VOID;
        }
    }

    private static final class UnknownDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.UNKNOWN;
        }
    }

    private static final class FileDeclaration implements Declaration {

        @Override
        public DeclarationType getType() {
            return DeclarationType.FILE;
        }
    }
}

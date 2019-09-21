package com.chendayu.c2d.processor.validation;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.chendayu.c2d.processor.validation.ValidationSupport.Constraint.createConstraint;

/**
 * 天知道为什么是这样的名字
 */
@SuppressWarnings("unchecked")
public class ValidationSupport {

    private static final Collection<Constraint> constraints;
    private static final Collection<Class<? extends Annotation>> annotations;

    static {
        Collection<Constraint> cc;
        if (ValidationApiV2.isEnabled()) {
            cc = ValidationApiV2.constraints();
        } else if (ValidationApiV1.isEnabled()) {
            cc = ValidationApiV1.getConstraints();
        } else {
            cc = Collections.emptyList();
        }

        constraints = Collections.unmodifiableCollection(cc);
        ArrayList<Class<? extends Annotation>> as = new ArrayList<>(constraints.size());
        constraints.forEach(c -> as.add(c.getAnnotationClazz()));
        annotations = Collections.unmodifiableCollection(as);
    }

    private ValidationSupport() {
        // just a static class
    }

    public static Collection<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    public static String getMessage(Annotation annotation) {
        for (Constraint<Annotation> constraint : constraints) {
            Class<? extends Annotation> clazz = constraint.getAnnotationClazz();
            if (clazz.isInstance(annotation)) {
                return constraint.getMessageFunction().apply(annotation);
            }
        }

        return null;
    }

    public static List<String> getMessages(Collection<Annotation> annotations) {
        if (annotations.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> result = new ArrayList<>(annotations.size());
        for (Annotation annotation : annotations) {
            String message = getMessage(annotation);
            if (message != null)
                result.add(message);
        }
        return result;
    }

    static class Constraint<A extends Annotation> {

        private final Class<A> annotationClazz;
        private final Function<A, String> messageFunction;

        public Constraint(Class<A> annotationClazz, Function<A, String> messageFunction) {
            this.annotationClazz = annotationClazz;
            this.messageFunction = messageFunction;
        }

        public static <T extends Annotation> Constraint<T> createConstraint(Class<T> clazz,
                                                                            Function<T, String> messageFunction) {
            return new Constraint<>(clazz, messageFunction);
        }

        public Class<A> getAnnotationClazz() {
            return annotationClazz;
        }

        public String getMessage(A annotation) {
            return messageFunction.apply(annotation);
        }

        public Function<A, String> getMessageFunction() {
            return messageFunction;
        }
    }

    private static class ValidationApiV1 {

        public static boolean isEnabled() {
            try {
                Class.forName("javax.validation.constraints.NotNull");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        public static Collection<Constraint> getConstraints() {

            return Arrays.asList(
                    createConstraint(AssertFalse.class, a -> "must be false"),
                    createConstraint(AssertTrue.class, a -> "must be true"),
                    createConstraint(DecimalMax.class, a -> {
                        if (a.inclusive()) {
                            return "must <= " + a.value();
                        } else {
                            return "must < " + a.value();
                        }
                    }),
                    createConstraint(DecimalMin.class, a -> {
                        if (a.inclusive()) {
                            return "must >= " + a.value();
                        } else {
                            return "must > " + a.value();
                        }
                    }),
                    createConstraint(Digits.class, a -> ""),
                    createConstraint(Future.class, a -> "must in the future"),
                    createConstraint(Max.class, a -> "must <= " + a.value()),
                    createConstraint(Min.class, a -> "must >= " + a.value()),
                    createConstraint(NotNull.class, a -> "must not null"),
                    createConstraint(Null.class, a -> "must be null"),
                    createConstraint(Past.class, a -> "must in the past"),
                    createConstraint(Pattern.class, a -> "must match: " + a.regexp()),
                    createConstraint(Size.class, a -> {
                        if (a.max() != Integer.MAX_VALUE) {
                            return "size/length must >= " + a.min() + " and <= " + a.max();
                        } else {
                            return "size/length must >= " + a.min();
                        }
                    }));
        }
    }

    private static class ValidationApiV2 {

        public static boolean isEnabled() {
            try {
                Class.forName("javax.validation.constraints.Email");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        public static Collection<Constraint> constraints() {
            List<Constraint> v2Constraints = Arrays.asList(
                    createConstraint(Email.class, a -> {
                        if (".*".equals(a.regexp())) {
                            return "must be a email address";
                        } else {
                            return "must be a email address and must match: " + a.regexp();
                        }
                    }),
                    createConstraint(FutureOrPresent.class, a -> "must be future or present"),
                    createConstraint(PastOrPresent.class, a -> "must be past or present"),
                    createConstraint(Negative.class, a -> "must < 0"),
                    createConstraint(NegativeOrZero.class, a -> "must <= 0"),
                    createConstraint(Positive.class, a -> "must > 0"),
                    createConstraint(PositiveOrZero.class, a -> "must >= 0"),
                    createConstraint(NotBlank.class, a -> "must not null or blank"),
                    createConstraint(NotEmpty.class, a -> "must not be null nor empty"));
            Collection<Constraint> v1Constrains = ValidationApiV1.getConstraints();

            int size = v1Constrains.size() + v2Constraints.size();
            ArrayList<Constraint> constraints = new ArrayList<>(size);
            constraints.addAll(v1Constrains);
            constraints.addAll(v2Constraints);
            return constraints;
        }
    }
}

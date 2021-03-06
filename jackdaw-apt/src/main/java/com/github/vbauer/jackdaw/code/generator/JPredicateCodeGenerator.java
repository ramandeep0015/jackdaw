package com.github.vbauer.jackdaw.code.generator;

import com.github.vbauer.jackdaw.annotation.JPredicate;
import com.github.vbauer.jackdaw.annotation.type.JPredicateType;
import com.github.vbauer.jackdaw.code.base.GeneratedCodeGenerator;
import com.github.vbauer.jackdaw.code.context.CodeGeneratorContext;
import com.github.vbauer.jackdaw.util.SourceCodeUtils;
import com.github.vbauer.jackdaw.util.TypeUtils;
import com.github.vbauer.jackdaw.util.callback.SimpleProcessorCallback;
import com.github.vbauer.jackdaw.util.function.AddSuffix;
import com.github.vbauer.jackdaw.util.model.ClassType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author Vladislav Bauer
 */

public class JPredicateCodeGenerator extends GeneratedCodeGenerator {

    private static final String SUFFIX = "Predicates";
    private static final AddSuffix NAME_MODIFIER = new AddSuffix(SUFFIX);

    private static final String CLASS_NAME = "Predicate";
    private static final String PACKAGE_GUAVA = "com.google.common.base";
    private static final String PACKAGE_JAVA = "java.util.function";


    public JPredicateCodeGenerator() {
        super(NAME_MODIFIER, ClassType.UTILITY);
    }


    @Override
    public Class<? extends Annotation> getAnnotation() {
        return JPredicate.class;
    }


    @Override
    protected void generateBody(
        final CodeGeneratorContext context, final TypeSpec.Builder builder
    ) throws Exception {
        final TypeElement typeElement = context.getTypeElement();
        SourceCodeUtils.processSimpleMethodsAndVariables(
            builder, typeElement, JPredicate.class,
            new SimpleProcessorCallback<JPredicate>() {
                @Override
                public void process(final TypeElement type, final String methodName, final JPredicate annotation) {
                    addPredicate(builder, typeElement, type, methodName, annotation);
                }
            }
        );
    }


    private void addPredicate(
        final TypeSpec.Builder builder, final TypeElement typeElement, final TypeElement type,
        final String methodName, final JPredicate annotation
    ) {
        if (TypeUtils.hasAnyType(type, Boolean.class, boolean.class)) {
            final JPredicateType functionType = annotation.type();
            final boolean reverse = annotation.reverse();
            final String operation = reverse ? "!" : StringUtils.EMPTY;
            final String packageName = getPredicatePackageName(functionType);

            builder.addField(
                FieldSpec.builder(
                    ParameterizedTypeName.get(
                        ClassName.get(packageName, CLASS_NAME),
                        TypeUtils.getTypeName(typeElement)
                    ),
                    SourceCodeUtils.normalizeName(methodName),
                    Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC
                )
                .initializer(
                    SourceCodeUtils.lines(
                        "new " + CLASS_NAME + "<$T>() {",
                            "public boolean apply(final $T input) {",
                                "return " + operation + "input.$L();",
                            "}",
                        "}"
                    ),
                    typeElement, typeElement, methodName
                )
                .build()
            );
        }
    }

    private String getPredicatePackageName(final JPredicateType type) {
        switch (type) {
            case GUAVA:
                return PACKAGE_GUAVA;
            case JAVA:
                return PACKAGE_JAVA;
            default:
                throw new UnsupportedOperationException();
        }
    }

}

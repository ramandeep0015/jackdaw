package com.github.vbauer.jackdaw.code.generator;

import com.github.vbauer.jackdaw.annotation.JComparator;
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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author Vladislav Bauer
 */

public class JComparatorCodeGenerator extends GeneratedCodeGenerator {

    private static final String SUFFIX = "Comparators";
    private static final AddSuffix NAME_MODIFIER = new AddSuffix(SUFFIX);

    private static final String CLASS_NAME = "Comparator";
    private static final String PACKAGE_JAVA = "java.util";

    private static final String PARAM_1 = "o1";
    private static final String PARAM_2 = "o2";


    public JComparatorCodeGenerator() {
        super(NAME_MODIFIER, ClassType.UTILITY);
    }


    @Override
    public Class<? extends Annotation> getAnnotation() {
        return JComparator.class;
    }


    @Override
    protected void generateBody(
        final CodeGeneratorContext context, final TypeSpec.Builder builder
    ) throws Exception {
        final TypeElement typeElement = context.getTypeElement();
        SourceCodeUtils.processSimpleMethodsAndVariables(
            builder, typeElement, JComparator.class,
            new SimpleProcessorCallback<JComparator>() {
                @Override
                public void process(final TypeElement type, final String methodName, final JComparator annotation) {
                    addFunction(builder, typeElement, type, methodName, annotation);
                }
            }
        );
    }


    private void addFunction(
        final TypeSpec.Builder builder, final TypeElement typeElement, final TypeElement type,
        final String methodName, final JComparator annotation
    ) {
        final boolean reverse = annotation.reverse();
        final String param1 = reverse ? PARAM_2 : PARAM_1;
        final String param2 = reverse ? PARAM_1 : PARAM_2;

        builder.addField(
            FieldSpec.builder(
                ParameterizedTypeName.get(
                    ClassName.get(PACKAGE_JAVA, CLASS_NAME),
                    TypeUtils.getTypeName(typeElement)
                ),
                SourceCodeUtils.normalizeName(methodName),
                Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC
            )
            .initializer(
                SourceCodeUtils.lines(
                    "new " + CLASS_NAME + "<$T>() {",
                        "public int compare(final $T " + param1 + ", final $T " + param2 + ") {",
                            "final $T v1 = o1 == null ? null : o1.$L();",
                            "final $T v2 = o2 == null ? null : o2.$L();",
                            "if (v1 == v2) {",
                                "return 0;",
                            "} else if (v1 == null) {",
                                "return -1;",
                            "} else if (v2 == null) {",
                                "return 1;",
                            "}",
                            "return v1.compareTo(v2);",
                        "}",
                    "}"
                ),
                typeElement, typeElement, typeElement,
                type, methodName,
                type, methodName
            )
            .build()
        );
    }

}


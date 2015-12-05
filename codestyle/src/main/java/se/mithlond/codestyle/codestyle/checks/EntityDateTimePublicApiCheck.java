/*
 * #%L
 * Nazgul Project: mithlond-codestyle
 * %%
 * Copyright (C) 2015 Mithlond
 * %%
 * Licensed under the jGuru Europe AB license (the "License"), based
 * on Apache License, Version 2.0; you may not use this file except
 * in compliance with the License.
 * 
 * You may obtain a copy of the License at
 * 
 *       http://www.jguru.se/licenses/jguruCorporateSourceLicense-2.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package se.mithlond.codestyle.codestyle.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.AbstractFormatCheck;
import com.puppycrawl.tools.checkstyle.utils.CheckUtils;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * <p>Calendar check which should be applied to model projects, implying two rules:</p>
 * <ol>
 * <li>The pre-JDK8 time types (Calendar, Date and GregorianCalendar) should not be
 * used in the public API of a class or interface. </li>
 * <li>JPA frameworks are not yet compliant with any timestamp class other than the pre-JDK8 types.
 * Hence, these types must be usable within the <strong>internal state</strong> of an Entity class.</li>
 * </ol>
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
public class EntityDateTimePublicApiCheck extends AbstractFormatCheck {

    /**
     * Reuse the messages for the IllegalType check in "messages.properties" file.
     */
    public static final String MSG_KEY = "illegal.type";

    /** Default value of pattern for illegal class name. */
    private static final String DEFAULT_FORMAT = "^(.*[\\.])?Abstract.*$";

    /** Abstract classes legal by default. */
    private static final String[] DEFAULT_LEGAL_ABSTRACT_NAMES = {};

    /** Types illegal by default. */
    private static final String[] DEFAULT_ILLEGAL_TYPES;

    static {

        final Map<String, String[]> plainTypesMap = new TreeMap<>();
        plainTypesMap.put("java.util.", new String[]{
                "HashSet",
                "HashMap",
                "LinkedHashMap",
                "LinkedHashSet",
                "TreeSet",
                "TreeMap",
                "Vector",
                "Date",
                "Calendar",
                "GregorianCalendar"});
        plainTypesMap.put("java.lang.", new String[]{
                "Double",
                "Float"});

        int numTypes = 0;
        for (String[] current : plainTypesMap.values()) {
            numTypes += current.length;
        }

        DEFAULT_ILLEGAL_TYPES = new String[numTypes * 2];
        int i = 0;
        for (Map.Entry<String, String[]> current : plainTypesMap.entrySet()) {

            final String packagePrefix = current.getKey();
            for (String currentPlainTypeName : current.getValue()) {
                DEFAULT_ILLEGAL_TYPES[2 * i] = currentPlainTypeName;
                DEFAULT_ILLEGAL_TYPES[2 * i + 1] = packagePrefix + currentPlainTypeName;
                i++;
            }
        }
    }

    /** Default ignored method names. */
    private static final String[] DEFAULT_IGNORED_METHOD_NAMES = {
            "getInitialContext",
            "getEnvironment",
    };

    /** Illegal classes. */
    private final Set<String> illegalClassNames = new HashSet<>();

    /** Legal abstract classes. */
    private final Set<String> legalAbstractClassNames = new HashSet<>();

    /** Methods which should be ignored. */
    private final Set<String> ignoredMethodNames = new HashSet<>();

    /** Check methods and fields with only corresponding modifiers. */
    private List<Integer> memberModifiers;

    /**
     * Controls whether to validate abstract class names.
     */
    private boolean validateAbstractClassNames;

    /** Creates new instance of the check. */
    public EntityDateTimePublicApiCheck() {
        super(DEFAULT_FORMAT);
        setIllegalClassNames(DEFAULT_ILLEGAL_TYPES);
        setLegalAbstractClassNames(DEFAULT_LEGAL_ABSTRACT_NAMES);
        setIgnoredMethodNames(DEFAULT_IGNORED_METHOD_NAMES);
    }

    /**
     * Sets whether to validate abstract class names.
     *
     * @param validateAbstractClassNames whether abstract class names must be ignored.
     */
    public void setValidateAbstractClassNames(boolean validateAbstractClassNames) {
        this.validateAbstractClassNames = validateAbstractClassNames;
    }

    /**
     * Defaults to the {@code TokenTypes.PARAMETER_DEF} and {@code TokenTypes.METHOD_DEF}.
     *
     * @return A default token set consisting of {@code TokenTypes.PARAMETER_DEF, TokenTypes.METHOD_DEF}.
     */
    @Override
    public int[] getDefaultTokens() {
        return new int[]{
                TokenTypes.PARAMETER_DEF,
                TokenTypes.METHOD_DEF
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getAcceptableTokens() {
        return new int[]{
                TokenTypes.VARIABLE_DEF,
                TokenTypes.PARAMETER_DEF,
                TokenTypes.METHOD_DEF,
                TokenTypes.IMPORT,
        };
    }

    /**
     * <p>No tokens are required for this Check.</p>
     * {@inheritDoc}
     */
    @Override
    public int[] getRequiredTokens() {
        return new int[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
                if (isVerifiable(ast)) {
                    visitMethodDef(ast);
                }
                break;
            case TokenTypes.VARIABLE_DEF:
                if (isVerifiable(ast)) {
                    visitVariableDef(ast);
                }
                break;
            case TokenTypes.PARAMETER_DEF:
                visitParameterDef(ast);
                break;
            case TokenTypes.IMPORT:
                visitImport(ast);
                break;
            default:
                throw new IllegalStateException(ast.toString());
        }
    }

    /**
     * Checks if current method's return type or variable's type is verifiable
     * according to <b>memberModifiers</b> option.
     *
     * @param methodOrVariableDef METHOD_DEF or VARIABLE_DEF ast node.
     * @return true if member is verifiable according to <b>memberModifiers</b> option.
     */
    private boolean isVerifiable(DetailAST methodOrVariableDef) {
        boolean result = true;
        if (memberModifiers != null) {
            final DetailAST modifiersAst = methodOrVariableDef.findFirstToken(TokenTypes.MODIFIERS);
            result = isContainVerifiableType(modifiersAst);
        }
        return result;
    }

    /**
     * Checks is modifiers contain verifiable type.
     *
     * @param modifiers parent node for all modifiers
     * @return true if method or variable can be verified
     */
    private boolean isContainVerifiableType(DetailAST modifiers) {
        boolean result = false;
        if (modifiers.getFirstChild() != null) {
            for (DetailAST modifier = modifiers.getFirstChild(); modifier != null;
                 modifier = modifier.getNextSibling()) {
                if (memberModifiers.contains(modifier.getType())) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Checks return type of a given method.
     *
     * @param methodDef method for check.
     */
    private void visitMethodDef(DetailAST methodDef) {
        if (isCheckedMethod(methodDef)) {
            checkClassName(methodDef);
        }
    }

    /**
     * Checks type of parameters.
     *
     * @param parameterDef parameter list for check.
     */
    private void visitParameterDef(DetailAST parameterDef) {
        final DetailAST grandParentAST = parameterDef.getParent().getParent();

        if (grandParentAST.getType() == TokenTypes.METHOD_DEF
                && isCheckedMethod(grandParentAST)) {
            checkClassName(parameterDef);
        }
    }

    /**
     * Checks type of given variable.
     *
     * @param variableDef variable to check.
     */
    private void visitVariableDef(DetailAST variableDef) {
        checkClassName(variableDef);
    }

    /**
     * Checks imported type (as static and star imports are not supported by Check,
     * only type is in the consideration).<br>
     * If this type is illegal due to Check's options - puts violation on it.
     *
     * @param importAst {@link TokenTypes#IMPORT Import}
     */
    private void visitImport(DetailAST importAst) {
        if (!isStarImport(importAst)) {
            final String canonicalName = getImportedTypeCanonicalName(importAst);
            extendIllegalClassNamesWithShortName(canonicalName);
        }
    }

    /**
     * <p>Checks if current import is star import. E.g.:</p>
     * <code>import java.util.*;</code>
     *
     * @param importAst {@link TokenTypes#IMPORT Import}
     * @return true if it is star import
     */
    private static boolean isStarImport(DetailAST importAst) {
        boolean result = false;
        DetailAST toVisit = importAst;
        while (toVisit != null) {
            toVisit = getNextSubTreeNode(toVisit, importAst);
            if (toVisit != null && toVisit.getType() == TokenTypes.STAR) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Checks type of given method, parameter or variable.
     *
     * @param ast node to check.
     */
    private void checkClassName(DetailAST ast) {
        final DetailAST type = ast.findFirstToken(TokenTypes.TYPE);
        final FullIdent ident = CheckUtils.createFullType(type);

        if (isMatchingClassName(ident.getText())) {
            log(ident.getLineNo(), ident.getColumnNo(), MSG_KEY, ident.getText());
        }
    }

    /**
     * @param className class name to check.
     * @return true if given class name is one of illegal classes
     * or if it matches to abstract class names pattern.
     */
    private boolean isMatchingClassName(String className) {
        final String shortName = className.substring(className.lastIndexOf('.') + 1);
        return illegalClassNames.contains(className)
                || illegalClassNames.contains(shortName)
                || validateAbstractClassNames
                && !legalAbstractClassNames.contains(className)
                && getRegexp().matcher(className).find();
    }

    /**
     * Extends illegal class names set via imported short type name.
     *
     * @param canonicalName <a href="http://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.7">
     *                      Canonical</a> name of imported type.
     */
    private void extendIllegalClassNamesWithShortName(String canonicalName) {
        if (illegalClassNames.contains(canonicalName)) {
            final String shortName = canonicalName
                    .substring(canonicalName.lastIndexOf('.') + 1);
            illegalClassNames.add(shortName);
        }
    }

    /**
     * Gets imported type's
     * <a href="http://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.7">
     * canonical name</a>.
     *
     * @param importAst {@link TokenTypes#IMPORT Import}
     * @return Imported canonical type's name.
     */
    private static String getImportedTypeCanonicalName(DetailAST importAst) {
        final StringBuilder canonicalNameBuilder = new StringBuilder();
        DetailAST toVisit = importAst;
        while (toVisit != null) {
            toVisit = getNextSubTreeNode(toVisit, importAst);
            if (toVisit != null && toVisit.getType() == TokenTypes.IDENT) {
                canonicalNameBuilder.append(toVisit.getText());
                final DetailAST nextSubTreeNode = getNextSubTreeNode(toVisit, importAst);
                if (nextSubTreeNode.getType() != TokenTypes.SEMI) {
                    canonicalNameBuilder.append('.');
                }
            }
        }
        return canonicalNameBuilder.toString();
    }

    /**
     * Gets the next node of a syntactical tree (child of a current node or
     * sibling of a current node, or sibling of a parent of a current node).
     *
     * @param currentNodeAst Current node in considering
     * @param subTreeRootAst SubTree root
     * @return Current node after bypassing, if current node reached the root of a subtree
     * method returns null
     */
    private static DetailAST getNextSubTreeNode(DetailAST currentNodeAst, DetailAST subTreeRootAst) {
        DetailAST currentNode = currentNodeAst;
        DetailAST toVisitAst = currentNode.getFirstChild();
        while (toVisitAst == null) {
            toVisitAst = currentNode.getNextSibling();
            if (toVisitAst == null) {
                if (currentNode.getParent().equals(subTreeRootAst)) {
                    break;
                }
                currentNode = currentNode.getParent();
            }
        }
        return toVisitAst;
    }

    /**
     * @param ast method def to check.
     * @return true if we should check this method.
     */
    private boolean isCheckedMethod(DetailAST ast) {
        final String methodName =
                ast.findFirstToken(TokenTypes.IDENT).getText();
        return !ignoredMethodNames.contains(methodName);
    }

    /**
     * Set the list of illegal variable types.
     *
     * @param classNames array of illegal variable types
     */
    public void setIllegalClassNames(String... classNames) {
        illegalClassNames.clear();
        Collections.addAll(illegalClassNames, classNames);
    }

    /**
     * Set the list of ignore method names.
     *
     * @param methodNames array of ignored method names
     */
    public void setIgnoredMethodNames(String... methodNames) {
        ignoredMethodNames.clear();
        Collections.addAll(ignoredMethodNames, methodNames);
    }

    /**
     * Set the list of legal abstract class names.
     *
     * @param classNames array of legal abstract class names
     */
    public void setLegalAbstractClassNames(String... classNames) {
        legalAbstractClassNames.clear();
        Collections.addAll(legalAbstractClassNames, classNames);
    }

    /**
     * Set the list of member modifiers (of methods and fields) which should be checked.
     *
     * @param modifiers String contains modifiers.
     */
    public void setMemberModifiers(String modifiers) {
        final List<Integer> modifiersList = new ArrayList<>();
        for (String modifier : modifiers.split(",")) {
            modifiersList.add(TokenUtils.getTokenId(modifier.trim()));
        }
        memberModifiers = modifiersList;
    }
}

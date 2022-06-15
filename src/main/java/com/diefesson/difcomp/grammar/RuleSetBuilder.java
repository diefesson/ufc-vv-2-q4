package com.diefesson.difcomp.grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.diefesson.difcomp.grammar.Element.*;

public class RuleSetBuilder {

    private final List<Rule> rules;

    public RuleSetBuilder() {
        rules = new ArrayList<>();
    }

    public RuleSet build() {
        checkRefs();
        return new RuleSet(rules);
    }

    public RuleSetBuilder emptyRule(String left) {
        checkLeft(left);
        rules.add(new Rule(variable(left), List.of(empty())));
        return this;
    }

    public RuleSetBuilder rule(String left, Object... right) {
        checkLeft(left);
        checkRight(right);
        List<Element> ruleRight = new ArrayList<>();
        for (Object r : right) {
            if (r instanceof String) {
                ruleRight.add(variable((String) r));
            } else if (r instanceof Integer) {
                ruleRight.add(terminal((Integer) r));
            }
        }
        rules.add(new Rule(variable(left), ruleRight));
        return this;
    }

    private static void checkLeft(String left) {
        if (left == null) {
            throw new IllegalArgumentException("left can't be null");
        }
    }

    private void checkRight(Object... right) {
        if (right.length == 0) {
            throw new IllegalArgumentException("right can't be empty");
        }
        for (Object r : right) {
            if (r == null) {
                throw new IllegalArgumentException("right can't contain null");
            }
            if (!(r instanceof String || r instanceof Integer)) {
                throw new IllegalArgumentException("right should contain only variable names and terminal token ids");
            }
        }
    }

    private void checkRefs() {
        Set<Element> leftVars = rules
                .stream()
                .map((r) -> r.left)
                .collect(Collectors.toSet());
        Set<Element> rightVars = rules
                .stream()
                .map((r) -> r.right())
                .flatMap((es) -> es.stream())
                .filter((e) -> e.type == ElementType.VARIABLE)
                .collect(Collectors.toSet());
        Set<Element> missingOnRight = new HashSet<>(leftVars);
        missingOnRight.removeAll(rightVars);
        missingOnRight.remove(rules.get(0).left); // Don't consider the start rule
        Set<Element> missingOnLeft = new HashSet<>(rightVars);
        missingOnLeft.removeAll(leftVars);
        if (!missingOnRight.isEmpty()) {
            String vars = missingOnRight
                    .stream()
                    .map((v) -> v.variable)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Variable(s) %s are never generated".formatted(vars));
        }
        ;
        if (!missingOnLeft.isEmpty()) {
            String vars = missingOnLeft
                    .stream()
                    .map((v) -> v.variable)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Variable(s) %s are not defined".formatted(vars));
        }
    }

}

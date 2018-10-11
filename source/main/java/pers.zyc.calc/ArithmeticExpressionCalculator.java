package pers.zyc.calc;


import pers.zyc.tools.utils.Regex;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 四则运算计算器
 */
public class ArithmeticExpressionCalculator {

    private enum CharacterType {

        /**
         * 数字
         */
        NUMBER("[1-9]\\d*\\.?\\d*") {
            @Override
            Character createCharacter(String character) {
                return new Character(this, Double.valueOf(character));
            }
        },
        /**
         * 运算符
         */
        OPERATOR("[\\+\\-\\*\\/]") {
            @Override
            Character createCharacter(String character) {
                return new OperatorCharacter(character);
            }
        },
        /**
         * 左括号
         */
        LP("\\(") {
            @Override
            Character createCharacter(String character) {
                return new Character(this, character);
            }
        },
        /**
         * 右括号
         */
        RP("\\)") {
            @Override
            Character createCharacter(String character) {
                return new Character(this, character);
            }
        };
        Pattern pattern;

        CharacterType(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        abstract Character createCharacter(String character);

        static CharacterType parse(String character) {
            for (CharacterType type : values()) {
                if (type.pattern.matcher(character).matches()) {
                    return type;
                }
            }
            throw new UnsupportCharacterException(character);
        }

        static Character newCharacter(String character) {
            return parse(character).createCharacter(character);
        }
    }

    /**
     * 算术表达式字符, 包括数字、四则运算符和左右括号
     */
    private static class Character {
        private CharacterType type;
        private Object value;

        private Character(CharacterType type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Character{" +
                    "type=" + type +
                    ", value='" + value +
                    "'}";
        }
    }

    private static class OperatorCharacter extends Character {
        private char operator;
        private OperatorCharacter(String value) {
            super(CharacterType.OPERATOR, value);
            this.operator = value.charAt(0);
        }

        /**
         * 优先于
         */
        boolean priorTo(OperatorCharacter operator) {
            return (this.operator == SimpleCalculator.MULTIPLY.operator ||
                    this.operator == SimpleCalculator.DIVIDE.operator) &&
                    (operator.operator == SimpleCalculator.ADD.operator ||
                            operator.operator == SimpleCalculator.SUBTRACT.operator);
        }
    }

    /**
     * 加减乘除计算
     */
    private enum SimpleCalculator {
        ADD('+') {
            @Override
            double calc(double d1, double d2) {
                return d1 + d2;
            }
        },
        SUBTRACT('-') {
            @Override
            double calc(double d1, double d2) {
                return d1 - d2;
            }
        },
        MULTIPLY('*') {
            @Override
            double calc(double d1, double d2) {
                return d1 * d2;
            }
        },
        DIVIDE('/') {
            @Override
            double calc(double d1, double d2) {
                return d1 / d2;
            }
        };
        private char operator;

        SimpleCalculator(char operator) {
            this.operator = operator;
        }

        abstract double calc(double d1, double d2);

        static double simpleCalc(double d1, double d2, char operator) {
            for (SimpleCalculator calculator : values()) {
                if (calculator.operator == operator) {
                    return calculator.calc(d1, d2);
                }
            }
            throw new UnsupportCharacterException(String.valueOf(operator));
        }
    }

    private static class PostfixCalculator {
        LinkedList<Double> postfix = new LinkedList<>();

        void inNumber(Character number) {
            postfix.offer((Double) number.value);
        }

        /**
         * 每次遇到一个运符便可计算出结果并且此结果就是中间子表达式的值
         * 当表达式读完(最后一个算符进入)最后一个计算结果也就是整个表达式的结果
         * @param operator 算符
         */
        void inOperator(OperatorCharacter operator) {
            double d2 = postfix.removeLast();
            double d1 = postfix.removeLast();
            postfix.offer(SimpleCalculator.simpleCalc(d1, d2, operator.operator));
        }

        /**
         * 返回当前表达式结果
         */
        double getResult() {
            return postfix.removeLast();
        }
    }

    /**
     * 转换为后缀表达式同时计算结果
     * @param expression 算术表达式
     * @return 表达式结果
     */
    private static double parseToPostfixExpressionAndCalc(String expression) {
        Matcher matcher = Regex.ARITHMETIC_EXPRESSION_CHARACTER.pattern().matcher(expression);
        LinkedList<Character> operators = new LinkedList<>();
        PostfixCalculator postfixCalculator = new PostfixCalculator();
        while (matcher.find()) {
            String group = matcher.group();
            if (group == null || group.trim().isEmpty()) {
                continue;
            }
            Character character = CharacterType.newCharacter(group);
            switch (character.type) {
                case LP:
                    operators.offer(character);
                    break;
                case RP:
                    while (operators.getLast().type != CharacterType.LP) {
                        postfixCalculator.inOperator((OperatorCharacter) operators.removeLast());
                    }
                    operators.removeLast();//移除左括号
                    break;
                case NUMBER:
                    postfixCalculator.inNumber(character);
                    break;
                case OPERATOR:
                    Character topOperator;//栈顶操作符
                    while (true) {
                        if (operators.isEmpty() ||
                                (topOperator = operators.getLast()).type == CharacterType.LP ||
                                //不是左括号则一定是运算符
                                ((OperatorCharacter) character).priorTo((OperatorCharacter) topOperator)) {
                            operators.offer(character);
                            break;
                        } else {
                            postfixCalculator.inOperator((OperatorCharacter) operators.removeLast());
                        }
                    }
                    break;
                default:
                    throw new UnsupportCharacterException(group);
            }
        }
        while (!operators.isEmpty()) {
            postfixCalculator.inOperator((OperatorCharacter) operators.removeLast());
        }
        return postfixCalculator.getResult();
    }

    /**
     * 计算四则运算表达式结果
     *
     * @param expression 表达式
     * @return 计算结果double
     * @throws IllegalArgumentException 不合法的表达式
     * @throws UnsupportCharacterException 非法字符时抛出
     */
    public static double calc(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return parseToPostfixExpressionAndCalc(expression);
    }
}
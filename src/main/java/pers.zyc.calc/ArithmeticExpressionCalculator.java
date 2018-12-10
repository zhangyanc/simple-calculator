package pers.zyc.calc;


import pers.zyc.tools.utils.Pair;
import pers.zyc.tools.utils.Regex;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 四则运算计算器
 */
public class ArithmeticExpressionCalculator {

	private enum CharacterType {

		NUMBER("[1-9]\\d*\\.?\\d*"),
		OPERATOR("[\\+\\-\\*\\/]"),
		LP("\\("),
		RP("\\)");

		final Pattern pattern;

		CharacterType(String pattern) {
			this.pattern = Pattern.compile(pattern);
		}

		static CharacterType parse(String character) {
			for (CharacterType type : values()) {
				if (type.pattern.matcher(character).matches()) {
					return type;
				}
			}
			throw new CalcException("Unsupported character: " + character);
		}
	}

	/**
	 * 算术表达式字符, 包括数字、四则运算符和左右括号
	 */
	private static class Character extends Pair<CharacterType, Object> {

		Character(String character) {
			CharacterType type = CharacterType.parse(character);
			key(type);
			if (type == CharacterType.NUMBER) {
				value(Double.parseDouble(character));
			} else if (type == CharacterType.OPERATOR) {
				value(character.charAt(0));
			} else {
				value(character);
			}
		}
	}

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

		final char operator;

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
			throw new CalcException("Unsupported operator: " + String.valueOf(operator));
		}
	}

	/**
	 * 乘除优先级高于加减
	 */
	private static boolean priorTo(char op1, char op2) {
		return (op1 == SimpleCalculator.MULTIPLY.operator || op1 == SimpleCalculator.DIVIDE.operator) &&
				(op2 == SimpleCalculator.ADD.operator || op2 == SimpleCalculator.SUBTRACT.operator);
	}

	private static class PostfixCalculator {
		final LinkedList<Double> numbers = new LinkedList<>();
		final LinkedList<Character> operators = new LinkedList<>();

		/**
		 * 计算栈顶表达式值，并将结果入栈
		 */
		void calc() {
			double d2 = numbers.removeLast();
			double d1 = numbers.removeLast();
			numbers.offer(SimpleCalculator.simpleCalc(d1, d2, (char) operators.removeLast().value()));
		}

		double getResult() {
			while (!operators.isEmpty()) {
				calc();
			}
			return numbers.removeLast();
		}

		void receive(Character character) {
			switch (character.key()) {
				case LP:
					operators.offer(character);
					break;
				case NUMBER:
					numbers.offer((Double) character.value());
					break;
				case RP:
					//计算括号内的值
					while (operators.getLast().key() != CharacterType.LP) {
						calc();
					}
					operators.removeLast();
					break;
				case OPERATOR:
					Character topOperator;
					while (true) {
						if (operators.isEmpty() ||
								(topOperator = operators.getLast()).key() == CharacterType.LP ||
								//不是左括号则一定是运算符
								priorTo((char) character.value(), (char) topOperator.value())) {
							operators.offer(character);
							break;
						} else {
							calc();
						}
					}
					break;
			}
		}
	}

	/**
	 * 转换为后缀表达式同时计算结果
	 *
	 * @param expression 算术表达式
	 * @return 结果
	 */
	public static double calc(String expression) {
		Matcher matcher = Regex.ARITHMETIC_EXPRESSION_CHARACTER.pattern().matcher(expression);
		PostfixCalculator postfixCalculator = new PostfixCalculator();
		while (matcher.find()) {
			String character = matcher.group();
			if (character == null || character.trim().isEmpty()) {
				continue;
			}
			postfixCalculator.receive(new Character(character));
		}
		return postfixCalculator.getResult();
	}
}
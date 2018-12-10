package pers.zyc.calc;

import org.junit.Assert;
import org.junit.Test;

public class CalcTest {

	@Test
	public void case0() {
		String[] expressions = {
				"345 - 6",
				"34.5 - 6.0",
				"3 + 4 * 5 - 6",
				"(3 + 4) * 5 - 6",
				"3 + 4 * (5 - 6)",
				"3 + 4 * 5 - 6 / 2",
				"(3 + 4 * 5) - 6 / 2",
				"3 + (4 * 5 - 6) / 2",
				"3 + 4 * (5 - 6 / 2)",
				"3 + (4 * 5 - 6 / 2)",
				"3 + (4 * (5 - 6) / 2)",
				"((3 + 4) * 5) - 6 / 2",
				"((3 + 4) * 5 - 6) / 2",
                "(1 + 2 * (3 - (4 / (5 + 6))) * 7) - 8 / 9"
		};

		for (String expression : expressions) {
            Assert.assertTrue(Calculator.calc(expression) == ArithmeticExpressionCalculator.calc(expression));
        }
	}
}

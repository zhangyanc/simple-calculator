package pers.zyc.calc;

import org.junit.Assert;
import org.junit.Test;

public class CalcTest {

    @Test
    public void case0() {
        Assert.assertTrue(339 == ArithmeticExpressionCalculator.calc("345 - 6"));
        Assert.assertTrue(28.5 == ArithmeticExpressionCalculator.calc("34.5 - 6.0"));


        Assert.assertTrue(17 == ArithmeticExpressionCalculator.calc("3 + 4 * 5 - 6"));
        Assert.assertTrue(17 == ArithmeticExpressionCalculator.calc("3 + (4 * 5) - 6"));
        Assert.assertTrue(29 == ArithmeticExpressionCalculator.calc("(3 + 4) * 5 - 6"));
        Assert.assertTrue(-1 == ArithmeticExpressionCalculator.calc("3 + 4 * (5 - 6)"));

        Assert.assertTrue(20 == ArithmeticExpressionCalculator.calc("3 + 4 * 5 - 6 / 2"));
        Assert.assertTrue(20 == ArithmeticExpressionCalculator.calc("(3 + 4 * 5) - 6 / 2"));
        Assert.assertTrue(10 == ArithmeticExpressionCalculator.calc("3 + (4 * 5 - 6) / 2"));
        Assert.assertTrue(11 == ArithmeticExpressionCalculator.calc("3 + 4 * (5 - 6 / 2)"));
        Assert.assertTrue(8.5 == ArithmeticExpressionCalculator.calc("(3 + 4 * 5 - 6) / 2"));
        Assert.assertTrue(20 == ArithmeticExpressionCalculator.calc("3 + (4 * 5 - 6 / 2)"));

        Assert.assertTrue(1 == ArithmeticExpressionCalculator.calc("3 + (4 * (5 - 6) / 2)"));
        Assert.assertTrue(32 == ArithmeticExpressionCalculator.calc("((3 + 4) * 5) - 6 / 2"));
        Assert.assertTrue(14.5 == ArithmeticExpressionCalculator.calc("((3 + 4) * 5 - 6) / 2"));

        System.out.println(ArithmeticExpressionCalculator.calc("1+2*3-4/5+6*7-8/9"));
        System.out.println(ArithmeticExpressionCalculator.calc("(1+2*(3-(4/(5+6)))*7)-8/9"));
    }
}

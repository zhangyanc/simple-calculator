package pers.zyc.calc;

/**
 * @author zhangyancheng
 */
public class CalcException extends RuntimeException {

	CalcException(String message) {
		super(message);
	}

	CalcException(Throwable cause) {
		super(cause);
	}
}
